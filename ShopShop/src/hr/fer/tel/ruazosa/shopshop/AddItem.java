package hr.fer.tel.ruazosa.shopshop;

import hr.fer.tel.ruazosa.shopshop.R;
import hr.fer.tel.ruazosa.shopshop.classes.Item;
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
 * Activity used to add item to list
 *
 */
public class AddItem extends Activity {

	private Button createItem;
	private EditText itemName;
	private EditText itemAmount;
	int pointer;
	String listName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_item);

		// Ime liste salje se kao parametar u Intentu.
		Intent intent = getIntent();
		listName = intent.getStringExtra("listName");	

		createItem = (Button) this.findViewById(R.id.editButton);
		itemName = (EditText) this.findViewById(R.id.editItemName);
		itemAmount = (EditText) this.findViewById(R.id.editItemAmount);
		
		createItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {			
				String itemname = itemName.getText().toString();
				String amount = itemAmount.getText().toString();
				if(!itemname.equals("")){			
				itemname=itemname.substring(0, 1).toUpperCase() + itemname.substring(1);
				}
				File file = getBaseContext().getFileStreamPath(listName);

				if(itemname.equals("")){ // Ako nema imena.
					Util.showToast(getApplicationContext(), "Enter Item Name");
				} else if(amount.equals("")){ // Ako nema kolicine.
					Util.showToast(getApplicationContext(), "Enter Item Amount");
				} else if(!((itemname.equals(""))&&(amount.equals("")))){
					// Ako su zadovoljeni svi uvjeti.
					List list= new List(file);
					if(list.itemExists(itemname)){
						// Ako item postoji.
						Util.showToast(getApplicationContext(), "Item Already Exists");
					}else {
						// Ako ne postoji stvori ga.
						list.addItem(new Item(itemname, amount, false));
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

		}); // End listener
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

