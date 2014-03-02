package hr.fer.tel.ruazosa.shopshop;

import hr.fer.tel.ruazosa.shopshop.R;
import hr.fer.tel.ruazosa.shopshop.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

/**
 * Service used to synchronize lists with Dropbox. Creates a ShopShop folder at Dropbox
 * root directory in which all lists are saved as XML files.
 *
 */
public class DropboxSyncService extends Service {
	private static final String TAG = "DbxSyncService";
	private SharedPreferences prefs;
	private int frequency;
	private boolean running;

	@Override
	public void onCreate() {
		Log.i(TAG, "Service Started!");
		Util.showToast(getApplicationContext(), "Sync Service Started.");
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		running = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Thread thread = new Thread()
		{
			@Override
			public void run() {
				try {
					while(running) {
						try {
							Log.i(TAG, "Service Running!");

							// Check if ShopShop folder exists
							try{
								DropboxConnect.mApi.metadata("/ShopShop/", 0, null, false, null);
								Log.i(TAG, "ShopShop folder exists!");
							}catch(Exception e){
								Log.i(TAG, "Creating Folder: ShopShop");
								DropboxConnect.mApi.createFolder("/ShopShop");
							}

							syncExistingLists();

							uploadNonExistingLists();

							downloadNonExistingLists();

						} catch (Exception e) {
							e.printStackTrace();
						}
						
						// Show notification
						Intent intent = new Intent(getApplicationContext(), Main.class);
						PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

						@SuppressWarnings("deprecation")
						Notification n  = new Notification.Builder(getApplicationContext())
						        .setContentTitle("ShopShop")
						        .setContentText("Lists are synchronized.")
						        .setSmallIcon(R.drawable.ic_launcher)
						        .setContentIntent(pIntent)
						        .setAutoCancel(true).getNotification();
						  
						NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						notificationManager.notify(0, n); 
						
						Log.i(TAG, "Service done.");
						
						// Sleep until next sync.
						frequency = Integer.parseInt(prefs.getString("syncFrequency", "10"));
						sleep(frequency * 1000);
					}
				}catch(Exception e){

				}
			}
		};

		thread.start();
		return START_STICKY;
	}

	//Upload files from internal storage that don't exist in dropbox
	private void uploadNonExistingLists(){
		Log.i(TAG, "Uploading non existing lists do dropbox...");
		FileInputStream is = null;
		File f = getFilesDir();
		File[] files = f.listFiles();
		for (File file : files) {
			String filename = file.getName();
			boolean exists = dbxFileExists(filename);
			if (!exists){
				if (dbxFileIsDeleted(filename) && !prefs.getBoolean(filename + "created", false)){
					File dir = getFilesDir();
					File fd = new File(dir, filename);
					fd.delete();
				}else{
					try {
						is = new FileInputStream(file);
						DropboxConnect.mApi.putFile("/ShopShop/" + filename + ".shopshop", is, file.length(), null, null);
						prefs.edit().putString(filename, getFileRev(filename)).commit();
						prefs.edit().putBoolean(filename + "created", false).commit();
						Log.i(TAG, "List '" + filename + "' added to dropbox.");
						is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	//Download files from dropbox that don't exist in internal storage
	private void downloadNonExistingLists(){
		Log.i(TAG, "Downloading non existing lists from dropbox...");
		Entry entry = null;
		try {
			entry = DropboxConnect.mApi.metadata("/ShopShop", 1000, null, true, null);
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		for (Entry ent: entry.contents) {
			String filename = Util.stripExtension("shopshop", ent.fileName());
			boolean exists = fileExistsInInternalStorage(filename);
			if (!exists){
				if (prefs.getBoolean(filename + "deleted", false)){
					try {
						DropboxConnect.mApi.delete("/ShopShop/" + filename + ".shopshop");
					} catch (DropboxException e) {
						e.printStackTrace();
					}
					prefs.edit().putBoolean(filename + "deleted", false).commit();
				} else{
					try {
						FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
						DropboxConnect.mApi.getFile(ent.path, null, fos, null);
						prefs.edit().putString(filename, getFileRev(filename)).commit();
						Log.i(TAG, "List '" + filename + "' downloaded to internal storage.");
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	//Synchronize files that exist in internal storage and in dropbox
	private void syncExistingLists(){
		Log.i(TAG, "Syncing existing lists...");
		File f = getFilesDir();
		File[] files = f.listFiles();
		for (File file : files) {
			String filename = file.getName();
			boolean exists = dbxFileExists(filename);
			if (exists){
				Entry entry = null;
				try {
					entry = DropboxConnect.mApi.metadata("/ShopShop/" + filename + ".shopshop", 1000, null, true, null);
				} catch (DropboxException e) {
					e.printStackTrace();
				}
				if (!entry.rev.equals(prefs.getString(filename, "NULL"))){ // If dropbox list was changed
					try {
						FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
						DropboxConnect.mApi.getFile(entry.path, null, fos, null);
						prefs.edit().putString(filename, getFileRev(filename)).commit();
						Log.i(TAG, "List '" + filename + "' downloaded to internal storage.");
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (prefs.getBoolean(filename + "changed", true)){ // If internal storage list was changed
					FileInputStream inputStream = null;
					try {
						inputStream = new FileInputStream(file);
						DropboxConnect.mApi.putFileOverwrite("/ShopShop/" + filename + ".shopshop", inputStream, file.length(), null);
						prefs.edit().putString(filename, getFileRev(filename)).commit();
						Util.listChangesCommited(filename, getApplicationContext());
						Log.i(TAG, "List '" + filename + "' uploaded to dropbox.");
						inputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	private boolean dbxFileExists(String name){
		Entry entry = null;
		try {
			entry = DropboxConnect.mApi.metadata("/ShopShop", 1000, null, true, null);
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		for (Entry ent: entry.contents) {
			String filename = Util.stripExtension("shopshop", ent.fileName());
			if (name.equals(filename))
				return true;
		}
		return false;
	}
	
	private boolean dbxFileIsDeleted(String name){
		Entry entry = null;
		try {
			entry = DropboxConnect.mApi.metadata("/ShopShop/" + name + ".shopshop", 1000, null, true, null);
			if (entry.isDeleted)
				return true;
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean fileExistsInInternalStorage(String filename){
		File f = getFilesDir();
		File[] files = f.listFiles();
		for (File file : files) {
			if (filename.equals(file.getName()))
				return true;
		}
		return false;
	}

	private String getFileRev (String filename){
		Entry entry = null;
		try {
			entry = DropboxConnect.mApi.metadata("/ShopShop/" + filename + ".shopshop", 1000, null, true, null);
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		return entry.rev;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		running = false;
		Log.i(TAG, "Service Destroyed!");
		Util.showToast(getApplicationContext(), "Sync Service Stopped.");
	}

}
