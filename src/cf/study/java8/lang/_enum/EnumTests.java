package cf.study.java8.lang._enum;

import org.junit.Test;

public class EnumTests {
	enum Direction {
		south, north, east, west;
	}
	
	@Test
	public void testNotExistentEnum() {
		System.out.println(Direction.valueOf("what"));
	}

	@Test
	public void testSwitchWithEnum() {
		Direction d = Direction.south;// null;
		switch (d) {//throw NullPointException for null value
		case south:
			System.out.println(Direction.south);
			break;
		case north:
			System.out.println(Direction.north);
			break;
		case west:
			System.out.println(Direction.west);
			break;
		case east:
			System.out.println(Direction.east);
			break;
		default:
			System.out.println(d);
			break;
		}
	}
}
