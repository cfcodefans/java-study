package cf.study.java8.net.socket.wire.format;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		(new Thread(() -> {
			try {
				RecvUDP.main(new String[] { "20000" });
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).start();
		Thread.sleep(1000);
		(new Thread(() -> {
			try {
				SendUDP.main(new String[] { "127.0.0.1", "20000" });
			} catch (Exception e) {
				e.printStackTrace();
			}
		})).start();

	}

}
