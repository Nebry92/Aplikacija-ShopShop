package hr.fer.tel.ruazosa.shopshop;

import hr.fer.tel.ruazosa.shopshop.R;
import hr.fer.tel.ruazosa.shopshop.classes.List;
import hr.fer.tel.ruazosa.shopshop.util.Util;

import java.io.File;
import java.io.FileOutputStream;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity used to edit list
 *
 */
public class EditList extends Activity implements OnColorChangedListener{
	private static final String TAG = "NewList";

	private Button ListBtn;
	private EditText ListName;
	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;
	private int color;
	String oldListName;
	String newListName;
	List list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Activity started!");
		setContentView(R.layout.new_list_activity);

		Intent intent = getIntent();
		oldListName = intent.getStringExtra("listName");
		File file = getBaseContext().getFileStreamPath(oldListName);
		list= new List(file);
		
		ListBtn = (Button) this.findViewById(R.id.newListBtn);
		ListBtn.setText("Edit List");
		ListName = (EditText) this.findViewById(R.id.newListName);
		ListName.setText(list.getName());
		picker = (ColorPicker) findViewById(R.id.picker);
		picker.setColor(list.getColor());
		svBar = (SVBar) findViewById(R.id.svbar);
		float hsv[] = {0.0f, 0.0f, 0.0f};
		Color.colorToHSV(list.getColor(), hsv);
//		Util.showToast(getApplicationContext(), "Saturation: " + hsv[1] + ", Value: " + hsv[2]);
		if (hsv[1] != 1.0f)
			svBar.setSaturation(hsv[1]);
		else if(hsv[2] != 1.0f)
			svBar.setValue(hsv[2]);
		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
		opacityBar.setOpacity(Color.alpha(list.getColor()));
		
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.setOldCenterColor(picker.getColor());
		picker.setOnColorChangedListener(this);

		ListBtn.setOnClickListener(new View.OnClickListener() {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			@Override
			public void onClick(View v) {
				// Postavljanje imena, boje pozadine te stvaranje nove liste.
				newListName = ListName.getText().toString();
				color = picker.getColor();
				list.setName(newListName);
				list.setColor(color);
				String string = list.toXMLplist();
				if(!newListName.equals("")){
					newListName = newListName.substring(0, 1).toUpperCase() + newListName.substring(1);
				}
				File file = getBaseContext().getFileStreamPath(oldListName);
				File fileNew = getBaseContext().getFileStreamPath(newListName);

				// Ako se ime promijenilo, provjera postoji li neki file sa novim imenom.
				boolean exists = fileNew.exists() && !newListName.equals(oldListName);

				if (newListName.equals("")){ // Ako nije zadano ime.
					Util.showToast(getApplicationContext(), "Please enter a list name");
				} else if (!exists){ // Ako ne postoji lista s tim imenom.
					file.renameTo(getBaseContext().getFileStreamPath(newListName));
					FileOutputStream fos;
					try {
						fos = openFileOutput(newListName, Context.MODE_PRIVATE);
						fos.write(string.getBytes());
						fos.close();
						Util.listChanged(newListName, getApplicationContext());
						prefs.edit().putBoolean(oldListName + "deleted", true).commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Util.showToast(getApplicationContext(), "List Edited");
					Intent intent = new Intent(EditList.this, Main.class);
					intent.putExtra("listName", newListName);
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
		intent.putExtra("listName", newListName);
		startActivity(intent);
	}

}
