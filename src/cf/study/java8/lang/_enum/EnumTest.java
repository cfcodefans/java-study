package cf.study.java8.lang._enum;

import org.junit.Test;

public class EnumTest {
	enum Direction {
		south, north, east, west;
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
