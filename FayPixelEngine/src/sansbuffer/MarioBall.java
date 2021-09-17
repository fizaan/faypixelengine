package sansbuffer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import fay.events.EventListener;
import fay.math.Vf2d;
import fay.original.FaySansBuffer;
import fay.utils.FayUtils;
import shiffman.Ray;
import shiffman.StaticHelper;
import shiffman.Wall;

// Mario is a Player that extends Ball
// Usage:
// 	s = left, d = right, j = jump, r = reset
//  constantVel = set to true if you want to move in constant velocity
//	else 'mario' (ball) will accelerate.

@SuppressWarnings("serial")
public class MarioBall extends FaySansBuffer {
	
	private Player mario;
	Wall top, right, left;
	private String keys;
	
	public static void main(String args[]) {
		new MarioBall(900,400,1,1, "Mario Ball");
	}

	public MarioBall(int len, int hei, int pX, int pY, String title) {
		super(len,hei,pX,pY,title);
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		clearConsole(Color.WHITE);
		KBInput(fElapsedTime);
		
		// there are two methods which can tell you
		// the angle at which mario will jump. Use
		// either one, not both.
		// 	- drawWallsAndBallJumpAngle()
		// 	- drawSmallJumpAngleVector()
		
		//drawWallsAndBallJumpAngle();
		drawSmallJumpAngleVector();
		drawBall();
		showBallStatus();
		return true;
	}
	
	private void drawBall() {
		fillCircle((int)mario.location.x, (int)mario.location.y, mario.radius(), mario.radius(), mario.colour());
	}
	
	private void drawWallsAndBallJumpAngle() {
		// walls:
		drawLine((int)top.a.x, (int)top.a.y, (int)top.b.x, (int)top.b.y, Color.BLUE);
		drawLine((int)right.a.x, (int)right.a.y, (int)right.b.x, (int)right.b.y, Color.BLUE);
		drawLine((int)left.a.x, (int)left.a.y, (int)left.b.x, (int)left.b.y, Color.BLUE);
		
		// create a ray whose position matches the ball's position
		Ray ray = new Ray(mario.location.x, mario.location.y, 
				(float)Math.cos(StaticHelper.degToRad(mario.angle())), 
				(float)Math.sin(StaticHelper.degToRad(mario.angle())));
		
		// cast ray at all the available walls. Return the position vector
		// if it intersects, null otherwise.
		Vf2d v1 = FayUtils.cast(ray, top);   // cast ray at top wall
		Vf2d v2 = FayUtils.cast(ray, right); // cast ray at right wall
		Vf2d v3 = FayUtils.cast(ray, left);  // cast ray at left wall 
		
		// show the ray if it is hitting any of the walls
		// this is the angle mario will jump to when 'J' key is pressed
		// along with eith 'S' or 'D' keys.
		if(v1 != null)
			translateVf2dFromTo(ray.position, v1, Color.red, 1.0f);
		else if(v2 != null)
			translateVf2dFromTo(ray.position, v2, Color.red, 1.0f);
		else if(v3 != null)
			translateVf2dFromTo(ray.position, v3, Color.red, 1.0f);

	}
	
	private void showBallStatus() {
		String s = "s = left, d = right, j = jump\nr = reset\n--------------\n";
		s += String.format("Position: %.2f %.2f\nVelocity: %.3f, %.2f\nAcceleration: %.4f, %.4f\nGravity: %.5f, %.5f\nAirbourne: %b\nKey pressed: %d: %s", 
		 mario.location.x, mario.location.y,
		 mario.velocity.x, mario.velocity.y,
		 mario.acceleration.x, mario.acceleration.y,
		 mario.gravity.x, mario.gravity.y, mario.airbourne(), EventListener.keyPressedEvent, keys);
		setStatus(s);
	}
	
