package sansbuffer.JMario;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;
import fay.sprite.Sprite;
import fay.utils.FayUtils;
import sansbuffer.Player;
import shiffman.StaticHelper;
import shiffman.Wall;
import fay.events.EventListener;
import fay.events.MouseEventListener;
import fay.exceptions.FayPixelEngineException;
import fay.math.Vf2d;
import fay.original.FaySansBuffer;

@SuppressWarnings("serial")
public class JMario extends FaySansBuffer {
	
	private Vf2d tileBlock;
	private Player mario;
	private Map<String,BufferedImage> individualTiles;
	private String keys;
	private Map<Integer, JMarioRay> rays;
	private List<Wall> barriers;
	
	// 'movingBrickPosX/Y' are just to test the moving brick/barrier - they serve no other purpose
	private float movingBrickPosX, movingBrickPosY;
	
	// 'movingMulBricksPosX/Y' are just to test the moving multiple bricks/barriers - they also serve no other purpose
	private float movingMulBricksPosX, movingMulBricksPosY;
	
	// show barrier/wall/bricks outline ?
	private boolean productionMode;
	
	// use json file or background string?
	private boolean useJsonFile;
	
	public static void main(String args[]) {
	 // Viewport settings
	 FaySansBuffer.USING_VP = true;
	 FaySansBuffer.VP_LEN = 400;
	 FaySansBuffer.VP_HEI = 400;
	 FaySansBuffer.VP_X_OFFSET = 50;	
	 FaySansBuffer.VP_Y_OFFSET = 70;
	 
	 new JMario(640,640,1,1,"JMario");
	}
	
	public JMario(int len, int hei, int pX, int pY, String title) {
	 super(len,hei,pX,pY,title);
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
	 clearConsole(Color.WHITE);
	 
	 // uncomment below to see FR w/ ITER
	 // drawSansIT("sky", 0, 0, 25, 14);
	 // drawSansIT("ground", 0, 12, 25, 14);
	 // FR is about the same for both
	 
	 if (useJsonFile) 	drawBackground();
	 else				drawStringBackground();
	 KBInput(fElapsedTime);
	 MouseInput(fElapsedTime);
	 
	 if(productionMode) 
		 drawMario(fElapsedTime);
	 else { 
		 marioTileOutline();
		 drawSmallJumpAngleVector();
	 }
	 
	 // uncomment to show status box
	 showStatus();
	 
	 // Update viewport (camera)
	 updateVP(mario.location);
	 
	 return true;
	}
	
	private void drawMarioTile(BufferedImage img, int x, int y, int scaleX, int scaleY, boolean block) {
	 x = block ? x * img.getWidth() * scaleX : x * scaleX;
	 y = block ?  y * img.getHeight() * scaleY : y * scaleY;
	 drawScaledSprite(new Vf2d(x, y), img, scaleX, scaleY);
	}
	
	private void drawMario(float fElapsedTime) {
	 boolean bTileBasedSpacing = false;
	 drawMarioTile(
	 individualTiles.get("idleMario"), 
	 (int)mario.location.x, 
	 (int)mario.location.y, 1,1,bTileBasedSpacing);
	}
	
	// ::::::::::::::::::::::::::::::::::::::::::::::::::::
	// function 'drawStringBackground()'
	//
	// stackoverflow.com/questions/56538578
	// Get X and Y positions of a Pixel given the HEIGHT , 
	// WIDTH and index of a pixel in FLATTENED array 
	// representing the image.
	//
	// Note: If you use this approach, don't use
	// ray casting.
	// :::::::::::::::::::::::::::::::::::::::::::::::::::
	private void drawStringBackground() {
	 boolean bTileBasedSpacing = true;
	 int width = 40;
	 for(int index = 0; index < Background.bg.length(); index++) {
	  int y = index / width;
	  int x = index % width;
	  switch(Background.bg.charAt(index)) {
	   case '.' : drawMarioTile(individualTiles.get("sky"), x, y, 1, 1, bTileBasedSpacing); break;
	   case '#' : drawMarioTile(individualTiles.get("ground"), x, y, 1, 1, bTileBasedSpacing); break;
	  } // end switch
	 } // end for
	}
	
