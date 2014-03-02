package hr.fer.tel.ruazosa.shopshop;

import hr.fer.tel.ruazosa.shopshop.R;
import hr.fer.tel.ruazosa.shopshop.classes.List;
import hr.fer.tel.ruazosa.shopshop.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity used to create a new list.
 *
 */
public class NewList extends Activity implements OnColorChangedListener{
	private static final String TAG = "NewList";

	private Button newListBtn;
	private EditText newListName;
	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;
	private int color;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Activity started!");
		setContentView(R.layout.new_list_activity);

		newListBtn = (Button) this.findViewById(R.id.newListBtn);
		newListName = (EditText) this.findViewById(R.id.newListName);		
		picker = (ColorPicker) findViewById(R.id.picker);
		svBar = (SVBar) findViewById(R.id.svbar);
		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);

		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.setOldCenterColor(picker.getColor());
		picker.setOnColorChangedListener(this);

		newListBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Postavljanje imena, boje pozadine te stvaranje nove liste.
				String listName = newListName.getText().toString();
				if(!listName.equals("")){
				listName=listName.substring(0, 1).toUpperCase() + listName.substring(1);
				}
				picker.setOldCenterColor(picker.getColor());
				color = picker.getColor();
				List list = new List(color, listName);
				String string = list.toXMLplist();

				File file = getBaseContext().getFileStreamPath(listName);
				boolean exists = file.exists();

				if (listName.equals("")){ 
					// Ako nije zadano ime.
					Util.showToast(getApplicationContext(), "Please enter a list name");
				} else if (!exists){ 
					// Ako ne postoji lista s tim imenom.
					FileOutputStream fos;
					try {
						fos = openFileOutput(listName, Context.MODE_PRIVATE);
						fos.write(string.getBytes());
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					prefs.edit().putBoolean(listName + "created", true).commit();
					
					Util.showToast(getApplicationContext(), "New list created");
					Intent intent = new Intent(NewList.this, EditListItems.class);
					intent.putExtra("listName", listName);
					startActivity(intent);
					finish();
				} else{ 
					// Ako postoji lista s tim imenom.
					Util.showToast(getApplicationContext(), "List with that name already exists!\nPlease choose another name.");
				}
			}
		});
	}

	@Override
	public void onColorChanged(int color) {
		picker.setOldCenterColor(picker.getColor());
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(getApplicationContext(), Main.class);
		startActivity(intent);
		finish();
	}

}
