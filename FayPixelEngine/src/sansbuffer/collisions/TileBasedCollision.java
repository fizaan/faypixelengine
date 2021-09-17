package sansbuffer.collisions;

import java.awt.Color;
import java.awt.event.KeyEvent;
import fay.events.EventListener;
import fay.original.FaySansBuffer;
import sansbuffer.Player;

@SuppressWarnings("serial")
public class TileBasedCollision extends FaySansBuffer {
	
	private Player player;
	
	private float speed;
	
	private boolean collision;
	
	public static void main(String args[]) {
		
		new TileBasedCollision(500, 500 , 1, 1, "Tile Based Collisions");
	}

	public TileBasedCollision(int len, int hei, int pX, int pY, String title) {
		
		super(len,hei,pX,pY,title);
	}
	
	private void drawPlayer() {
		
		fillRect((int)player.location.x, (int)player.location.y, player.radius(), player.radius(), player.colour());
	}
	
	@Override
	public boolean OnUserCreate() {
		
		speed = 150.0f;
		
		player = new Player(20, getConsoleHeight() / 2, 0, 0);
		
		player.setRadius(16);
		
		return true;
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		
		clearConsole(Color.white);
		
		// Wall for collision. Wall width is same as player width = 16 pixels
		fillRect(getConsoleWidth() - 100, 20, 16, getConsoleHeight() - 50, collision ? Color.RED : Color.GREEN);
		
		KBInput(fElapsedTime);
		
		drawPlayer();
		
		return true;
	}
	
	private void KBInput(float fElapsedTime) {
		
		switch(EventListener.keyPressedEvent) {
		
			case KeyEvent.VK_RIGHT:
				
				player.velocity.x = speed;
				
			break;
			
			case KeyEvent.VK_LEFT:
				
				player.velocity.x = -speed;
				
			break;
			
			default:
				
				player.velocity.setPosition(0, 0);
			
			break;
		}
		
		player.velocity.mul(fElapsedTime);
		
		float fPlayerPosXBeforeCollision = player.location.x;
		
		player.location.add(player.velocity);
		
		// :::::::::::::::::::::::::: COLLISION DETECTION :::::::::::::::::::::::::::::::::::::::
		
		// By adding player's radius to the player's current position X, I am essentially 
		
		// checking where the player WILL BE in the next frame and if there is a collision
		
		// bring the player's X position back to what it was BEFORE the collision (..and this way
		
		// the player REMAINS in the exact same postion X prior to the collision for as long as
		
		// collision is occuring in the same direction).
		
		// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
		
		if(collision = checkCollision()) player.location.x = fPlayerPosXBeforeCollision;
	}
	
	private boolean checkCollision() {
		
		return player.location.x + player.radius() >= getConsoleWidth() - 100;
			
	}

}
