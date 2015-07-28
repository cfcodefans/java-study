package cf.study.network.socket.wire.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ItemQuoteEncoderText implements ItemQuoteEncoder, ItemQuoteTextConst {

	private String encoding;
	
	public ItemQuoteEncoderText() {
		this.encoding = DEFAULT_ENCODING;
	}
	
	public ItemQuoteEncoderText(String encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public byte[] encode(ItemQuote item) throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		OutputStreamWriter	out = new OutputStreamWriter(buf, encoding);
		
		out.write(item.itemNumber + " ");
		if (item.itemDesc.indexOf('\n') != -1) {
			throw new IOException("Invalid description (contains newline)");
		}
		
		out.write(item.itemDesc + "\n" + item.quantity + " " + item.unitPrice + " ");
		if (item.discount) {
			out.write('d');
		}
		if (item.intStock) {
			out.write('s');
		}
		out.write('\n');
		out.flush();
		if (buf.size() > MAX_WIRE_LENGTH) {
			throw new IOException("Encoded length too long");
		}
		
		return buf.toByteArray();
	}
	
}
