package cf.study.java.net.socket.wire.format;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;

public class ItemQuoteDecoderBin implements ItemQuoteDecoder, ItemQuoteBinConst {
	private String encoding;

	public ItemQuoteDecoderBin() {
		this.encoding = DEFAULT_ENCODING;
	}

	public ItemQuoteDecoderBin(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public ItemQuote decode(InputStream wire) throws IOException {
		DataInputStream src = new DataInputStream(wire);

		long itemNumber = src.readLong();
		int quantity = src.readInt();
		int unitPrice = src.readInt();
		byte flags = src.readByte();
		int stringLength = src.read(); // Returns an unsigned byte as an int
		if (stringLength == -1) {
			throw new IOException();
		}

		byte[] stringBuf = new byte[stringLength];
		src.readFully(stringBuf);
		String itemDesc = new String(stringBuf, encoding);

		return new ItemQuote(itemNumber, itemDesc, quantity, unitPrice, ((flags & DISCOUNT_FLAG) == DISCOUNT_FLAG), ((flags & IN_STOCK_FLAG) == IN_STOCK_FLAG));

	}

	@Override
	public ItemQuote decode(DatagramPacket packet) throws IOException {
		ByteArrayInputStream payload = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
		return decode(payload);
	}

}
