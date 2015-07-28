package cf.study.jdk7.io.nio;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;


public class BufferTest {
	ByteBuffer buf = ByteBuffer.allocate(26);
	
	@Before
	public void initBuf() {
		buf.put("abcdefghijklmnopqrstuvwxyz".getBytes());
	}
	
	@Test
	public void test() {
		buf.rewind();//go to 0
		System.out.println((char)buf.get()); //go to 1
		System.out.println((char)buf.get()); //go to 2
		System.out.println(buf.position()); //2
		buf.mark(); //mark 2
		System.out.println((char)buf.get()); //go to 3
		System.out.println((char)buf.get()); //go to 4
		System.out.println(buf.position()); //4
		buf.position(buf.position() + 2); //go to new position 4+2 = 6
		System.out.println((char)buf.get()); // go to 7
		System.out.println((char)buf.get()); // go to 8
		System.out.println(buf.position()); // 8
		buf.reset(); //back to mark 2
		System.out.println((char)buf.get());
		System.out.println((char)buf.get());
		System.out.println(buf.position());
		buf.rewind();//go to 0 again
		System.out.println((char)buf.get());
		System.out.println((char)buf.get());
		System.out.println(buf.position());
		
		System.out.println(new String(buf.array()));
		buf.compact();
		System.out.println(new String(buf.array()));
		System.out.println(buf.position());
		System.out.println((char)buf.get());
		System.out.println((char)buf.get());
		System.out.println(buf.position());
		
		buf.rewind();
		System.out.println((char)buf.get());
		System.out.println((char)buf.get());
		System.out.println(buf.position());
		
		buf.flip();
		System.out.println(buf.position());
		byte[] ba = new byte[buf.remaining()];
		buf.get(ba);
		System.out.println(new String(buf.array()));
		System.out.println(new String(ba));
	}
}