	private void drawSmallJumpAngleVector() {
	 Vf2d v2 = Vf2d.normalizedAngle(mario.angle());
	 v2.mul(50);
	 translateVf2dPoint(v2, mario.location.clone(), Color.BLUE);
	}
	
	private void KBInput(float fElapsedTime) {
		switch(EventListener.keyPressedEvent) {
			case KeyEvent.VK_R:
				mario.reset();
			break;
			
			case KeyEvent.VK_D:
				if(!mario.airbourne()) mario.location.y = mario.groundPosition();
				mario.setDirBoolean(true);
				mario.incAngle();
				mario.moveX(fElapsedTime);
				mario.updatePosition(getConsoleWidth(), getConsoleHeight());
				keys = "RIGHT";
			break;
			
			case KeyEvent.VK_S:
				if(!mario.airbourne()) mario.location.y = mario.groundPosition();
				mario.setDirBoolean(false);
				mario.decAngle();
				mario.moveX(fElapsedTime);
				mario.updatePosition(getConsoleWidth(), getConsoleHeight());
				keys = "LEFT";
			break;
			
			case KeyEvent.VK_D | KeyEvent.VK_J:
				mario.setDirBoolean(true);
				mario.incAngle();
				mario.velocity.setAngledPosition(mario.angle());
				mario.moveX(fElapsedTime);
				mario.updatePosition(getConsoleWidth(), getConsoleHeight());
				keys = "RIGHT+JUMP";
			break;
			
			case KeyEvent.VK_S | KeyEvent.VK_J:
				mario.setDirBoolean(false);
				mario.decAngle();
				mario.velocity.setAngledPosition(mario.angle());
				mario.moveX(fElapsedTime);
				mario.updatePosition(getConsoleWidth(), getConsoleHeight());
				keys = "LEFT+JUMP";
			break;
			
			case KeyEvent.VK_J:
				mario.velocity.setAngledPosition(mario.angle());
				mario.moveX(fElapsedTime);
				mario.updatePosition(getConsoleWidth(), getConsoleHeight());
				keys = "JUMP";
			break;
			
			case  KeyEvent.VK_D | KeyEvent.VK_K:
				if(mario.airbourne()) {
					mario.velocity.setAngledPosition(-mario.angle());
					mario.updateWithGravity(false);
				} else {
					mario.location.y = mario.groundPosition();
					mario.velocity.y = 0;
					mario.setDirBoolean(true);
					mario.incAngle();
					mario.moveX(fElapsedTime);
					mario.updatePosition(getConsoleWidth(), getConsoleHeight());
				}	
				keys = "DESCEND";
			break;
				
			default:
				if(!mario.airbourne()) {
					mario.setJumpAngle(Player.INIT_JUMP_ANGLE);
					mario.location.y = mario.groundPosition();
					mario.velocity.setPosition(0, 0);
					mario.acceleration.setPosition(0, 0);
				} else {
					mario.velocity.setAngledPosition(-mario.angle());	
					mario.updatePosition(getConsoleWidth(), getConsoleHeight());
				}
				keys = "N/A";
					
			break;
		}
	}

	@Override
	public boolean OnUserCreate() {
		mario = new Player();
		mario.setRadius(20);
		mario.initMotionBooleans(false, false);
		mario.initFloats(Player.INIT_JUMP_ANGLE, 0.5f, 0.005f, 0.3f, 0.05f, 0.03f, 1.2f);
		mario.initInts(getConsoleHeight() - 20 - mario.radius(), 5);
		mario.location.setPosition(mario.startXPosition(), mario.groundPosition());
		mario.setDirBoolean(false);
		
		// walls (optional)
		top = new Wall();
		right = new Wall();
		left = new Wall();
		top.createWall(0, 0, getConsoleWidth(), 0);
		right.createWall(getConsoleWidth()-2, 2, getConsoleWidth()-2, getConsoleHeight()-2);
		left.createWall(2, 2, 2, getConsoleHeight()-2);
		
		createStatusWindow(300, 200);
		return true;
	}

}