	private void drawBackground(Iterator<JSONObject> backgrounds, List<Wall> barriers) throws FayPixelEngineException {
	 boolean bTileBasedSpacing = true;
	 while (backgrounds.hasNext()) {
	  JSONObject jsonObj = backgrounds.next();
	  
	  @SuppressWarnings("unchecked")
	  Set<String> keys = jsonObj.keySet();
		  
	  for(String key:keys) {
	   String[] arr 	= jsonObj.get(key).toString().split(",");
	   String tile 		= arr[0];
	   int x 			= Integer.parseInt(arr[1]);
	   int y 			= Integer.parseInt(arr[2]);
	   int xLimit 		= x + Integer.parseInt(arr[3]);
	   int yLimit 		= y + Integer.parseInt(arr[4]);
	   
	   // optional
	   if(movingBrickPosY == -1 && tile.equals("ground")) {
		   int numberOfMulXBricks = 3;
		   movingBrickPosY = y * tileBlock.y;
		   movingMulBricksPosX = xLimit - numberOfMulXBricks;
		   movingMulBricksPosY = yLimit - numberOfMulXBricks;
	   }
	   // --- optional end
	   
	   // walls where rays can be cast to
	   createBlock(x, y, xLimit, yLimit, barriers);
	   
	   loopNDraw(tile, x, y, xLimit, yLimit, bTileBasedSpacing);
	  } // end for
	  
	 } // end while
	 
	// uncomment to test a moving brick
	   movingBricksTestWrapper();
	 
	// uncomment to draw a NB-Collision testing wall
	// drawTestWall();
	   
	// test multiple barriers SINGLE image
	// drawMarioTile(individualTiles.get("movingBarriers"), 50, 50, 1, 1, false);
	// movingMultipleBricksTest(50, 50, 3);
	 
	} // end function
	
	private void movingBricksTestWrapper() throws FayPixelEngineException {
	 
	 movingBrickTest(movingBrickPosX, movingBrickPosY - tileBlock.y);
	 
	 movingMultipleBricksTest(movingBrickPosX + tileBlock.x * 3, movingBrickPosY - tileBlock.y - 50, 3);
	 
	 // This is now absolete. Don't use it.
	 
	 //movingMultipleBricksTest(movingMulBricksPosX, movingMulBricksPosY);
	 
	 movingBrickPosX -= 1.0f;
	 
	 movingMulBricksPosX -= 0.1f;
	 
	 if(movingBrickPosX <= 0) movingBrickPosX = getConsoleWidth() - tileBlock.x;
	
	}
	
	private void drawTestWall() {
	 // testing walls
	 Wall testWall1 = StaticHelper.createWall(10, 10, 395, 10);
	 
	 // this wall exposes a bug while you try to jump under it.
	 // question is, can you live with it?
	 Wall testWall2 = StaticHelper.createWall(100, 100, 200, 100);
	 
	 barriers.add(testWall1);
	 barriers.add(testWall2);
	 StaticHelper.showWall(testWall1, Color.RED, this);
	 StaticHelper.showWall(testWall2, Color.RED, this);
	}
	
	private void loopNDraw(String tile, int x, int y, int xLimit, int yLimit, boolean bTileBasedSpacing) 
			throws FayPixelEngineException {
	 // Throw exception if 'xLimit < x' or 'yLimit < y'
	 if(xLimit <= x || yLimit <= y) 
	  throw new FayPixelEngineException(String.format("xLimit/yLimit (%d, %d) must be > x/y (%d, %d)",xLimit, yLimit, x, y));
	 
	 if(productionMode)
	  for(int i = x; i < xLimit; i++)
	   for(int j = y; j < yLimit; j++)
	    drawMarioTile(individualTiles.get(tile), i, j, 1, 1, bTileBasedSpacing);
	} // end function
	
	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	// function 'createBlock()' is 'tile based' not 'pixel based'! 
	// That means x and y positions including len and hei, are 
	// multiples of a tile size. Function StaticHelper.createBlock on 
	// the other hand, IS 'pixel based'
	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	private void createBlock(int x, int y, int xLimit, int yLimit, List<Wall> barriers) throws FayPixelEngineException {
	 
	 // Throw exception if 'xLimit < x' or 'yLimit < y'
	 if(xLimit <= x || yLimit <= y) 
	  throw new FayPixelEngineException(String.format("xLimit/yLimit (%d, %d) must be > x/y (%d, %d)", xLimit, yLimit, x, y));
	 
	 // draw barriers (bricks/walls and screen border) where jmario will cast rays.
	 StaticHelper.createBlock(x * tileBlock.x, y * tileBlock.y, (xLimit - x) * tileBlock.x, (yLimit - y) * tileBlock.y, barriers);
	 
	 // show barrier outlines ?
	 if(!productionMode) StaticHelper.showWalls(barriers, this, Color.RED);
	
	} // end function
	
