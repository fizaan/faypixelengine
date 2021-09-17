package sansbuffer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import fay.events.EventListener;
import fay.math.Vf2d;
import fay.original.FaySansBuffer;

// Tile based platform game #1. Alot of the idea in this code came
// from Javidx9's YT video but not all. Some of it was actually mine
// which worked quite nicely. For example, squeezing between 1 unit
// blocks/tiles is actually my own code and I'm pleased with it

@SuppressWarnings("serial")
public class JavidPlayer extends FaySansBuffer {
	
	private Player player;
	private float speed;
	private static int tileSize = 20, levelW = 60, levelH = 20;
	
	// Boolean 'bDevmode' is an important flag. When set, we are in dev mode, where there is no gravity 
	// and no acceleration. The player moves in a constant velocity and there is no 'jump' option. 
	// Use this mode only for testing. In dev mode, use the arrow keys otherwise use the S,D SPACE
	// keys
	private boolean bDevmode;
	
	// A "levelW x levelH" level. Each tile is 'tileSize' in pixels
	// . = sky
    // # = ground/barrier
    public static final String bg = 
         "##..##..............##......................................"
       + ".........................####..............................#"
       + "#####.........#......................#......................"
       + "..###########.####.............#####.######................."
       + "..................................##.#...........######....."
       + "...#..............###..............#.#..........#######....."
       + "...#.#............###.............##.#.........########....#"
       + "...#.#....................########............#########....."
       + "...................................#.........#.............."
       + "...................................#.########..............."
       + "............##############.........#.##..##############....."
       + "...................................#.##....................#"
       + "..#####....................#########.##....................."
       + ".....................................##.....###########....."
       + "...........................#########.##....................."
       + "............................................................"
       + "..........................................................##"
       + "..####################################......................"
       + "############################################################"
       + "............................................................";
	
	public static void main(String args[]) {
		new JavidPlayer(levelW * tileSize, levelH * tileSize,1,1, "Tile based game");
	}

	public JavidPlayer(int len, int hei, int pX, int pY, String title) {
		super(len,hei,pX,pY,title);
	}
	
	private void drawPlayer() {
		fillRect((int)player.location.x, (int)player.location.y, player.radius(), player.radius(), player.colour());
	}
	
	@Override
	public boolean OnUserCreate() {
		int startX = 200, startY = 0;
		
		player = new Player(startX, startY, 0, 0);
		
		player.setRadius(tileSize);
		
		bDevmode = false;
		
		speed = bDevmode ? 150.0f : 2.0f;
		
		return true;
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		clearConsole(Color.WHITE);
		
		drawStringBackground();
		
		KBInput(fElapsedTime);
		
		if(bDevmode) showTileBoundaries();
		
		drawPlayer();
		
		return true;
	}
	
	private void setDefaultVel() {
	 if(bDevmode) player.velocity.setPosition(0, 0);
		 
				  // set only horizontal movement to 0 because now we have introduced gravity
	 else 		  player.velocity.x = 0;
	}
	
	private void updateVel(float fElapsedTime) {
				  // if in dev mode, update player's velocity relative to frame rate
	 if(bDevmode) player.velocity.mul(fElapsedTime);
				
				  // else add gravity to player's velocity y
	 else 		  player.velocity.y += 20.0f * fElapsedTime;
	}
	
	private void capVel() {
	 if(player.velocity.x > speed) player.velocity.x = speed;
				
	 if(player.velocity.x < -speed) player.velocity.x = -speed;
				
	 if(player.velocity.y > speed) player.velocity.y = speed;
				
	 if(player.velocity.y < -speed) player.velocity.y = -speed;
	}
	
