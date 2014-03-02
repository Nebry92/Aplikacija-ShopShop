package hr.fer.tel.ruazosa.shopshop.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.Toast;
import static java.text.DateFormat.*;

/**
 * Class that contains methods used in rest of the application.
 *
 */
public class Util {

	// Prikaz Toast poruke.
	public static void showToast(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	// Formatiranje oblika datuma.
	public static String getLocalDate(Date date){
		DateFormat f = DateFormat.getDateTimeInstance(DEFAULT, DEFAULT, Locale.ENGLISH);
		return f.format(date);
	}

	// Izracun suprotne boje.
	public static int getOppositeColor(int color){
		int opposite = Color.rgb(255-Color.red(color),
				255-Color.green(color),
				255-Color.blue(color));
		return opposite;
	}

	// Brisanje ekstenzije sa imena file-a
	public static String stripExtension(String extension, String filename) {
		extension = "." + extension;
		if (filename.endsWith(extension)) {
			return filename.substring(0, filename.length() - extension.length());
		}
		return filename;
	}
	
	// Bilježi da je lista mijenjana kako bi se promjene uploadale na Dropbox
	public static void listChanged(String listName, Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putBoolean(listName + "changed", true).commit();
	}
	
	// Bilježi da su promjene liste uploadane na Dropbox
	public static void listChangesCommited(String listName, Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().putBoolean(listName + "changed", false).commit();
	}

}