	private void movingBrickTest(float x, float y) {
		boolean bTileBasedSpacing = false;
		if(productionMode) drawMarioTile(individualTiles.get("brick1"), (int)x, (int)y, 1, 1,bTileBasedSpacing);
		StaticHelper.createBlock(x, y, tileBlock.x, tileBlock.y, barriers);
	}
	
	// Function movingMultipleBricksTest() 
	
	// Is a single image made of combined individual tiles from tilesheet
	
	private void movingMultipleBricksTest(float x, float y, int numberOfMulXBricks) throws FayPixelEngineException {
		boolean bTileBasedSpacing = false;
		if(productionMode) drawMarioTile(individualTiles.get("movingBarriers"), (int)x, (int)y, 1, 1,bTileBasedSpacing);
		StaticHelper.createBlock(x, y, numberOfMulXBricks * tileBlock.x, tileBlock.y, barriers);
	}
	
	// Function movingMultipleBricksTest() 
	
	// Is not absolete. Don't use it
	
	private void movingMultipleBricksTest(float x, float y) throws FayPixelEngineException {
		int numberOfMulXBricks = 3;
		boolean bTileBasedSpacing = true;
		createBlock((int)x, (int)y, (int)x + numberOfMulXBricks, (int)y + 1, barriers);
		loopNDraw("barrier", (int)x, (int)y, (int)x + numberOfMulXBricks, (int)y + 1, bTileBasedSpacing);
	}
	
	private void castRays(List<Wall> barriers) {
	 // cast rays from jmario
	 StaticHelper.lookAround(rays, barriers, mario.location.x, mario.location.y, tileBlock);
	 
	 // show rays?
	 boolean showRays = false;
	 
	 // call show rays
	 StaticHelper.showMarioRays(rays, this, Color.YELLOW, mario, showRays);
	}
	
	private void drawBackground() {
	 // get all backgrounds from JSON object
	 Iterator<JSONObject> backgrounds = FayUtils.getIterator("backgrounds");
	 
	 // create 'new' barriers list
	 barriers = new ArrayList<Wall>();
	 
	 // start drawing the background tiles
	 try {
		 drawBackground(backgrounds, barriers);
	 } catch(FayPixelEngineException fpe) {
		 fpe.printStackTrace();
		 System.exit(1);
	 }
	 
	 // cast rays as new background is drawn
	 castRays(barriers);
     
	} // end function
	
	private void MouseInput(float fElapsedTime) {
		if(MouseEventListener.mousePressed) {
			// location x = mouse x
			mario.location.x = mouseXAdjust();
			
			// location y = mouse y
			mario.location.y = mouseYAdjust();
			
			// set velocity to 0
			mario.velocity.setPosition(0, 0);
			
			// set acceleration to 0
			mario.acceleration.setPosition(0, 0);
		}
	}
	
