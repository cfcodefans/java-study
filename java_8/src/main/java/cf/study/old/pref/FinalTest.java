package cf.study.pref;

import org.junit.Test;

import cf.study.utils.TimeCounter;

/**
 * conclusion, final modifiers on parameters don't seem to bring any difference on performance 
 * 
 * @author fan
 *
 */
public class FinalTest {

	@Test
	public void testFinalPref() {
		TimeCounter.start("normalParam");
		int c = 8;
		
		normalParam(c);
		TimeCounter.end();
		
		TimeCounter.start("finalParam");
		finalParam(c);
		TimeCounter.end();
		
		TimeCounter.start("normalParam");
		normalParam(c);
		TimeCounter.end();
		
		TimeCounter.start("finalParam");
		finalParam(c);
		TimeCounter.end();
		
		TimeCounter.start("normalParam");
		normalParam(c);
		TimeCounter.end();
		
		TimeCounter.start("finalParam");
		finalParam(c);
		TimeCounter.end();
		
		TimeCounter.start("normalParam");
		normalParam(c);
		TimeCounter.end();
		
		TimeCounter.start("finalParam");
		finalParam(c);
		TimeCounter.end();

		TimeCounter.start("normalParam");
		normalParam(c);
		TimeCounter.end();
		
		TimeCounter.start("finalParam");
		finalParam(c);
		TimeCounter.end();
	}
	
	public void finalParam(final int i) {
		if (i <= 0) {
			return;
		}
		for (int j = 0; j < 10; j++) {
			finalParam(i - 1);
		}
	}
	
	@Test
	public void testFinalPref_() {
		Param c = new Param();
		c.i = 8;
		
		TimeCounter.start("normalParam");
		normalParamObj(c);
		TimeCounter.end();
		
		TimeCounter.start("finalParam");
		finalParamObj(c);
		TimeCounter.end();
		
		TimeCounter.start("normalParam");
		normalParamObj(c);
		TimeCounter.end();
		
		TimeCounter.start("finalParam");
		finalParamObj(c);
		TimeCounter.end();
		
		TimeCounter.start("normalParam");
		normalParamObj(c);
		TimeCounter.end();
		
		TimeCounter.start("finalParam");
		finalParamObj(c);
		TimeCounter.end();
		
		TimeCounter.start("normalParam");
		normalParamObj(c);
		TimeCounter.end();
		
		TimeCounter.start("finalParam");
		finalParamObj(c);
		TimeCounter.end();

		TimeCounter.start("normalParam");
		normalParamObj(c);
		TimeCounter.end();
		
		TimeCounter.start("finalParam");
		finalParamObj(c);
		TimeCounter.end();
		
		TimeCounter.start("normalVars");
		normalVars();
		TimeCounter.end();
		
		TimeCounter.start("finalVars");
		finalVars();
		TimeCounter.end();
		
		TimeCounter.start("normalObjVars");
		normalObjVars();
		TimeCounter.end();
		
		TimeCounter.start("finalObjVars");
		finalObjVars();
		TimeCounter.end();
	}
	
	public void normalParam(int i) {
		if (i <= 0) {
			return;
		}
		for (int j = 0; j < 10; j++) {
			normalParam(i - 1);
		}
	}
	
	public void finalParamObj(final Param i) {
		if (i.i <= 0) {
			return;
		}
		for (int j = 0; j < 10; j++) {
			i.i--;
			finalParamObj(i);
			i.i++;
		}
	}
	
	public void normalParamObj(Param i) {
		if (i.i <= 0) {
			return;
		}
		for (int j = 0; j < 10; j++) {
			i.i--;
			normalParamObj(i);
			i.i++;
		}
	}
	
	public void normalVars() {
		int v = 1;
		int sum = 0;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			sum = sum + v;
		}
	}
	
	public void finalVars() {
		final int v = 1;
		int sum = 0;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			sum = sum + v;
		}
	}
	
	public void normalObjVars() {
		Param v = new Param();
		v.i = 1;
		int sum = 0;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			sum = sum + v.i;
		}
	}
	
	public void finalObjVars() {
		final Param v = new Param();
		v.i = 1;
		int sum = 0;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			sum = sum + v.i;
		}
	}
}

class Param {
	int i;
}
