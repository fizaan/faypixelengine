package fay.abstct;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Queue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import fay.events.EventListener;
import fay.events.Mouse;
import fay.events.MouseEventListener;
import fay.math.Vf2d;

@SuppressWarnings("serial")
public abstract class Console extends JPanel implements ActionListener {
	
	private BufferedImage fBuffer;
	private JFrame console;
	private int len, hei, pixelW, pixelH;
	private String title;
	private EventListener eventListener;
	private MouseEventListener mouseMotionListener;
	private int eventCode, mouseMoveEventCodeX, mouseMoveEventCodeY, mouseX, mouseY;
	private Timer timer;
	private long then,now;
	public static boolean initializeGameSettingsManually = false;
	
	public int getConsoleWidth() { return len; }
	public int getConsoleHeight() { return hei; }
	public int getPixelX() { return pixelW; }
	public int getPixelY() { return pixelH; }
	public BufferedImage getFrameBufferImg() { return fBuffer; }
	public JFrame console() { return console; }
	public void sleep(int ms) { Thread.currentThread(); try { Thread.sleep(ms); } catch(Exception e) {} }
	public Queue<Integer> getEvents() { return eventListener.getEvents(); }
	public Queue<Mouse> getMouseMoveEvents() { return mouseMotionListener.getEvents(); }
	public int getEventCode() { return eventCode; }
	public int getMouseMoveEventCodeX() { return mouseMoveEventCodeX; }
	public int getMouseMoveEventCodeY() { return mouseMoveEventCodeY; }
	public void setMouseMoveEventCodeX(int x) { mouseMoveEventCodeX = x; }
	public void setMouseMoveEventCodeY(int y) { mouseMoveEventCodeY = y; }
	public int mouseX() { return mouseX; }
	public int mouseY() { return mouseY; }
	public void setMouseX(int x) { mouseX = x; }
	public void setMouseY(int y) { mouseY = y; }
	public int mouseXAdjust() { return mouseX - 10; }
	public int mouseYAdjust() { return mouseY - 30; }
	public Timer timer() { return timer; }
	public void setResizable() { console.setResizable(true); }
	public String title() { return title; }
	public void setTitle(String s) { console.setTitle(s); }
	public void setThen(long t) { then = t; }
	public void setNow(long t) { now = t; }
	public long then() { return then; }
	public long now() { return now; }
	public void setEventCode(int e) { eventCode = e; }
	public void stopTimer() { timer.stop(); }
	
	public Console() {
		len = 80;
		hei = 30;
		this.title = "Console - ";
		pixelW = 8;
		pixelH = 8;
		GraphicsConfiguration gfxConfig = GraphicsEnvironment.
        getLocalGraphicsEnvironment().getDefaultScreenDevice().
        getDefaultConfiguration();
		fBuffer = gfxConfig.createCompatibleImage(len, hei);
		eventListener = new EventListener();
		mouseMotionListener = new MouseEventListener();
		timer = new Timer(0, this);
        timer.start();
	}
	
	public Console(int len, int hei, int pixelW, int pixelH, String title) {
		GraphicsConfiguration gfxConfig = GraphicsEnvironment.
        getLocalGraphicsEnvironment().getDefaultScreenDevice().
        getDefaultConfiguration();
		fBuffer = gfxConfig.createCompatibleImage(len, hei);
		eventListener = new EventListener();
		mouseMotionListener = new MouseEventListener();
		this.len = len;
		this.hei = hei;
		this.pixelW = pixelW;
		this.pixelH = pixelH;
		this.title = title;
		timer = new Timer(0, this);
        timer.start();
	}
	