	// Note: whenever mario's velocity changes, his 'DIRECTION OF MOTION' also
	// changes because i'm using his velocity vector as a direction vector
	private void KBInput(float fElapsedTime) {
		switch(EventListener.keyPressedEvent) {
		
			case KeyEvent.VK_RIGHT:
				if(FaySansBuffer.VP_X + VP_LEN <= getConsoleWidth()) FaySansBuffer.VP_X++;
			break;
			
			case KeyEvent.VK_LEFT:
				if(FaySansBuffer.VP_X >= 0) FaySansBuffer.VP_X--;
			break;
			
			case KeyEvent.VK_R:
				mario.reset();
				FaySansBuffer.VP_X = (int) mario.location.x;
			break;
			
			case KeyEvent.VK_D:	// move ->
				// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				// if mario somehow goes past the ground position or SB collision point
				// while returning from a jump (i.e. the D key is pressed while the J 
				// key is released AFTER a jump) then this test will ensure that he lands
				// on the ground or the SB collision point, whichever comes first. It is 
				// also why you may notice that part of his feet are below the ground when 
				// he lands. The same test applies for VK_S below. 
				//
				// This test also ensures that when he's not airborne then his ground position 
				// is always the same during horizontal motion. Try and see what happens when
				// you do this and explain your observation.
				//		mario.location.y = mario.groundPosition() - 20;
				// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				if(!mario.airbourne()) mario.location.y = mario.groundPosition();
				
				// he's moving right so set this to true
				mario.setDirBoolean(true);
				
				// increase the jump angle as he moves
				mario.incAngle();
				
				// update his horizontal acceleration (or horizontal velocity) according to frame rate
				mario.moveX(fElapsedTime);
				
				// finally update his location/position
				mario.updatePosition();
				
				// set which key was pressed to show in status window
				keys = "RIGHT";
			break;
			
			case KeyEvent.VK_S: // move <-
				// as above
				if(!mario.airbourne()) mario.location.y = mario.groundPosition();
				
				// he's moving left so set this to false
				mario.setDirBoolean(false);
				
				// reduce the jump angle as he moves
				mario.decAngle();
				
				// as above
				mario.moveX(fElapsedTime);
				
				// as above
				mario.updatePosition();
				
				// as above
				keys = "LEFT";
			break;
			
			case KeyEvent.VK_D | KeyEvent.VK_J:
				// as above
				mario.setDirBoolean(true);
				
				// as above
				mario.incAngle();
				
				// when mario jumps, his x velocity (direction) at the time of jump should 
				// be relative to his ground x velocity (direction) just before the jump
				mario.velocity.setAngledPosition(mario.angle(),mario.fXSpeedAtJump());
				
				// as above
				mario.moveX(fElapsedTime);
				
				// as above
				mario.updatePosition();
				
				// as above
				keys = "RIGHT+JUMP";
			break;
			
			case KeyEvent.VK_S | KeyEvent.VK_J:
				// as above
				mario.setDirBoolean(false);
				
				// as above
				mario.decAngle();
				
				// when mario jumps, his x velocity (direction) at the time of jump should 
				// be relative to his ground x velocity (direction) just before the jump
				mario.velocity.setAngledPosition(mario.angle(),mario.fXSpeedAtJump());
				
				// as above
				mario.moveX(fElapsedTime);
				
				// as above
				mario.updatePosition();
				
				// as above
				keys = "LEFT+JUMP";
			break;
			
			case KeyEvent.VK_J:
				// set mario's velocity (direction) jump angle. affects both velocity x and y
				mario.velocity.setAngledPosition(mario.angle());
				
				// as above
				mario.moveX(fElapsedTime);
				
				// as above
				mario.updatePosition();
				
				// as above
				keys = "JUMP";
			break;
			
			case  KeyEvent.VK_D | KeyEvent.VK_K:
				if(mario.airbourne()) {
					// set mario's velocity (direction) jump angle. affects both velocity x and y
					mario.velocity.setAngledPosition(-mario.angle());
					
					
					mario.updateWithGravity(false);
				} else {
					mario.location.y = mario.groundPosition();
					mario.velocity.y = 0;
					mario.setDirBoolean(true);
					mario.incAngle();
					mario.moveX(fElapsedTime);
					mario.updatePosition();
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
					mario.updatePosition();
				}
				keys = "N/A";
					
			break;
		} // end switch
	} // end function
	
