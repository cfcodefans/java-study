package cf.study.java8.net.socket.wire.format;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ItemQuoteEncoderBin implements ItemQuoteEncoder, ItemQuoteBinConst {
	private String encoding;
	
	public ItemQuoteEncoderBin() {
		this.encoding = DEFAULT_ENCODING;
	}
	
	public ItemQuoteEncoderBin(String encoding) {
		this.encoding = encoding;
	}
	@Override
	public byte[] encode(ItemQuote item) throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream	out = new DataOutputStream(buf);
		
		out.writeLong(item.itemNumber);
		out.writeInt(item.quantity);
		out.writeInt(item.unitPrice);
		
		byte flags = 0;
		if (item.discount) {
			flags |= DISCOUNT_FLAG;
		}
		if (item.intStock) {
			flags |= IN_STOCK_FLAG;
		}
		out.writeByte(flags);
		
		byte[] encodedDesc = item.itemDesc.getBytes(encoding);
		if (encodedDesc.length > MAX_DESC_LEN) {
			throw new IOException("Item Description exceeds encoded length limit");
		}
		out.writeByte(encodedDesc.length);
		out.write(encodedDesc);
		out.flush();
		
		return buf.toByteArray();
	}

}
