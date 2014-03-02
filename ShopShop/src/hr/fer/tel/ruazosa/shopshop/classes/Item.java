package hr.fer.tel.ruazosa.shopshop.classes;

/**
 * Class that represents one item from list. Item contains name, item count,
 * item unit and status that represents if item is bought or not.
 *
 */
public class Item {
	private String name;
	private String count;
	private boolean done;

	public Item(String name, String count, boolean done) {
		super();
		this.name = name;
		this.count = count;
		this.done = done;
	}

	public String getName() {
		return name;
	}

	public String getCount() {
		return count;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

}
