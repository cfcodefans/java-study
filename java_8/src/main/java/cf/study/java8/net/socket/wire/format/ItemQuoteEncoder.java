package cf.study.java8.net.socket.wire.format;

public interface ItemQuoteEncoder {
	byte[] encode(ItemQuote item) throws Exception;
}
