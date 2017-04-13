package cf.study.java.net.socket.wire.format;

public interface ItemQuoteEncoder {
	byte[] encode(ItemQuote item) throws Exception;
}
