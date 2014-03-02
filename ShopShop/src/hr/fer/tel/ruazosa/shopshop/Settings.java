package hr.fer.tel.ruazosa.shopshop;

import hr.fer.tel.ruazosa.shopshop.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * Settings Activity. Extends Preference Activity.
 *
 */
public class Settings extends PreferenceActivity{
	private static final String TAG = "Settings";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Activity started!");
		addPreferencesFromResource(R.xml.settings);

	} // End onCreate.


}
