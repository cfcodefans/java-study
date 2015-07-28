package cf.study.network.socket.wire.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;

public class ItemQuoteDecoderText implements ItemQuoteDecoder, ItemQuoteTextConst {

	private String encoding;

	public ItemQuoteDecoderText() {
		encoding = DEFAULT_ENCODING;
	}

	public ItemQuoteDecoderText(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public ItemQuote decode(InputStream wire) throws IOException {
		String itemNo, desc, quant, price, flags;

		byte[] space = " ".getBytes(encoding);
		byte[] newline = "\n".getBytes(encoding);
		
		itemNo = new String(Framer.nextToken(wire, space), encoding);
		desc = new String(Framer.nextToken(wire, newline), encoding);
		quant = new String(Framer.nextToken(wire, space), encoding);
		price = new String(Framer.nextToken(wire, space), encoding);
		flags = new String(Framer.nextToken(wire, newline), encoding);
		return new ItemQuote(Long.parseLong(itemNo), desc, Integer.parseInt(quant), Integer.parseInt(price), (flags.indexOf('d') != -1), (flags.indexOf('s') != -1));
	}

	@Override
	public ItemQuote decode(DatagramPacket packet) throws IOException {
		ByteArrayInputStream payload = 
				new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
		return decode(payload);
	}

}