	private void KBInput(float fElapsedTime) {
		switch(EventListener.keyPressedEvent) {
		
			case KeyEvent.VK_RIGHT:
				if(bDevmode) player.velocity.x = speed;	// constant right speed
			break;
			
			case KeyEvent.VK_LEFT:
				if(bDevmode) player.velocity.x = -speed; // constant left speed
			break;
			
			case KeyEvent.VK_DOWN:
				if(bDevmode) player.velocity.y = speed; // constant downward speed
			break;
			
			case KeyEvent.VK_UP:
			case KeyEvent.VK_SPACE:
				player.velocity.y = -speed;
			break;
		
			case KeyEvent.VK_D:
				if(!bDevmode) player.velocity.x += speed * fElapsedTime;	// right acceleration
			break;
			
			case KeyEvent.VK_S:
				if(!bDevmode) player.velocity.x -= speed * fElapsedTime; // left acceleration
			break;
			
			case KeyEvent.VK_D | KeyEvent.VK_SPACE:
				if(!bDevmode) {
					player.velocity.x += speed * fElapsedTime; // right acceleration while jumping
					player.velocity.y = -speed;
				}
			break;
			
			case KeyEvent.VK_S | KeyEvent.VK_SPACE:
				if(!bDevmode) { 
					player.velocity.x -= speed * fElapsedTime; // left acceleration while jumping
					player.velocity.y = -speed;
				}
			break;
			
			default: setDefaultVel(); break;
		
		} // end switch
		
		// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		// note everything below should NOT be placed in switch default case!
		
		updateVel(fElapsedTime);
		
		// cap player's velocity if not in dev mode
		if(!bDevmode) capVel();
		
		// make a clone of actual 'player', and call it 'futurePlayer'
		Vf2d futurePlayer = player.location.clone();
		
		// update future player's location
		futurePlayer.add(player.velocity);
		
		// Handle collisions. He uses both real and future player's positions but I only use future player
		handleCollisions(futurePlayer);
		
	} // end function
	
