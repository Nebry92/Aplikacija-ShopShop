package hr.fer.tel.ruazosa.shopshop;

import hr.fer.tel.ruazosa.shopshop.R;
import hr.fer.tel.ruazosa.shopshop.classes.Item;
import hr.fer.tel.ruazosa.shopshop.classes.List;
import hr.fer.tel.ruazosa.shopshop.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Activity that shows list with all of its items.
 *
 */
public class EditListItems extends ListActivity {
	private static final String TAG = "EditList";

	RelativeLayout relLayout;
	private ListView lv;
	TextView listNameTextView;
	TextView itemRow;
	TextView empty;
	String listName;
	List list;
	int textColor;
	private EditText et;
	private ArrayList<String> array_sort;
	int textlength=0;
	private String[] listview_names;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_list_items_activity);
		Log.i(TAG, "Activity started!");

		listNameTextView = (TextView) this.findViewById(R.id.editListName);
		itemRow = (TextView) this.findViewById(R.id.itemRow);
		empty = (TextView) this.findViewById(android.R.id.empty);
		relLayout = (RelativeLayout) this.findViewById(R.id.editListBg);

		// Pronaði i postavi ime liste. Ime liste se prenosi kao parametar u Intentu.
		Intent intent = getIntent();
		listName = intent.getStringExtra("listName");
		listNameTextView.setText(listName);

		// Otvori listu iz interne memorije.
		File file = getBaseContext().getFileStreamPath(listName);
		list= new List(file);
		
		// Postavi boju pozadine.
		int bgColor = list.getColor();
		relLayout.setBackgroundColor(bgColor);

		// Postavi boju teksa suprotnu boji pozadine.
		textColor = Util.getOppositeColor(bgColor);
		listNameTextView.setTextColor(textColor);
		empty.setTextColor(textColor);

		registerForContextMenu(getListView());

		listview_names=populateList();

		et = (EditText) findViewById(R.id.EditText02);
		et.setTextColor(textColor);
		array_sort = new ArrayList<String> (Arrays.asList(listview_names));
		setListAdapter(new bsAdapter(this));
		
		lv = (ListView) findViewById(android.R.id.list);

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
				String name1=array_sort.get(position);
				String[] name = name1.split(",");
				int broj= list.idByNameInArray(name[0]);
				if(list.getItems().get(broj).isDone()){
						list.getItems().get(broj).setDone(false);
					}else{
						list.setItemDone(broj);
					}
					String str = list.toXMLplist();
					FileOutputStream fostream;
					try {					
						fostream = openFileOutput(listName, Context.MODE_PRIVATE);
						fostream.write(str.getBytes());
						fostream.close();
						Util.listChanged(listName, getApplicationContext());
					} catch (Exception e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(getApplicationContext(), EditListItems.class);
					intent.putExtra("listName", listName);
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

	// Popunjavanje ListView-a Itemima.
	private String[] populateList(){
		ArrayList<Item> items= list.getItems();
		String[] item_row = new String[items.size()];
		for (int i = 0; i < items.size(); ++i){
			Item item = items.get(i);
			String status = item.isDone() ? "  " + Html.fromHtml("&#10004;"): "";
			item_row[i] = item.getName() + ", " + item.getCount() + status;
		}
		return item_row;
	}

	// Stvaranje menija.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_menu, menu);
		return true;
	}

	// Na odabir stavke menija.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.addItemToList:
			Intent intent= new Intent(this, AddItem.class);
			intent.putExtra("listName", listName);
			startActivity(intent);
			finish();
			break;		
		case R.id.refreshItems:
			Intent intent2= new Intent(this, EditListItems.class);
			intent2.putExtra("listName", listName);
			startActivity(intent2);
			finish();
			break;
				
		case R.id.cleanItems:
			ArrayList<Item> items= list.getItems();
			int n = items.size();
			for (int i = 0; i < n; i++){
				if(list.getItems().get(i).isDone())
				{ 
					list.removeItem(i);
					i--;
					n--;
					
				}
					
			}

			String string = list.toXMLplist();
			FileOutputStream fos;
			try {					
				fos = openFileOutput(listName, Context.MODE_PRIVATE);
				fos.write(string.getBytes());
				fos.close();
				Util.listChanged(listName, getApplicationContext());
			} catch (Exception e) {
				e.printStackTrace();
			}		
			
			Intent intent3 = new Intent(getApplicationContext(), EditListItems.class);
			intent3.putExtra("listName", listName);
			startActivity(intent3);
			finish();
			break;
			
		}
		return true;
		
	}

	// Stvaranje floating context menija.
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_list_context_menu, menu);
		}

	// Popunjavanje floating context menija.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int index3= info.position;
		String name1=array_sort.get(index3);
		String[] name = name1.split(",");
		int broj= list.idByNameInArray(name[0]);
		switch (item.getItemId()) {
		// Editiranje Itema sa liste.
		case R.id.editItem:
			Intent intent2 = new Intent(this, EditItem.class);
			intent2.putExtra("listName", listName);
			intent2.putExtra("itemIndex", broj);
			startActivity(intent2);
			finish();
			break;
			// Brisanje Itema sa liste.
		case R.id.deleteItem:
			int index4= info.position;
			String name3=array_sort.get(index4);
			String[] name2 = name3.split(",");
			int num= list.idByNameInArray(name2[0]);
			list.removeItem(num);
			String string = list.toXMLplist();
			FileOutputStream fos;
			try {					
				fos = openFileOutput(listName, Context.MODE_PRIVATE);
				fos.write(string.getBytes());
				fos.close();
				Util.listChanged(listName, getApplicationContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
			Intent intent3 = new Intent(getApplicationContext(), EditListItems.class);
			intent3.putExtra("listName", listName);
			startActivity(intent3);
			finish();
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(getApplicationContext(), Main.class);
		startActivity(intent);
		finish();
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
			row=inflater.inflate(R.layout.edit_list_items_activity_row, null);
			int bgColor = list.getColor();
			textColor = Util.getOppositeColor(bgColor);
			TextView tv= (TextView) row.findViewById(R.id.itemRow);
			tv.setTextColor(textColor);
			tv.setText(array_sort.get(position));

			return row;
		}
	}

}
