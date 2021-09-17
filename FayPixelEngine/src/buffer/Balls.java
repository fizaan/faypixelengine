package buffer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import fay.engine.FayBuffer;
import tests.Ball;

@SuppressWarnings("serial")
public class Balls extends FayBuffer {
	
	private List<Ball> balls;
	
	public static void main(String args[]) {
		initializeGameSettingsManually = true;
		Balls b = new Balls(400,400,1,1, "Random balls");
		b.OnUserCreate();
		initializeGameSettingsManually = false;
	}

	public Balls(int len, int hei, int pX, int pY, String title) {
		super(len,hei,pX,pY,title);
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		clearConsole(Color.WHITE);
		for(Ball ball: balls) {
			ball.constantVel(getConsoleWidth(), getConsoleHeight());
			fillCircle((int)ball.location.x, (int)ball.location.y, ball.radius(), ball.radius(), ball.colour());
		}
		return true;
	}

	@Override
	public boolean OnUserCreate() {		
		balls = new ArrayList<Ball>();
		for(int i = 0; i < 20; i++) {
		 Ball ball = new Ball(getConsoleWidth()/2, getConsoleHeight()/2);
		 ball.setRadius(20);
		 ball.velocity.setPosition(ball.velocity.x * 3, ball.velocity.y * 3);
		 balls.add(ball);
		}
		return true;
	}

}
