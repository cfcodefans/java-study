package cf.study.network.socket.wire.format;

public interface ItemQuoteEncoder {
	byte[] encode(ItemQuote item) throws Exception;
}
