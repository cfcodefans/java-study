package cf.study.java.net.socket.wire.format;

public class ItemQuote {
	public long itemNumber;
	public String itemDesc;
	public int quantity;
	public int unitPrice;
	public boolean discount;
	public boolean intStock;
	
	public ItemQuote(long itemNumber, String itemDesc, int quantity, int unitPrice, boolean discount, boolean intStock) {
		super();
		this.itemNumber = itemNumber;
		this.itemDesc = itemDesc;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.discount = discount;
		this.intStock = intStock;
	}
	
	public String toString() {
		final String EOLN = System.getProperty("line.separator");
		String value = "Item#=" + itemNumber + EOLN
					   + "Description=" + itemDesc + EOLN
					   + "Quantity=" + quantity + EOLN
					   + "Price(each)" + unitPrice + EOLN
					   + "Total=" + (quantity * unitPrice);
		if (discount) {
			value += " (discounted)";
		}
		if (intStock) {
			value += EOLN + "In Stock" + EOLN;
		} else {
			value += EOLN + "Out of Stock" + EOLN;
		}
		return value;
	}
}