	public void fillRect(Vf2d pos, Vf2d size, Color c) {
		fillRectHelper((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, c);
	}
	
	public void fillRect(int x, int y, int width, int height, Color c) {
		if(x+width >= 0 && x+width <= len * pixelW && y+height >= 0 && y+height <= hei * pixelH) 
			fillRectHelper(x, y, width, height, c);
	}
	
	private void fillRectHelper(int x, int y, int width, int height, Color c) {
		width += x; height += y;
		for(int xPos = x; xPos < width; xPos++)
			for(int yPos = y; yPos < height; yPos++)
				draw(xPos,yPos,c);
	}
	
	public void draw(int x, int y, Color c) {
		if(x >= 0 && x <= len && y >= 0 && y <= hei) drawPixel(x,y,c);
	}
	
	public void drawSprite(Vf2d pos, Vf2d size, int[] pixels) {
		drawSprite((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, pixels);
	}
	
	public void drawSprite(Vf2d pos, BufferedImage img) {
		drawSprite((int)pos.x, (int)pos.y, img);
	}
	
	public void drawSprite(Vf2d pos, BufferedImage img, float alpha) {
		drawSprite((int)pos.x, (int)pos.y, img, alpha);
	}
	
	public void drawPartialSprite(Vf2d pos, Vf2d index, Vf2d size, BufferedImage img) {
		drawPartialSprite((int)index.x, (int)index.y, (int)pos.x, (int)pos.y, (int)size.x, (int)size.y, img);
	}
	
	public void drawPartialSpriteUnscaled(Vf2d pos, Vf2d index, Vf2d size, BufferedImage img) {
		drawPartialSpriteUnscaled((int)index.x, (int)index.y, (int)pos.x, (int)pos.y, (int)size.x, (int)size.y, img);
	}
	
	public void drawSprite(Vf2d pos, BufferedImage img, float angle, int midX, int midY, int scaleX, int scaleY, float alpha, boolean cwr) {
		drawRotatedDecal((int)pos.x, (int)pos.y, img, angle, midX, midY, scaleX, scaleY, alpha, cwr);
	}
	
	private void drawSprite(int x, int y, int w, int h, int[] pixels) {
		if(w * h != pixels.length) {
			System.out.println("Error: tile w and h does not match");
			return;
		}
		
		w += x;
		h += y;
		int i = 0;
		for( int yPos = y; yPos < h; yPos++)
			for(int xPos = x; xPos < w; xPos++)
				drawPixel(xPos,yPos,pixels[i++]);
	}
	
	public void fillDecal(BufferedImage sprite, Color c) {
		for(int x = 0; x < sprite.getWidth(); x++)
			for(int y = 0; y < sprite.getHeight(); y++) {
				int col = sprite.getRGB(x, y);
				int alpha = (col >> 24) & 0xff;
				if(alpha == 255 && col != Color.BLACK.getRGB()) 
					sprite.setRGB(x, y, c.getRGB()); 
			}
	}
	
	// abstract methods which user must implement.
	public abstract void drawScaledSprite(Vf2d pos, Image img);
	public abstract void drawScaledSprite(Vf2d pos, BufferedImage sprite, int scale);
	public abstract void drawSprite(int x, int y, BufferedImage img);
	public abstract void drawSprite(int x, int y, BufferedImage img, float alpha);
	public abstract void drawRotatedDecal(int x, int y, BufferedImage img, float angle, int midX, int midY, int scaleX, int scaleY, float alpha, boolean cwr);
	public abstract void drawPartialSprite(int indexX, int indexY, int x, int y, int w, int h, BufferedImage img);
	public abstract void drawPartialSpriteUnscaled(int indexX, int indexY, int x, int y, int w, int h, BufferedImage img);
	public abstract void drawPixel(int x, int y, Color c);
	public abstract void drawPixel(int x, int y, int rgb);
	public abstract void drawLine(int x, int y, int w, int h, Color c);
	public abstract void drawVf2d(Vf2d v, Color c);
	public abstract void fillCircle(int x, int y, int w, int h, Color c);
	public abstract void fillPolygon(Polygon p, Color c);
	public abstract void showVf2dPoint(Vf2d v, int w, int h, Color c, boolean showlines);
	public abstract void translateVf2dPoint(Vf2d v, Vf2d translateStart, Color c);
	public abstract void translateVf2dFromTo(Vf2d from, Vf2d to, Color c, float transparency);
	public abstract void drawFrame(Graphics2D g2d);
	public abstract void clearConsole(Color c);
	public abstract void clearConsoleRegion(int x, int y, int w, int h, Color c);
	public abstract void actionPerformed(ActionEvent e);
	
	public void startGui(int len, int hei, int pixelW, int pixelH) {
	 setPreferredSize(new Dimension(len * pixelW, hei * pixelH));
	 console = new JFrame(title);
	 console.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 //console.setLocationRelativeTo(null);
	 console.setLocation(0,0);
	 console.setContentPane(this);
	 console.setResizable(false);
	 console.setVisible(true);
	 console.pack();
	 console.addKeyListener(eventListener);
	 console.addMouseMotionListener(mouseMotionListener);
   }

}
