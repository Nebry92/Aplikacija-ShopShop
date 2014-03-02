package hr.fer.tel.ruazosa.shopshop;

import hr.fer.tel.ruazosa.shopshop.R;
import hr.fer.tel.ruazosa.shopshop.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

/**
 * Launcher Activity. Activity shows all lists.
 *
 */
public class Main extends ListActivity{
	private static final String TAG = "Main";
	private EditText et;
	private ListView lv;
	private ArrayList<String> array_sort;
	int textlength=0;
	private String[] listview_names;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Activity started!");
		setContentView(R.layout.main_activity);
		// Priprema za floating context menu.
		registerForContextMenu (getListView());
		
		listview_names=populateList();

		et = (EditText) findViewById(R.id.EditText01);
		lv = (ListView) findViewById(android.R.id.list);

		array_sort=new ArrayList<String> (Arrays.asList(listview_names));

		setListAdapter(new bsAdapter(this));

		et.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s){
				// Abstract Method of TextWatcher Interface.
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
				// Abstract Method of TextWatcher Interface.
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				textlength = et.getText().length();
				array_sort.clear();
				for (int i = 0; i < listview_names.length; i++){
					if (textlength <= listview_names[i].length()){
						if(listview_names[i].toLowerCase().contains(et.getText().toString().toLowerCase().trim())){
							array_sort.add(listview_names[i]);
						}
					}
				}
				AppendList(array_sort);
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0,View arg1, int position, long arg3){
				String item = (String) getListAdapter().getItem(position);
				Intent intent = new Intent(Main.this, EditListItems.class);
				intent.putExtra("listName", item);
				startActivity(intent);
				finish();
			}
		});
	}

	public void AppendList(ArrayList<String> str){
		setListAdapter(new bsAdapter(this));
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateList();
		array_sort=new ArrayList<String> (Arrays.asList(listview_names));
		Collections.sort(array_sort);
		setListAdapter(new bsAdapter(this));
	}

	// Ispisivanje svih lista iz interne memorije u ListView.
	private String[] populateList(){
		File[] files = getFilesDir().listFiles();
		String[] fileArray = new String[files.length];
		for (int i = 0; i < files.length; ++i){
			fileArray[i] = files[i].getName();
		}
		return fileArray;
	}

	/* Pritisak na jednu od listi u ListView pokrece aktivnost EditList za tu listu.
	   Salje se ime liste kao parametar u Intentu. */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Intent intent = new Intent(Main.this, EditListItems.class);
		intent.putExtra("listName", item);
		startActivity(intent);
		finish();
	}

	// Stvaranje menija.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	// Na odabir elementa iz menija.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		switch (item.getItemId()){
		case R.id.newListMenuItem:
			startActivity(new Intent(this, NewList.class));
			finish();
			break;
		case R.id.dropbox:
			startActivity(new Intent(this, DropboxConnect.class));
			break;
		case R.id.refresh:
			startActivity(new Intent(this, Main.class));
			finish();
			break;
		case R.id.deleteAllLists:
			File f = getFilesDir();
			File[] files = f.listFiles();
			for (File file : files) {
				file.delete();
				prefs.edit().putBoolean(file.getName() + "deleted", true).commit();
			}
			startActivity(new Intent(this, Main.class));
			finish();
			break;
		case R.id.settingsMenuItem:
			startActivity(new Intent(this, Settings.class));
			break;
		case R.id.exitMenuItem:
			System.exit(0);
			break;
		}
		return true;
	}

	// Stvaranje floating context menija.
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_context_menu, menu);
	}

	// Na odabir elementa iz floating context menija.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int broj= info.position;
		String name=array_sort.get(broj);		
		switch (item.getItemId()) {
		// Brisanje liste iz interne memorije.
		case R.id.deleteList:
			File dir = getFilesDir();
			File file = new File(dir, name);
			boolean deleted = file.delete();
			if (deleted){
				Util.showToast(getApplicationContext(), "List deleted");
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

				// Mark as deleted to delete list on Dropbox
				prefs.edit().putBoolean(name + "deleted", true).commit();
				Intent intent = new Intent(getApplicationContext(), Main.class);
				// Aktivnost se ponovo pokrece kako bi se popis listi obnovio.
				startActivity(intent);
				finish();
			} else {
				Util.showToast(getApplicationContext(), "List can't be deleted");
			}	        	
			break;
		case R.id.editList:
			Intent intent = new Intent(getApplicationContext(), EditList.class);
			intent.putExtra("listName", name);
			startActivity(intent);
			finish();
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}

	public class bsAdapter extends BaseAdapter{
		Activity cntx;

		public bsAdapter(Activity context){
			this.cntx=context;
		}

		@Override
		public int getCount(){
			return array_sort.size();
		}

		@Override
		public Object getItem(int position){
			return array_sort.get(position);
		}

		@Override
		public long getItemId(int position){
			return array_sort.size();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row=null;

			LayoutInflater inflater=cntx.getLayoutInflater();
			row=inflater.inflate(R.layout.main_activity_row, null);

			TextView   tv   =   (TextView)  row.findViewById(R.id.itemRow);

			tv.setText(array_sort.get(position));

			return row;
		}
	}
}
