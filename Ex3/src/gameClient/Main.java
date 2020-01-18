package gameClient;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.event.MouseEvent;

public class Main {
	static JFrame jFrame = getFrame();

	public static void main(String[] args) {
		JComponent jComponent = new MyComponent();
		jFrame.add(jComponent);
		jFrame.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				MyComponent.xCord = e.getX();
				MyComponent.yCord = e.getY();
				jComponent.repaint();
			}
		});
	}

	static class MyComponent extends JComponent {
		public static int xCord;
		public static int yCord;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).drawString("Cordinates x: " + xCord + ", y: " + yCord, 50, 50);
		}
	}

	static JFrame getFrame() {
		JFrame jFrame = new JFrame();
		jFrame.setVisible(true);
		jFrame.setBounds(750, 250, 500, 500);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return jFrame;
	}
}