	private void handleCollisions(Vf2d futurePlayer) {
		// Tile coordinates for collisions detection
		
		/*		(x,y)		   (x + 1, y)
		 * 		+--------------+
		 * 		|			   |	
		 * 		|	PLAYER	   |
		 * 		|			   |
		 * 		+--------------+
		 * 		(x,y + 1)	   (x + 1, y + 1) unsused
		 */
		
		if(player.velocity.x <= 0) {
			
			// moving left VK_S
			
			// using coords (x1,y1) and (x1, y1 + 1) i.e. TOP LEFT and BOTTOM LEFT
			
			int x = (int) (futurePlayer.x / tileSize);
			
			int y = (int) (futurePlayer.y  / tileSize);
			
			int indexTopLeft = x + y * levelW;
			
			int indexBottomLeft = x + (y + 1) * levelW;
			
			// Squeeze between a gap on the left if required. We need a diffY.
			// We can only squeeze through if the future player's y position is
			// on an integer boundary (or close enough). That 'close enough' is
			// the diffY
			
			float diffY = futurePlayer.y / tileSize;
			diffY = (float) (diffY - Math.floor(diffY));
			
			// what's 0.07f ?
			
			if(diffY < 0.07f) {
				
				if(getTile(indexTopLeft) != '.') {
					
					futurePlayer.x = player.location.x;
					player.velocity.x = 0;
				}
			}
			
			// else handle normal collision case
			
			else if(getTile(indexTopLeft) != '.' || getTile(indexBottomLeft) != '.' || getTile(indexTopLeft) == 'e' || getTile(indexBottomLeft) == 'e') {
				
				futurePlayer.x = player.location.x;
				
				// There's a bug here when he lands from a jump. He stops even when there is no WB collision
				
				player.velocity.x = 0;
			}
			
		} else {
			
			// moving right VK_D
			
			// using coords (x1,y1) and (x1, y1 + 1) i.e. TOP LEFT and BOTTOM LEFT
			
			// first move future player to the right by 1 tile
			
			// what's with the 1.0f?
			
			float newPosX = futurePlayer.x + tileSize - 1.0f;
			
			int x = (int) (newPosX / tileSize);
			
			int y = (int) (futurePlayer.y  / tileSize);
			
			int indexTopLeft = x + y * levelW;
			
			int indexBottomLeft = x + (y + 1) * levelW;
			
			// Squeeze between a gap on the right if required. We need a diffY.
			// We can only squeeze through if the future player's y position is
			// on an integer boundary (or close enough). That 'close enough' is
			// the diffY
			
			float diffY = futurePlayer.y / tileSize;
			diffY = (float) (diffY - Math.floor(diffY));
			
			// what's 0.07f ?
			
			if(diffY < 0.07f) {
				
				if(getTile(indexTopLeft) != '.') {
					
					futurePlayer.x = player.location.x;
					player.velocity.x = 0;
				}
			}
			
			// else handle normal collision case
			
			else if(getTile(indexTopLeft) != '.' || getTile(indexBottomLeft) != '.' || getTile(indexTopLeft) == 'e' || getTile(indexBottomLeft) == 'e') {
				
				futurePlayer.x = player.location.x;
				
				// There's a bug here when he lands from a jump. He stops even when there is no EB collision
				
				player.velocity.x = 0;
			}
		}
		
		if(player.velocity.y <= 0) {
			
			// moving up VK_SPACE
			
			// using coords (x1,y1) and (x1 + 1, y1) i.e. TOP LEFT and TOP RIGHT
			
			int x = (int) (futurePlayer.x / tileSize);
			
			int y = (int) (futurePlayer.y  / tileSize);
			
			int indexTopLeft = x + y * levelW;
			
			int indexTopRight = (x + 1) + y * levelW;
			
			// Squeeze between a gap above if required. We need a diffX.
			// We can only squeeze through if the future player's x position is
			// on an integer boundary (or close enough). That 'close enough' is
			// the diffX
			
			float diffX = futurePlayer.x / tileSize;
			diffX = (float) (diffX - Math.floor(diffX));
			
			if(diffX < 0.07f) {
				
				if(getTile(indexTopLeft) != '.') {
					
					futurePlayer.y = player.location.y;
					player.velocity.y = 0;
				}
			}
			
			// else handle normal collision case
			
			else if(getTile(indexTopLeft) != '.' || getTile(indexTopRight) != '.' || getTile(indexTopLeft) == 'e' || getTile(indexTopRight) == 'e') {
				
				futurePlayer.y = player.location.y;
				player.velocity.y = 0;
			}
			
		} else {
			
			// moving down VK_DOWN (no longer used due to gravity, but useful for testing)
			
			// using coords (x1,y1) and (x1 + 1, y1) i.e. TOP LEFT and TOP RIGHT
			
			// first move future player down by 1 tile
			
			// what's with the 1.0f?
			
			float newPosY = futurePlayer.y + tileSize - 1.0f;
			
			int x = (int) (futurePlayer.x / tileSize);
			
			int y = (int) (newPosY  / tileSize);
			
			int indexTopLeft = x + y * levelW;
			
			int indexTopRight = (x + 1) + y * levelW;
			
			// Squeeze between a gap above if required. We need a diffX.
			// We can only squeeze through if the future player's x position is
			// on an integer boundary (or close enough). That 'close enough' is
			// the diffX
			
			float diffX = futurePlayer.x / tileSize;
			diffX = (float) (diffX - Math.floor(diffX));
			
			if(diffX < 0.07f) {
				
				if(getTile(indexTopLeft) != '.') {
					
					futurePlayer.y = player.location.y;
					player.velocity.y = 0;
				}
			}
			
			// else handle normal collision case
			
			else if(getTile(indexTopLeft) != '.' || getTile(indexTopRight) != '.' || getTile(indexTopLeft) == 'e' || getTile(indexTopRight) == 'e') {
				
				futurePlayer.y = player.location.y;
				player.velocity.y = 0;
			}
		}
		
		// update real player's position
		player.location = futurePlayer;
	
	} // end function 
	
	private char getTile(int index) {
		if(index >= 0 && index < bg.length()) return bg.charAt(index);
		
		return 'e';
	}
	
	private void drawStringBackground() {
	 for(int index = 0; index < bg.length(); index++) {
	  int y = index / levelW;
	  int x = index % levelW;
	  switch(bg.charAt(index)) {
	   case '#' : fillRect(x * tileSize, y * tileSize, tileSize, tileSize, Color.RED); break;
	   default: break;
	  } // end switch
	 } // end for
	}
	
	private void showTileBoundaries() {
	 for(int col = 0; col < getConsoleWidth(); col++)
	  drawLine(col * tileSize, 0, col * tileSize, getConsoleHeight(), Color.BLACK);
	
	 for(int row = 0; row < getConsoleHeight(); row++)
	  drawLine(0, row * tileSize, getConsoleWidth(), row * tileSize, Color.BLACK);
	}

}
