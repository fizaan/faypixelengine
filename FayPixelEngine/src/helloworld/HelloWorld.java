package helloworld;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import fay.events.EventListener;
import fay.math.Vf2d;
import fay.original.FaySansBuffer;
import fay.sprite.Sprite;

// Use the arrow keys to scroll left right up down

@SuppressWarnings("serial")
public class HelloWorld extends FaySansBuffer {
	
	private BufferedImage map;
	
	private float x, y;
	
	public static void main(String args[]) {
		
     // Viewport settings
     FaySansBuffer.USING_VP = true;
     FaySansBuffer.VP_LEN = 600;
     FaySansBuffer.VP_HEI = 600;
     FaySansBuffer.VP_X_OFFSET = 0;	
     FaySansBuffer.VP_Y_OFFSET = 0;
	 
	new HelloWorld(1749,987,1,1, "Hello World");
	
	}
	
	public HelloWorld(int len, int hei, int pX, int pY, String title) {
	 
	super(len,hei,pX,pY,title);
	
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		
	 drawSprite(new Vf2d(0, 0), map);
	
	 KBInput();
	
	 updateVP(x, y);
	
	 return true;
	}

	@Override
	public boolean OnUserCreate() {
		
	 map  = new Sprite(System.getProperty("user.dir") + "\\img\\viewport\\world.jpeg").sprite();
	
	 return true;
	}
	
	private void KBInput() {
		
	 switch(EventListener.keyPressedEvent) {
	  
	 case KeyEvent.VK_RIGHT:
	  
	  if(x <= getConsoleWidth()) x++;
	 
	 break;
		
	 case KeyEvent.VK_LEFT:
	  
	  if(x >= 0) x--;
	 
	 break;
	 
	 case KeyEvent.VK_UP:
	  
	  if(y >= 0) y--;
	 
	 break;
		
	 case KeyEvent.VK_DOWN:
	  
	  if(y <= getConsoleHeight()) y++;
	 
	 break; 
	
	 }
	}

}
