package hr.fer.tel.ruazosa.shopshop;

import hr.fer.tel.ruazosa.shopshop.R;
import hr.fer.tel.ruazosa.shopshop.util.AccountInfo;
import hr.fer.tel.ruazosa.shopshop.util.Util;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

/**
 * Activity used to connect application with Dropbox. User must provide dropbox username
 * and password.
 *
 */
public class DropboxConnect extends Activity{
	private static final String TAG = "DbxConnect";

	final static private String APP_KEY = "jrdtwl5wsibu5rp";
	final static private String APP_SECRET = "qp43rr11sqotaql";

	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;

	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	public static DropboxAPI<AndroidAuthSession> mApi;

	private boolean linked;
	private boolean serviceStarted;

	private Button linkToDbxBtn;
	private Button startSyncBtn;
	
	private TextView linkId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Activity started!");
		setContentView(R.layout.dbx_connect);

		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI<AndroidAuthSession>(session);

		linkToDbxBtn = (Button) findViewById(R.id.linkToDbxBtn);
		startSyncBtn = (Button) findViewById(R.id.syncBtn);
		linkId = (TextView) findViewById(R.id.linkId);

		linkToDbxBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// This logs you out if you're logged in, or vice versa
				if (linked) {
					// Destroy Service
					stopService(new Intent(DropboxConnect.this, DropboxSyncService.class));
					logOut();
				} else{
					// Start the remote authentication
					mApi.getSession().startAuthentication(DropboxConnect.this);
				}
				setServiceStatus(isMyServiceRunning());
			}
		});

		startSyncBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!serviceStarted && linked){
					// Start Sync Service
					startService(new Intent(DropboxConnect.this, DropboxSyncService.class));
				} else if (serviceStarted){
					// Destroy Service
					stopService(new Intent(DropboxConnect.this, DropboxSyncService.class));
				} else if (!linked){
					Util.showToast(getApplicationContext(), "Please link with Dropbox first");
				}
				setServiceStatus(isMyServiceRunning());
			}
		});

		setServiceStatus(isMyServiceRunning());
		setLoggedIn(mApi.getSession().isLinked());

	} // End onCreate.

	private void setServiceStatus(boolean running){
		serviceStarted = running;
		if (running){
			startSyncBtn.setText("Stop Sync Service");
		} else if (!running){
			startSyncBtn.setText("Start Sync Service");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "Activity resumed!");
		AndroidAuthSession session = mApi.getSession();

		if (session.authenticationSuccessful()) {
			try {
				session.finishAuthentication();
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				setLoggedIn(true);
				Util.showToast(getApplicationContext(), "Linked with Dropbox");
			} catch (IllegalStateException e) {
				Util.showToast(getApplicationContext(), "Couldn't authenticate with Dropbox" + e.getLocalizedMessage());
				Log.i(TAG, "Error authenticating", e);
			}
		}
	}

	private void logOut() {
		mApi.getSession().unlink();
		clearKeys();
		setLoggedIn(false);
		Util.showToast(getApplicationContext(), "Unlinked from Dropbox");
	}

	private void setLoggedIn(boolean loggedIn) {
		linked = loggedIn;
		if (loggedIn) {
			linkToDbxBtn.setText("Unlink from Dropbox");
			linkId.setVisibility(View.VISIBLE);
			AccountInfo ai = new AccountInfo();
			ai.start();
			try {
				ai.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String accInfo = ai.getAccInfo();
			linkId.setText("Linked as \"" + accInfo + "\"");
		} else {
			linkToDbxBtn.setText("Link with Dropbox");
			linkId.setVisibility(View.GONE);
		}
	}

	private String[] getKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key != null && secret != null) {
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			return null;
		}
	}

	private void storeKeys(String key, String secret) {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}
		return session;
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (DropboxSyncService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
