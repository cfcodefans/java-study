package cf.study.misc.quiz;

import java.awt.geom.Rectangle2D;

import org.junit.Test;




public class Interview {
	
//	public static class TestRectangle extends Rectangle {
//		private static final long serialVersionUID = -795478552952540567L;
//
//		public TestRectangle(int x, int y, int width, int height) {
//			super(x, y, width, height);
//		}
//		
//		public Rectangle intersection(Rectangle r) {
//	        int tx1 = this.x;
//	        int ty1 = this.y;
//	        int rx1 = r.x;
//	        int ry1 = r.y;
//	        long tx2 = tx1; tx2 += this.width;
//	        long ty2 = ty1; ty2 += this.height;
//	        long rx2 = rx1; rx2 += r.width;
//	        long ry2 = ry1; ry2 += r.height;
//	        if (tx1 < rx1) tx1 = rx1;
//	        if (ty1 < ry1) ty1 = ry1;
//	        if (tx2 > rx2) tx2 = rx2;
//	        if (ty2 > ry2) ty2 = ry2;
//	        tx2 -= tx1;
//	        ty2 -= ty1;
//	        // tx2,ty2 will never overflow (they will never be
//	        // larger than the smallest of the two source w,h)
//	        // they might underflow, though...
//	        if (tx2 < Integer.MIN_VALUE) tx2 = Integer.MIN_VALUE;
//	        if (ty2 < Integer.MIN_VALUE) ty2 = Integer.MIN_VALUE;
//	        return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
//	    }
//		
//	    public boolean intersects(Rectangle r) {
//	        long tw = this.width;
//	        long th = this.height;
//	        long rw = r.width;
//	        long rh = r.height;
//	        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
//	            return false;
//	        }
//	        int tx = this.x;
//	        int ty = this.y;
//	        int rx = r.x;
//	        int ry = r.y;
//	        rw += rx;
//	        rh += ry;
//	        tw += tx;
//	        th += ty;
//	        //      overflow || intersect
//	        return ((rw < rx || rw > tx) &&
//	                (rh < ry || rh > ty) &&
//	                (tw < tx || tw > rx) &&
//	                (th < ty || th > ry));
//	    }
//	}
	
    public int solution(int K, int L, int M, int N, int P, int Q, int R, int S) {
    	double area = compute(K, L, M, N, P, Q, R, S);
    	return area > Integer.MAX_VALUE ? -1 : (int)area;
    }
    
    public double compute(double K, double L, double M, double N, double P, double Q, double R, double S) {
    	Rectangle2D.Double rect1 = new Rectangle2D.Double(K, L, M - K, N - L);
    	Rectangle2D.Double rect2 = new Rectangle2D.Double(P, Q, R - P, S - Q);
    	
    	double area1 = rect1.height * rect1.width;
    	double area2 = rect2.height * rect2.width;
    	
    	if (rect1.contains(rect2)) {
    		return area1;
    	}
    	
    	if (rect2.contains(rect1)) {
    		return area2;
    	}
    	
    	if (!rect1.intersects(rect2)) {
    		return area1 + area2;
    	}
    	
    	Rectangle2D.Double intersetion = new Rectangle2D.Double(); 
    	Rectangle2D.Double.intersect(rect1, rect2, intersetion);;
    	double areaForIntersection = intersetion.height * intersetion.width;
    	return area1 + area2 - areaForIntersection; 
    }
    
    @Test
    public void test() {
//    	int re = solution(-4, 1, 2, 6, 0, -1, 4, 3);
//    	System.out.println(re);
//    	
//    	re = solution(-4, 1, 2, 6, -4, 1, 2, 6);
//    	System.out.println(re);
//    	
//    	re = solution(0, -1, 4, 3, 0, -1, 4, 3);
//    	System.out.println(re);
//    	
//    	re = solution(0, 0, 10, 10, 2, 2, 8, 8);
//    	System.out.println(re);
//    	
//    	re = solution(2, 2, 8, 8, 0, 0, 10, 10);
//    	System.out.println(re);
//    	
//    	re = solution(0, 0, 3, 5, 6, 0, 8, 5);
//    	System.out.println(re);
//    	
//    	re = solution(0, 0, 2147483647, 2, 0, 0, 1, 1);
//    	System.out.println(re);
//    	
//    	re = solution(0, -1, 2147483647, 1, -1, 0, 2147483647, 2);
//    	System.out.println(re);
    	
    	int re = solution(-50, 0, 2147483647, 6, -100, 0, 2147483647, 6);
    	System.out.println(re);
    }
}
