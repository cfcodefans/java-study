package cf.study.misc.algo.datastructure;

import org.junit.Test;

public class Point {
	double x, y;
	public Point() {
		x = Math.random();
		y = Math.random();
	}
	
//	public Point(double x, double y) {
//		this.x = x;
//		this.y = y;
//	}
	
	double r() {
		return Math.sqrt(x*x + y*y);
	}
	
	double theta() {
		return Math.atan2(y, x);
	}
	
	double distance(Point p) {
		double dx = x - p.x;
		double dy = y - p.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}
	
	@Test
	public void testClosestPoint() {
		int cnt = 0; 
		int N = 10;
		double d = Math.random();
		
		Point[] a = new Point[N];
		for (int i = 0; i < a.length; i++) {
			a[i] = new Point();
		}

		for (int i = 0; i < a.length; i++) {
			for (int j = i + 1; j < a.length; j++) {
				if (a[i].distance(a[j]) < d) cnt++;
			}
		}
		
		System.out.println(String.format("%d pairs closer than %f", cnt, d));
	}
}
