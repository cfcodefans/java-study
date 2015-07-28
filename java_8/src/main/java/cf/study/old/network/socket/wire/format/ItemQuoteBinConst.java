package cf.study.network.socket.wire.format;

public interface ItemQuoteBinConst {
	public static final String DEFAULT_ENCODING = "ISO_8859_1";
	public static final int DISCOUNT_FLAG = 1 << 7;
	public static final int IN_STOCK_FLAG = 1 << 0;
	public static final int MAX_DESC_LEN = 255;
	public static final int MAX_WIRE_LENGTH = 1024;
}