	private void showStatus() {
		String s = "s = left, d = right, j = jump\nr = reset\n--------------\n";
		s += String.format("JMario pos: %.2f %.2f\nVelocity: %.3f, %.2f\nAcceleration: %.4f, %.4f\nGravity: %.5f, %.5f\nAirbourne: %b\nKey pressed: %d: %s" +
				"\nKey event size: %d | Key event code: %d\nMouse x,y: (%d, %d)" + 
				"\n\nGround position: %.2f\nEast-B Collision point: %.2f\nWest-B Collision point: %.2f\nNorth-B Collision point: %.2f" + 
				"\nSE-B Collision point: %.2f\nSW-B Collision point: %.2f\nNE-B Collision point: %.2f\nNW-B Collision point: %.2f\n" +
				
		 "Last detected collision: %s", 
		 mario.location.x, mario.location.y,
		 mario.velocity.x, mario.velocity.y,
		 mario.acceleration.x, mario.acceleration.y,
		 mario.gravity.x, mario.gravity.y, mario.airbourne(), EventListener.keyPressedEvent, keys, getEvents().size(), 
		 	getEventCode(), mouseXAdjust(), mouseYAdjust(), mario.groundPosition(), mario.eBoundCP(), mario.wBoundCP(), mario.nBoundCP(), 
		 	mario.seBoundCP(), mario.swBoundCP(), mario.neBoundCP(), mario.nwBoundCP(), mario.collisionInfo());
		setStatus(s);
	}
	
	private void marioTileOutline() {
		List<Wall> block = new ArrayList<Wall>();
		StaticHelper.createBlock(mario.location.x, mario.location.y, tileBlock.x, tileBlock.y, block);
		StaticHelper.showWalls(block, this, Color.BLUE);
	}
	
	private void drawSmallJumpAngleVector() {
	 Vf2d v2 = Vf2d.normalizedAngle(mario.angle());
	 v2.mul(50);
	 translateVf2dPoint(v2, mario.location.clone(), Color.BLUE);
	}
	
	//TODO: Example function
	private void drawSansIT(String tile, int startX, int startY, int xLen, int yLen) {
	 for(int x = startX; x < xLen; x++)
	  for(int y = startY; y < yLen; y++)
	   drawMarioTile(individualTiles.get(tile), x, y, 1, 1,true);
	}

