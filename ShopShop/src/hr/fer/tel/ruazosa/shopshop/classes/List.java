package hr.fer.tel.ruazosa.shopshop.classes;

import hr.fer.tel.ruazosa.shopshop.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.Color;

import com.dd.plist.NSArray;
import com.dd.plist.NSDate;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;

/**
 * Class that represents one list. List contains name, color and date.
 * Every list contains of multiple items.
 *
 */
public class List{
	private int color;
	private String name;
	private ArrayList<Item> items= new ArrayList<Item>();

	public List(File file){
		fromXMLplist(file);
	}

	public List(int color, String name) {
		super();
		this.color = color;
		this.name = name;
		}

	public int getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public void addItem(Item i){
		items.add(i);
	}

	public void removeItem(int index){
		items.remove(index);
	}

	public void setItemDone(int index){
		items.get(index).setDone(true);
	}

	// Provjera da li Lista vec sadrzava Item sa tim imenom.
	public boolean itemExists(String itemName){
		for (Item item : items) {
			if (item.getName().equals(itemName))
				return true;
		}
		return false;
	}
	
	// Vraca id u prvobitnoj listi prije searcha. Id se trazi po imenu.
	public int idByNameInArray(String itemName){
		int broj=0;
		for (Item item : items) {
			if (item.getName().equals(itemName))
				break;
			broj++;
		}
		return	broj;	
	}
	
	// Postavlja name, count Itema sa danim indexom na nove vrijednosti.
	public void editListItem(int itemIndex, String newName, String newCount){
		Item item = items.get(itemIndex);
		item.setName(newName);
		item.setCount(newCount);
		}

	// Pretvaranje liste u String -> XMLplist-u.
	public String toXMLplist(){
		float alpha = (float) Color.alpha(color) / 255;
		float red = (float) Color.red(color) / 255;
		float green = (float) Color.green(color) / 255;
		float blue = (float) Color.blue(color) / 255;
		
		NSDictionary root = new NSDictionary();
		NSArray color = new NSArray(4);

		color.setValue(0, NSObject.wrap(alpha));
		color.setValue(1, NSObject.wrap(red));
		color.setValue(2, NSObject.wrap(green));
		color.setValue(3, NSObject.wrap(blue));

		root.put("color", color);
		
		NSArray shoppingList = new NSArray(items.size());
		int num = 0;
		for (Item i : items) {
			NSDictionary item = new NSDictionary();
			item.put("count", i.getCount());
			item.put("done", i.isDone());
			item.put("name", i.getName());
			shoppingList.setValue(num, item);
			num++;
		}
		root.put("shoppingList", shoppingList);
		return root.toXMLPropertyList();
	}

	// Stvaranje liste parsiranjem file-a spremljenog u obliku XMLpliste.
	public void fromXMLplist(File file){
		try {
			NSDictionary root = (NSDictionary)PropertyListParser.parse(file);
			NSObject[] cvalues = ((NSArray) root.objectForKey("color")).getArray();
			NSObject alphaObj = cvalues[0];
			NSObject redObj = cvalues[1];
			NSObject greenObj = cvalues[2];
			NSObject blueObj = cvalues[3];

			this.name = file.getName();

			int alpha = (int) Math.round(((NSNumber) alphaObj).floatValue() * 255);
			int red = (int) Math.round(((NSNumber) redObj).floatValue() * 255);
			int green = (int) Math.round(((NSNumber) greenObj).floatValue() * 255);
			int blue = (int) Math.round(((NSNumber) blueObj).floatValue() * 255);

			int c = Color.argb(alpha, red, green, blue);
			this.color = c;

			NSObject[] shoppingList = ((NSArray) root.objectForKey("shoppingList")).getArray();
			for (NSObject obj : shoppingList) {
				NSDictionary dict = (NSDictionary) obj;
				String count = ((NSString) dict.objectForKey("count")).getContent();
				boolean done = ((NSNumber) dict.objectForKey("done")).boolValue();
				String name = ((NSString) dict.objectForKey("name")).getContent();				
				items.add(new Item(name, count, done));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
