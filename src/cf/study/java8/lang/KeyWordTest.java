package cf.study.java8.lang;

import org.junit.Test;

public class KeyWordTest {
	public static final String[] KEY_WORDS = {
		"abstract",		//checked
		"assert", 		//checked
		"boolean",
		"break", 		//checked
		"byte",
		"case", 		//checked
		"catch",
		"char",
		"class",
		"const",		//checked
		"continue",
		"default",		//checked
		"do",
		"double",
		"else",			//checked
		"enum",
		"extends",
		"final",
		"finally",
		"float",
		"for",
		"goto",
		"if",			//checked
		"implements",
		"import",		//checked
		"instanceof",
		"int",
		"interface",
		"long",
		"native",
		"new",
		"package",		//checked
		"private",
		"protected",
		"public",
		"return",
		"short",
		"static",
		"strictfp",
		"super",
		"switch",		//checked
		"synchronized",
		"this",
		"throw",
		"throws	",
		"transient",
		"try",
		"void",
		"volatile",
		"while"
	};

	@Test
	public void testIfAndElse() {
		if (true) {System.out.println(true);}
		if (false) {System.out.println(false);}
		
		if (true)
			if (false)
				System.out.println(false);
			else if (true)
				System.out.println(true);
			else if (false);
			else;
	}
	
	@Test
	public void testAssert() {
		assert true;
		System.out.println("assert true");
		assert false; // run java with "-ea", this throws AssertError
		System.out.println("assert false");  
	}
	
	@Test
	public void testBreak() {
//		break; break can't be used outside of loop or switch
		for (int i = 0; i < 1; i++) {break;}
		do {break;} while(true);
		while(true) {break;}
		
		switch (1) {
			case 1:
				break;
			default:
				break;
		}
		
		_outer: for (int i = 0; i < 3; i++) {
			System.out.println("outer begins: " + i);
			for (int j = 0; j < i; j++) {
				System.out.println("\t" + j);
				break _outer; //jump out of outer loop too
			}
			System.out.println("outer ends: " + i);
		}
	}
	
	@Test
	public void testCaseAndSwitchAndDefaultAndBreakAndContinueAndLabel() {
		// case; error
		// case true;
		switch (5) {
		case 1:
			System.out.println(1);
		case 2:
			System.out.println(2);
		case 3:
			System.out.println(3);
		case 4:
			System.out.println(4);
		case 5:
			System.out.println(5);
		case 6:
			System.out.println(6);
		case 7:
			System.out.println(7);
		case 8:
			System.out.println(8);
		case 9:
			System.out.println(9);
		case 0:
			System.out.println(10);
		default:
			System.out.println("default:");
			break;
		}

//		testDefaultInSwitch();
		
		System.out.println();
		{
			int i = 5;
			_1: switch (i) {
			case 1:
				System.out.println(1);
			case 2:
				System.out.println(2);
			case 3:
				System.out.println(3);
			case 4:
				System.out.println(4);
			case 5:
				System.out.println(5);
				i = 1;
				break _1;
			case 6:
				System.out.println(6);
			case 7:
				System.out.println(7);
			case 8:
				System.out.println(8);
			case 9:
				System.out.println(9);
			case 0:
				System.out.println(10);
				i = 0;
			default:
				System.out.println("default: i = " + i);
				break;
			}

			System.out.println("i = " + i);
		}

		System.out.println();
		{
			int i = 5;
			_1: switch (i) {
			case 1:
				System.out.println(1);
			case 2:
				System.out.println(2);
			case 3:
				System.out.println(3);
			case 4:
				System.out.println(4);
			case 5:
				System.out.println(5);
				i = 1;
//				continue _1; continue can't be used outside of loop
//				for() {continue _1}; no statement between label and outer loop
			case 6:
				System.out.println(6);
			case 7:
				System.out.println(7);
			case 8:
				System.out.println(8);
			case 9:
				System.out.println(9);
			case 0:
				System.out.println(10);
				i = 0;
			default:
				System.out.println("default: i = " + i);
				break;
			}

			System.out.println("i = " + i);
		}
		
		System.out.println();
		{
			int i = -1;
			_1: switch (i) {
			case 1:
				System.out.println(1);
			case 2:
				System.out.println(2);
			case 3:
				System.out.println(3);
			case 4:
				System.out.println(4);
			case 5:
				System.out.println(5);
				i = 1;
//				break _1;
//				continue _1; continue can't be used outside of loop
//				for() {continue _1}; no statement between label and outer loop
			case 6:
				System.out.println(6);
			case 7:
				System.out.println(7);
			case 8:
				System.out.println(8);
			case 9:
				System.out.println(9);
			case 0:
				System.out.println(10);
				i = 0;
			default:
				i = 2;
				System.out.println("default: i = " + i);
				break _1;
			}

			System.out.println("i = " + i);
		}
		
	}

