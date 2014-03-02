package hr.fer.tel.ruazosa.shopshop;

import hr.fer.tel.ruazosa.shopshop.R;
import hr.fer.tel.ruazosa.shopshop.classes.List;
import hr.fer.tel.ruazosa.shopshop.util.Util;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity used to edit selected item from list
 *
 */
public class EditItem extends Activity {

	private Button editItem;
	private EditText editName;
	private EditText editAmount;
	String listName;
	int itemIndex;
	List list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_item);

		// Preuzimanje parametara iz Intenta.
		Intent intent = getIntent();
		listName = intent.getStringExtra("listName");
		itemIndex = intent.getIntExtra("itemIndex", -1);

		editItem = (Button) this.findViewById(R.id.editItemButton);
		editName = (EditText) this.findViewById(R.id.editItemOnList);
		editAmount = (EditText) this.findViewById(R.id.editAmountOnList);
		
		// Otvori Item iz liste i postavi inicijalne vrijednosti.
		File file = getBaseContext().getFileStreamPath(listName);
		list= new List(file);
		editName.setText(list.getItems().get(itemIndex).getName());
		editAmount.setText((list.getItems().get(itemIndex).getCount()));
		
		editItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {			
				String itemname = editName.getText().toString();
				String amount = editAmount.getText().toString();
				if(!itemname.equals("")){
				itemname = itemname.substring(0, 1).toUpperCase() + itemname.substring(1);				
				}
				if(itemname.equals("")){ // Ako nema imena.
					Util.showToast(getApplicationContext(), "Enter Item Name");
				} else if(amount.equals("")){ // Ako nema kolicine.
					Util.showToast(getApplicationContext(), "Enter Item Amount");
				} else if(!((itemname.equals(""))&&(amount.equals("")))){
					if(list.itemExists(itemname) && !itemname.equals(list.getItems().get(itemIndex).getName())){
						// Ako item postoji.
						Util.showToast(getApplicationContext(), "Item Already Exists");
					}else{
						// Ako su zadovoljeni svi uvjeti.
						list.editListItem(itemIndex, itemname, amount);
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
						Intent intent2 = new Intent(getApplicationContext(), EditListItems.class);
						intent2.putExtra("listName", listName);
						startActivity(intent2);
						finish();
					}
				}
			}

		});

	} // End onCreate

	// Back button vraca na EditList.
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(getApplicationContext(), EditListItems.class);
		intent.putExtra("listName", listName);
		startActivity(intent);
		finish();
	}

}