	@Override
	public boolean OnUserCreate() {
	 // setup A
	 tileBlock = new Vf2d(16, 16);	
	 
	 // optional: moving brick(s) test
	 movingBrickPosX = getConsoleWidth() - tileBlock.x;
	 movingBrickPosY = -1;
	 movingMulBricksPosX = -1;
	 movingMulBricksPosY = -1;
	 
	 // show barrier/wall/bricks outline? Setting to true is real game mode
	 productionMode = true;
	 
	 // use json file or String for drawing BG?
	 // set to true
	 useJsonFile = true;
		
	 // tilesheet.png dimensions are 528 x 448
	 Sprite tilesheet = new Sprite(System.getProperty("user.dir") + "\\img\\jmario\\tiles.png");
	 
	 // characters sheet
	 Sprite characters = new Sprite(System.getProperty("user.dir") + "\\img\\jmario\\characters.gif");
	 
	 // add individual tiles from the one tilesheet
	 
	 individualTiles = new HashMap<String,BufferedImage>();
	 
	 // ground tile
	 individualTiles.put("ground",
			 (BufferedImage) tilesheet.partialSprite(
					 new Vf2d(0 * tileBlock.x, 0 * tileBlock.y),  
					 new Vf2d(tileBlock.x, tileBlock.y)));
	 
	 // barrier tile
	 individualTiles.put("barrier",
			 (BufferedImage) tilesheet.partialSprite(
					 new Vf2d(0 * tileBlock.x, 0 * tileBlock.y),  
					 new Vf2d(tileBlock.x, tileBlock.y)));
	 
	// brick tile 1
	individualTiles.put("brick1",
			 (BufferedImage) tilesheet.partialSprite(
					 new Vf2d(1 * tileBlock.x, 0 * tileBlock.y),  
					 new Vf2d(tileBlock.x, tileBlock.y)));
	
	// brick tile 2
	individualTiles.put("brick2",
			 (BufferedImage) tilesheet.partialSprite(
					 new Vf2d(2 * tileBlock.x, 0 * tileBlock.y),  
					 new Vf2d(tileBlock.x, tileBlock.y)));
	
	// question tile 1
	individualTiles.put("q1",
			 (BufferedImage) tilesheet.partialSprite(
					 new Vf2d(24 * tileBlock.x, 0 * tileBlock.y),  
					 new Vf2d(tileBlock.x, tileBlock.y)));
	 
	 // multiple moving barrier tiles
	 try {
	  
	  BufferedImage mulBarrierTiles = tilesheet.multipleXPlaneImage(individualTiles.get("brick1"), 3);
	  
	  individualTiles.put("movingBarriers", mulBarrierTiles);
	 
	 } catch (FayPixelEngineException fpe) {
	  
	  System.exit(1);	 
		 
	 }
	 
	 // sky tile starts at block (3,23)
	 // "Looks like it's about 3 and 23 or something"
	 individualTiles.put("sky",
			 (BufferedImage) tilesheet.partialSprite(
					 new Vf2d(3 * tileBlock.x, 23 * tileBlock.y),  
					 new Vf2d(tileBlock.x, tileBlock.y)));
	 
	 // characters.gif are 513 x 401
	 // idle mario. He uses absolute positioning here in pixels, not blocks.
	 // hence, 17 becomes 276 and 3 becomes 44.
	 individualTiles.put("idleMario",
			 (BufferedImage) characters.partialSprite(
					 new Vf2d(276, 44),  
					 new Vf2d(tileBlock.x, tileBlock.y)));
	 
	 // define mario's starting position at 64,64 going down
	 // at a 45 deg angle
	 // mario = new Ray(64,64,1.0f,StaticHelper.degToRad(45));
	 
	 // E3 - Parabolic jump
	 // mario = new Ray(64,180,200,-600);
	 
	 // setup B
	 // At (64,180) Mario overlaps part of the ground in that 
	 // his feet are digging in the ground tiles. Why? The sky 
	 // occupies 0-14 tiles in height so 16 * 14 = 224 pixels. The
	 // ground occupies 12-14 tiles so 2 * 16 = 32 pixels. That 
	 // leaves 192 pixels b/w the sky and ground. Since mario's 
	 // height is 16 pixels where should I place him? 192 - 16 = 
	 // at 176 Y-pixels. However, he puts Mario at 180 pixels which 
	 // is definitely not correct. Hence I changed his position to 
	 // (64,176).
	 mario = new Player();
	 mario.setRadius(16);
	 
	 // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	 // I won't use constant velocity or acceleration. Both of these 
	 // will be false meaning mario's acceleration depends on the frame rate
	 // see 'Player.java' 'moveX()' and 'accelerate()' functions. 
	 //
	 // Note: set both to false. There is a bug in moveX() when using 
	 // constant velocity. The jump "jitters"
	 boolean bConstantVel, bConstantAcc; 
	 bConstantVel = bConstantAcc = false;
	 mario.initMotionBooleans(bConstantVel, bConstantAcc);
	 // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	 
	 mario.initFloats(Player.INIT_JUMP_ANGLE, 0.5f, 0.005f, 0.85f, 0.05f, 0.03f, 1.2f);
	 mario.initInts(176, 1);
	 mario.location.setPosition(mario.startXPosition(), mario.groundPosition());
	 mario.setDirBoolean(false);
	 
	 // load level JSON file
	 // "class java.lang.String cannot be cast to class org.json.simple.JSONArray"
	 // Make sure that the string is a valid JSON
	 // stackoverflow.com/questions/25567041/
	 String jsonFile = System.getProperty("user.dir") + "\\src\\sansbuffer\\JMario\\1-1.json";
	 FayUtils.loadJson(jsonFile);
	 
	 createStatusWindow(400, 400);
	 
	 // allow this game to be paused and resumed with P and C keys
	 allowPause();
	 
	 // rays of light coming out of the MIDDLE of mario tile
	 rays = StaticHelper.marioRays(mario.location.x, mario.location.y, tileBlock);
	 
	 return true;
	} // end function

}