	@Test
	public void testDefaultInSwitch() {
		System.out.println();
		switch (5) {
		default:
			System.out.println("default:");
//			break;
		case 1:
			System.out.println(1);
		case 2:
			System.out.println(2);
		case 3:
			System.out.println(3);
		case 4:
			System.out.println(4);
		case 5:
			System.out.println(5);
		case 6:
			System.out.println(6);
		case 7:
			System.out.println(7);
		case 8:
			System.out.println(8);
		case 9:
			System.out.println(9);
		case 0:
			System.out.println(10);
		}
		
		System.out.println();
		switch (5) {
//			break;
		case 1:
			System.out.println(1);
		case 2:
			System.out.println(2);
		case 3:
			System.out.println(3);
		case 4:
			System.out.println(4);
		case 5:
			System.out.println(5);
		case 6:
			System.out.println(6);
		case 7:
			System.out.println(7);
		case 8:
			System.out.println(8);
		default:
			System.out.println("default:");
		case 9:
			System.out.println(9);
		case 0:
			System.out.println(10);
		}
		
		System.out.println();
		switch (-1) {
		default:
			System.out.println("default:");
//			break;
		case 1:
			System.out.println(1);
		case 2:
			System.out.println(2);
		case 3:
			System.out.println(3);
		case 4:
			System.out.println(4);
		case 5:
			System.out.println(5);
		case 6:
			System.out.println(6);
		case 7:
			System.out.println(7);
		case 8:
			System.out.println(8);
		case 9:
			System.out.println(9);
		case 0:
			System.out.println(10);
		}
		
		System.out.println();
		switch (-1) {
//			break;
		case 1:
			System.out.println(1);
		case 2:
			System.out.println(2);
		case 3:
			System.out.println(3);
		case 4:
			System.out.println(4);
		case 5:
			System.out.println(5);
		case 6:
			System.out.println(6);
		case 7:
			System.out.println(7);
		default:
			System.out.println("default:");
		case 8:
			System.out.println(8);
		case 9:
			System.out.println(9);
		case 0:
			System.out.println(10);
		}
	}
	
	@Test
	public void testSwitchWithVariable() {
		System.out.println();
		int i = 3;
		switch (i) {
//			break;
		case 1:
			System.out.println(1 + " var :" + i);
		case 2:
			System.out.println(2 + " var :" + i);
		case 3:
			System.out.println(3 + " var :" + i);
		case 4:
			System.out.println(4 + " var :" + i);
		case 5:
			System.out.println(5 + " var :" + i);
			i = -1; //continue execution
		case 6:
			System.out.println(6 + " var :" + i);
		case 7:
			System.out.println(7 + " var :" + i);
		case 8:
			System.out.println(8 + " var :" + i);
		case 9:
			System.out.println(9 + " var :" + i);
		case 0:
			System.out.println(0 + " var :" + i);
		default:
			System.out.println("default:");
		}
	}

	@Test
	public void testSwitchWithStr() {
		System.out.println();
		{
			final String _str = "i"; //constant literal
			final String _random = String.valueOf(System.currentTimeMillis()); //non constant expression
			String str = "d";
			switch (str) {
			case "a":
				System.out.println("a");
				break;
			case "b":
				System.out.println("b");
				break;
			case "c":
				System.out.println("c");
				break;
			case "d":
				System.out.println("d");
				break;
			case "e":
				System.out.println("e");
				break;
			case "f":
				System.out.println("f");
				break;
			case "g":
				System.out.println("g");
				break;
			case "h":
				System.out.println("h");
				break;
			case _str: //constant expression can be "case"
				System.out.println(_str);
				break;
//			case _random: //constant expression can be "case"
//				System.out.println(_random);
//				break;
			default:
				System.out.println("default: str = " + str);
				break;
			}
		}
	}
	
	@Test
	public void testSwitchWithNull() {
		System.out.println();
		{
			final String _str = "i"; //constant literal
			final String _random = String.valueOf(System.currentTimeMillis()); //non constant expression
			String str = "d"; //null; //switch will throw NullPointerException for null value
//			switch (null) { can't take null literal
			switch (str) {
			case "a":
				System.out.println("a");
				break;
			case "b":
				System.out.println("b");
				break;
//			case null: doesn't accept null as case expression
			case "c":
				System.out.println("c");
				break;
			case "d":
				System.out.println("d");
				break;
			case "e":
				System.out.println("e");
				break;
			case "f":
				System.out.println("f");
				break;
			case "g":
				System.out.println("g");
				break;
			case "h":
				System.out.println("h");
				break;
			case _str: //constant expression can be "case", except null
				System.out.println(_str);
				break;
//			case _random: //constant expression can be "case"
//				System.out.println(_random);
//				break;
			default:
				System.out.println("default: str = " + str);
				break;
			}
		}
	}
	
	
//	const String _str = "i"; no use of const
}
//abstract, class
abstract class Abstract {
	abstract void method();
//	abstract Object obj;
}

abstract interface AbstractInterface {
	abstract void method();
	void foo();
}