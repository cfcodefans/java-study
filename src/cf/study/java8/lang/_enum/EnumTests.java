package cf.study.java8.lang._enum;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class EnumTests {
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
	
	public static interface IConverter<S, T> {
		T doConvert(S s, T t, Map<?, ?> ctx);
	}
	
	public static interface IJsonParser<T> extends IConverter<JsonNode, T> {
		String getName();
	}
	
	enum JsonDateParser implements IJsonParser<Date> {
		YEAR {
			public Date doConvert(JsonNode s, Date t, Map<?, ?> ctx) {
				return DateUtils.setYears(t, s.get(getName()).asInt());
			}
		},
		MONTH {
			public Date doConvert(JsonNode s, Date t, Map<?, ?> ctx) {
				return DateUtils.setMonths(t, s.get(getName()).asInt());
			}
		},
		DATE {
			public Date doConvert(JsonNode s, Date t, Map<?, ?> ctx) {
				return DateUtils.setDays(t, s.get(getName()).asInt());
			}
		};
		public String getName() {return this.name().toLowerCase();}
	}
	
}
