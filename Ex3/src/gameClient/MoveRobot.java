package gameClient;

import java.awt.event.MouseEvent;
import java.awt.AWTException;
import java.awt.Robot;

public class MoveRobot {
	private Robot robot;
	private Runtime runtime;
	private int x;
	private int y;

	public void Move(MouseEvent e) {
		this.x = e.getX();
		this.y = e.getY();
		double alpha = 0;
		runtime = Runtime.getRuntime();
		try {
			this.robot = new Robot();
		} catch (AWTException e2) {
			e2.printStackTrace();
		}
		robot.delay(1000);
		robot.mouseMove(x, y);
	}

}
