package cf.study.misc.algo.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;


class Point {
	int x;
	int y;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	static int dist(Point p1, Point p2) {
		int dx = p1.x - p2.x;
		int dy = p1.y - p2.y;
		return (int)Math.floor(Math.sqrt( dx*dx + dy*dy ));
	}
	
	public String toString() {
		return String.format("{x: %d, y: %d}", x, y);
	}
	
	public static String toPointsString(Point[] ps) {
		return StringUtils.join(ps, ',');
	}
}



class XCmp implements Comparator<Point> {
	@Override
	public int compare(Point o1, Point o2) {
		return Integer.valueOf(o1.x).compareTo(Integer.valueOf(o2.x));
	}
}

class YCmp implements Comparator<Point> {
	@Override
	public int compare(Point o1, Point o2) {
		return Integer.valueOf(o1.y).compareTo(Integer.valueOf(o2.y));
	}
}

public class GraphTest {
	static Point[] getSmallestDistancePointPair(Point[] points) {
		Point p1 = points[0], p2 = points[0];
		int d = Integer.MAX_VALUE;
		
		for (Point _p1 : points) {
			for (Point _p2 : points) {
				if (_p1 == _p2) {
					continue;
				}
				int di = Point.dist(_p1, _p2);
				if (d > di) {
					d = di;
					p1 = _p1;
					p2 = _p2;
				}
				cmpCnt++;
			}
		}
		
		return new Point[] {p1, p2};
	}
	
	Point[] testPoints;
	
	static long cmpCnt = 0;
	
	@Before
	public void initTestPoints() {
		int cnt = 1000;
		testPoints = new Point[cnt];
		for (int i = 0; i < cnt; i++) {
			testPoints[i] = new Point();
		}
		List<Integer> il = new ArrayList<Integer>();
		for (int i = 0; i < cnt; i++) {
			il.add(i*10);
		}
		Collections.shuffle(il);
		for (int i = 0; i < cnt; i++) {
			testPoints[i].x = il.get(i);
		}
		Collections.shuffle(il);
		for (int i = 0; i < cnt; i++) {
			testPoints[i].y = il.get(i);
		}
	}
	
	@Test
	public void test1() {
		//System.out.println(Point.toPointsString(testPoints));
		cmpCnt = 0;
		long t = System.currentTimeMillis();
		System.out.println(Point.toPointsString(getSmallestDistancePointPair(testPoints)));
		System.out.println(System.currentTimeMillis() - t);
	}
	
	@Test
	public void test2() {
		Arrays.sort(testPoints, new XCmp());
		long t = System.currentTimeMillis();
		System.out.println(Point.toPointsString(getSmallestDistancePointPair_(testPoints)));
		System.out.println(System.currentTimeMillis() - t);
	}
	
	@Test
	public void test3() {
		cmpCnt = 0;
		long t = System.currentTimeMillis();
		Point[] pair1 = getSmallestDistancePointPair(testPoints);
		System.out.println(Point.toPointsString(pair1) + " distance: " + Point.dist(pair1[0], pair1[1]));
		System.out.println(String.format("%d %d",System.currentTimeMillis() - t,cmpCnt));
		t = System.currentTimeMillis();
		cmpCnt = 0;
		Point[] pair2 = getSmallestDistancePointPair_(testPoints);
		System.out.println(Point.toPointsString(pair2) + " distance: " + Point.dist(pair2[0], pair2[1]));
		System.out.println(String.format("%d %d",System.currentTimeMillis() - t,cmpCnt));
	}
	
	public static Point[] getSmallestDistancePointPair_(Point[] points)	 {
		if (points.length <= 6) {
			return getSmallestDistancePointPair(points);
		}
		
		Point[] leftPart = (Point[])ArrayUtils.subarray(points, 0, points.length / 2);
		Point[] leftPair = getSmallestDistancePointPair_(leftPart);
		Point[] rightPart = (Point[])ArrayUtils.subarray(points, points.length / 2 + 1, points.length -1 );
		Point[] rightPair = getSmallestDistancePointPair_(rightPart);
		
		int ld = Point.dist(leftPair[0], leftPair[1]);
		int rd = Point.dist(rightPair[0], rightPair[1]);
		
		
		Point p1 = points[0], p2 = points[0];
		int d = Integer.MAX_VALUE;
		for (Point lp : leftPart) {
			for (Point rp : rightPart) {
				if (lp == rp) {
					continue;
				}
				int di = Point.dist(lp, rp);
				if (d > di) {
					d = di;
					p1 = lp;
					p2 = rp;
				}
				cmpCnt++;
			}
		}
		Point[] betweenPair = new Point[] {p1, p2};
		int bd = Point.dist(betweenPair[0], betweenPair[1]);
		
		int md = Math.min(rd, Math.min(ld, bd));
		if (md == ld) {
			return leftPair;
		} else if (md == rd) {
			return rightPair;
		} else {
			return betweenPair;
		}
	}
}
