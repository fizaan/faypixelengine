package sansbuffer;

import java.awt.Color;

import fay.engine.FaySansBuffer;
import fay.math.Vf2d;
import tests.Ball;

// https://www.desmos.com/calculator

@SuppressWarnings("serial")
public class BallParabola extends FaySansBuffer {
	
	// a ball showing a parabolic movement using equation
	// and a ball acting as mario from Meth Meth Method Mario
	// JS vidoes.
	
	// Aug 30: New formula from desmos.com/caculator
	// y = -w * (x - m) * (x - m) + h
	// where 
	//	w = jump width (the more the narrower, the less the wider)
	//  h = max height
	//  m = curve midpoint at the x-axis. Moves it to either left or right
	private Ball ballParabola, ballMario, experimentBall;
	
	public static void main(String args[]) {
		new BallParabola(400,400,1,1, "Ball parabola");
	}

	public BallParabola(int len, int hei, int pX, int pY, String title) {
		super(len,hei,pX,pY,title);
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		clearConsole(Color.WHITE);
		drawBallParabola();
		drawBallMario(fElapsedTime);
		drawExpBall();
		return true;
	}
	
	private void drawBallParabola() {
		fillCircle((int)ballParabola.location.x, (int)ballParabola.location.y, ballParabola.radius(), ballParabola.radius(), ballParabola.colour());
		YAsFunctionOfX();
	}
	
	private void drawExpBall() {
		fillCircle((int)experimentBall.location.x, (int)experimentBall.location.y, experimentBall.radius(), experimentBall.radius(), experimentBall.colour());
		YAsFunctionOfX(200,(1.0f/80.0f),150,experimentBall);
	}
	
	private void drawBallMario(float fElapsedTime) {
		fillCircle((int)ballMario.location.x, (int)ballMario.location.y, ballMario.radius(), ballMario.radius(), ballMario.colour());
		updateMarioPOS(fElapsedTime);
	}
	
	private void updateMarioPOS(float fElapsedTime) {
	 ballMario.location.add(Vf2d.mul(ballMario.velocity, fElapsedTime));
	 float gravity = 2000.0f;
	 ballMario.velocity.y += gravity * fElapsedTime;
	 resetMarioPos();
	}
		
	private void resetMarioPos() {
	 // check if mario has landed, reset his position and speed
	 if(ballMario.location.y > 320) {
	   ballMario.location.y = 320;
	   ballMario.velocity.setPosition(200, -600);
	 }
	 
	 if(ballMario.location.x + ballMario.radius() > 400)
	  ballMario.location.x = 400 - ballMario.location.x - ballMario.radius();	
	}
	
	// y = -(x^2/a) + bx
	private void YAsFunctionOfX() {
		ballParabola.velocity.x += 0.1f;
		ballParabola.velocity.y = -(ballParabola.velocity.x * ballParabola.velocity.x) / 23 + (8 * ballParabola.velocity.x);
		ballParabola.velocity.y = getConsoleHeight() - ballParabola.radius() - ballParabola.velocity.y;
		
		if(ballParabola.velocity.y > getConsoleHeight() - ballParabola.radius())
			ballParabola.velocity.setPosition(0,getConsoleHeight()-20);
	}
	
	// e.g. h = 200, w = 0.5, m = 40
	// y - -0.5(x - 40)^2 + 200
	private void YAsFunctionOfX(int h, float w, int m, Ball b) {
		b.velocity.x += 0.1f;
		b.velocity.y = -1 * w * (b.velocity.x - m) * ( b.velocity.x - m) + h;
		
		if(b.velocity.x > m)
			if(b.velocity.y < 0)
				b.velocity.setPosition(1, 1);
	}

	@Override
	public boolean OnUserCreate() {
		ballParabola = new Ball(0,0,0,0);
		ballParabola.velocity.setPosition(0,getConsoleHeight()-20);
		ballParabola.location = ballParabola.velocity;
		ballParabola.setRadius(20);
		
		ballMario = new Ball(64,320);
		ballMario.velocity.setPosition(200, -600);
		ballMario.setRadius(20);
		
		experimentBall = new Ball(0,0,0,0);
		experimentBall.velocity.setPosition(1,1);
		experimentBall.location = experimentBall.velocity;
		experimentBall.setRadius(20);
		
		return true;
	}

}
