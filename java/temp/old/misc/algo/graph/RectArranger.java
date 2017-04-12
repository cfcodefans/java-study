package cf.study.misc.algo.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.math3.util.ArithmeticUtils;


public class RectArranger {
	private static final int IMG_NUM = 6;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}
	static JFrame fra = new JFrame("RectArranger");

	protected static void createAndShowGUI() {
		fra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fra.setLayout(new BorderLayout());

		fra.setVisible(true);
		
		JPanel panel = new JPanel();
		//panel.setLayout(new FlowLayout(FlowLayout.LEADING));
		panel.setLayout(null);
		panel.setBounds(0, 0, 640, 480);
		panel.setPreferredSize(new Dimension(640, 480));//
		List<JPanel> imgs = generateRandImgs(640, 480);
		layoutImgs(panel, imgs);
		panel.setBackground(Color.white);
		
		fra.getContentPane().add(panel, BorderLayout.CENTER);
		fra.pack();
	}
	

	
	private static Rectangle getBasicBlock(List<JPanel> imgs) {
		int w = imgs.get(0).getWidth();
		int h = imgs.get(0).getHeight();
		for (JPanel img : imgs) {
			w = ArithmeticUtils.gcd(w, img.getWidth());
			h = ArithmeticUtils.gcd(w, img.getHeight());
		}
		
		Rectangle rr = new Rectangle(w, h);
		return rr;
	}

	private static void layoutImgs(JPanel panel, List<JPanel> imgs) {
		List<Rectangle> rects = new ArrayList<Rectangle>();
		
		for (JPanel img : imgs) {
			img.setBounds(0, 0, img.getPreferredSize().width, img.getPreferredSize().height);
			rects.add(new Rectangle(img.getPreferredSize()));
		}
		
		layoutRects(new Rectangle(panel.getPreferredSize()), rects);
		
		for (int i = 0, j = rects.size(); i < j; i++) {
			JPanel img = imgs.get(i);
			img.setBounds(rects.get(i));
			panel.add(img);
		}
	}
	
	private static boolean isRectsFit(Rectangle c, Rectangle r1, Rectangle r2) {
		return !(r1.height + r2.height > c.height && r1.width + r2.width > c.width);
	}
	
	private static void layoutRects(Rectangle c, List<Rectangle> rects) {
		
	}
	
	private static Rectangle isOverlap(Rectangle r, List<Rectangle> rects) {
		for (Rectangle rect : rects) {
			if (rect.intersects(r)) {
				return rect;
			}
		}
		return null;
	}
	
	private static Comparator<Rectangle> MIN_TOP_CMP = new Comparator<Rectangle>() {
		@Override
		public int compare(Rectangle o1, Rectangle o2) {
			return Integer.valueOf(o1.y + o1.height).compareTo(o2.y + o2.height);
		}
	};
	
	private static void tryPutRects(Rectangle c, List<Rectangle> rects) {
		List<Rectangle> putRects = new ArrayList<Rectangle	>();
		putRects.add(rects.get(0));
		rects.remove(0);
		for (ListIterator<Rectangle> li = rects.listIterator(); li.hasNext();) {
			Rectangle rect = li.next();
			Rectangle lastPutRect = putRects.get(putRects.size() - 1);
			rect.x = lastPutRect.x + lastPutRect.width;
			rect.y = lastPutRect.y;
			if (rect.x + rect.width > c.width) {
				rect.x = 0;
				Rectangle minHeight = Collections.min(putRects, MIN_TOP_CMP);
				rect.y = minHeight.y + minHeight.height;
			} else if (isOverlap(rect, putRects) != null) {
				
			} else {
				putRects.add(rect);
			}
		}
	}
	
	protected static List<JPanel> generateRandImgs(int width, int height) {
		List<JPanel> imgs = new ArrayList<JPanel>(10);
		int s = width * height;
		List<Integer> ts = new ArrayList<Integer>();
		ts.add(0);
		for (int i = 0; i < IMG_NUM; i++) {
			ts.add(Math.round(s * RandomUtils.nextFloat()));
		}
		Collections.sort(ts);
		ts.add(s);
		
		for (int i = 0; i < IMG_NUM; i++) {
			String title = String.valueOf(i);
			
			int minWidth = SwingUtilities.computeStringWidth(fra.getFontMetrics(fra.getFont()), title);
			int minHeight = fra.getFontMetrics(fra.getFont()).getHeight();
			
			int si = ts.get(i + 1) - ts.get(i);
			int wi = minWidth + Math.round(RandomUtils.nextFloat() * width);

			Dimension d = new Dimension(wi, si / wi);
			
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JLabel label = new JLabel(title);
			label.setForeground(Color.white);
			label.setBorder(new LineBorder(Color.white));
			panel.add(label, BorderLayout.CENTER);
			panel.setPreferredSize(d);
			panel.setBackground(Color.getHSBColor(RandomUtils.nextFloat(), RandomUtils.nextFloat(), RandomUtils.nextFloat()));
			
			imgs.add(panel);
		}
		
		System.out.println(width * height);
		int sumS = 0;
		for (JPanel p : imgs) {
			sumS += p.getPreferredSize().getWidth() * p.getPreferredSize().getHeight();
		}
		System.out.println(sumS);
		
		return imgs;
	}
}
