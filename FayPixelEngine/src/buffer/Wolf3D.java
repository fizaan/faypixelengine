// https://en.wikipedia.org/wiki/Wolfenstein_3D

package buffer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import fay.engine.FayBuffer;
import shiffman.Ray;
import shiffman.StaticHelper;
import shiffman.Wall;

@SuppressWarnings("serial")
public class Wolf3D extends FayBuffer {
	
	private List<Ray> rays;
	private List<Wall> walls;
	private float angleStart = -45;
	private float angleEnd = 45;
	private float angleInc;
	private View playerEye;
	private int gap = 1;
	
	public static void main(String args[]) {
		
		// very important to do this
		// why? Think about it. 
		initializeGameSettingsManually = true;
		
		Wolf3D w = new Wolf3D(200,200,1,1, "Wolf 3D");
		
		initializeGameSettingsManually = false;
		
		// must call this manually in this case
		w.OnUserCreate();
		
	}
	
	public Wolf3D(int len, int hei, int pX, int pY, String title) {
		super(len,hei,pX,pY,title);
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		clearConsole(Color.BLACK);
		if(playerEye != null) {
		 handleInput();
		 StaticHelper.showWalls(walls, this);
		 StaticHelper.lookAround(rays, walls, this);
		 playerEye.setRays(rays);
		 StaticHelper.showRays(rays, this);
		}
		return true;
	}

	@Override
	public boolean OnUserCreate() {
		angleInc = 0.375f;	
		
		walls = new ArrayList<Wall>();
		
		// BORDER
		StaticHelper.createBlock(gap, gap, getConsoleWidth() - gap * 2, getConsoleHeight() - gap * 2, walls);
		
		// blocks
		StaticHelper.createBlock(25,gap,25,25,walls);
		StaticHelper.createBlock(gap,getConsoleHeight() - 25 - gap, 25, 25, walls);
		StaticHelper.createBlock(150,35,25,50,walls);
		StaticHelper.createBlock(125,getConsoleHeight() - 50 - gap, 75,25,walls);
		
		rays = StaticHelper.arrayOfRays(getConsoleWidth() / 2, getConsoleHeight() / 2, angleStart, angleEnd, angleInc);
		
		initializeGameSettingsManually = true;
		playerEye = new View(rays.size(),100,2,2,"Player Eye");
		playerEye.setRays(rays);
		playerEye.setBirdEyeWH(getConsoleWidth(), getConsoleHeight());
		initializeGameSettingsManually = false;
		playerEye.OnUserCreate();
		
		return true;
	}
	
	private void handleInput() {
		switch(getEventCode()) {
			case KeyEvent.VK_S:
				// rotate CW
				angleStart += 5;
				angleEnd += 5;
				rays = StaticHelper.rotate(mouseXAdjust(), mouseYAdjust(), angleStart, angleEnd, angleInc);
			break;
			
			case KeyEvent.VK_A:
				// rotate CCW
				angleStart -= 5;
				angleEnd -= 5;
				rays = StaticHelper.rotate(mouseXAdjust(), mouseYAdjust(), angleStart, angleEnd, angleInc);
			break;
			
			case KeyEvent.VK_F:
				// fish eye on/off
				playerEye.fishEYEOff = !playerEye.fishEYEOff;
				System.out.println(playerEye.fishEYEOff ? "Normal" : "Fish eye!");
			break;
			
			case KeyEvent.VK_P:
				// Use position heading on/off
				playerEye.usingPositionHeading = !playerEye.usingPositionHeading;
				System.out.println(playerEye.usingPositionHeading ? "Positiong heading" : "Direction heading");
			break;
			
			default:
				//
			break;
		}
	}

}
