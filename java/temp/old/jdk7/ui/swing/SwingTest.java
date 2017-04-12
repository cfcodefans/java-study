package cf.study.jdk7.ui.swing;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class SwingTest extends JFrame {
	public static void main(String args[]) {
		new SwingTest();
	}

	public SwingTest() {
		JLabel jlbHelloWorld = new JLabel("Hello World");
		add(jlbHelloWorld);
		this.setSize(100, 100);
		// pack();
		setVisible(true);
	}
}
