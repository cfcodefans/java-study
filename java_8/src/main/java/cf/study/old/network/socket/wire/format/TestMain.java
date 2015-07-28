package cf.study.network.socket.wire.format;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
//		(new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					RecvTCP.main(new String[] {"20000"});
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		})).start();
//		
//		Thread.sleep(1000);
//		
//		(new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					SendTCP.main(new String[] {"127.0.0.1", "20000"});
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		})).start();
		
		
		
		(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					RecvUDP.main(new String[] {"20000"});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		})).start();
		Thread.sleep(1000);
		(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					SendUDP.main(new String[] {"127.0.0.1", "20000"});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		})).start();
		
	}

}
