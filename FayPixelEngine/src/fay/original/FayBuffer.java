package fay.original;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.Queue;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import fay.math.Vf2d;
import fay.events.EventListener;
import fay.events.Mouse;
import fay.events.MouseEventListener;

@SuppressWarnings("serial")
public abstract class FayBuffer extends JPanel implements ActionListener {
	private BufferedImage fBuffer;
	private JFrame console;
	private JViewport viewport;
	private JTextArea statusWindow;
	private int len, hei, pixelW, pixelH;
	private String title;
	private EventListener eventListener;
	private MouseEventListener mouseMotionListener;
	private int eventCode, mouseMoveEventCodeX, mouseMoveEventCodeY, mouseX, mouseY;
	private Timer timer;
	private boolean pause, allowPause;
	long then,now;
	
	// Viewport settings
	public static boolean USING_VP = false;
	
	public static int VP_LEN, VP_HEI, VP_X, VP_Y, VP_X_OFFSET, VP_Y_OFFSET;
	
	public FayBuffer() {
		super();
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
		
		init();
		
		timer = new Timer(0, this);
        timer.start();
	}
	
	public FayBuffer(int len, int hei, int pixelW, int pixelH, String title) {
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
		
		init();
		
		timer = new Timer(0, this);
        timer.start();
	}
	
	public int getConsoleWidth() { return len; }
	public int getConsoleHeight() { return hei; }
	public int getPixelW() { return pixelW; }
	public int getPixelH() { return pixelH; }
	public BufferedImage getFrameBufferImg() { return fBuffer; }
	public JFrame console() { return console; }
	public void sleep(int ms) { Thread.currentThread(); try { Thread.sleep(ms); } catch(Exception e) {} }
	public Queue<Integer> getEvents() { return eventListener.getEvents(); }
	public Queue<Mouse> getMouseMoveEvents() { return mouseMotionListener.getEvents(); }
	public int getEventCode() { return eventCode; }
	public int getMouseMoveEventCodeX() { return mouseMoveEventCodeX; }
	public int getMouseMoveEventCodeY() { return mouseMoveEventCodeY; }
	public int mouseX() { return mouseX; }
	public int mouseY() { return mouseY; }
	public int mouseXAdjust() { return mouseX - 10; }
	public int mouseYAdjust() { return mouseY - 30; }
	public Timer timer() { return timer; }
	public void setResizable() { console.setResizable(true); }
	public void allowPause() { allowPause = !allowPause; }
	
	public static void updateVP(Vf2d player) {
	 if(player.x > VP_X_OFFSET) VP_X = (int) player.x - VP_X_OFFSET;
	 if(player.y > VP_Y_OFFSET) VP_Y = (int) player.y - VP_Y_OFFSET;
	}
	
	public static void updateVP(float playerX, float playerY) {
	 if(playerX > VP_X_OFFSET) VP_X = (int) playerX - VP_X_OFFSET;
	 if(playerY > VP_Y_OFFSET) VP_Y = (int) playerY - VP_Y_OFFSET;
	}
	
	private void init() {
		System.setProperty("sun.java2d.opengl", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        } catch (UnsupportedLookAndFeelException ex) {
        }
         SwingUtilities.invokeLater(() -> startGui(len,hei,pixelW,pixelH));
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
	
	// one way of drawing sprite
	public void drawSprite(Vf2d pos, Vf2d size, int[] pixels) {
		drawSprite((int)pos.x, (int)pos.y, (int)size.x, (int)size.y, pixels);
	}
	
	// another, simpler way of drawing sprite
	public void drawSprite(Vf2d pos, BufferedImage img) {
		drawSprite((int)pos.x, (int)pos.y, img);
	}
	
	// draw sprite with alpha
	// must draw immediately!
	public void drawSprite(Vf2d pos, BufferedImage img, float alpha) {
		drawSprite((int)pos.x, (int)pos.y, img, alpha);
	}
	
	// drawPartialSprite()
	public void drawPartialSprite(Vf2d pos, Vf2d index, Vf2d size, BufferedImage img) {
		drawPartialSprite((int)index.x, (int)index.y, (int)pos.x, (int)pos.y, (int)size.x, (int)size.y, img);
	}
	
	// drawPartialSpriteUnscaled()
	public void drawPartialSpriteUnscaled(Vf2d pos, Vf2d index, Vf2d size, BufferedImage img) {
		drawPartialSpriteUnscaled((int)index.x, (int)index.y, (int)pos.x, (int)pos.y, (int)size.x, (int)size.y, img);
	}
	
	// Aug 27
	public void drawPartialSpriteScaled(Vf2d pos, Vf2d index, Vf2d size, Vf2d scale, BufferedImage img) {
		drawPartialSpriteScaled((int)index.x, (int)index.y, (int)pos.x, (int)pos.y, (int)size.x, (int)size.y, (int)scale.x, (int)scale.y, img);
	}
	
	// scaledSprite - method 1: no longer used
	public void drawScaledSprite(Vf2d pos, Image img) {
		fBuffer.createGraphics().drawImage(img, (int)pos.x, (int) pos.y, null);
	}
	
	// scaledSprite - method 2: New
	public void drawScaledSprite(Vf2d pos, BufferedImage sprite, int scale) {
		fBuffer.createGraphics().drawImage(sprite, (int)pos.x, (int) pos.y, sprite.getWidth() * scale, sprite.getHeight() * scale, null);
	}
	
	// scaledSprite - method 3: New - Aug 27
	public void drawScaledSprite(Vf2d pos, BufferedImage sprite, int scaleX, int scaleY) {
		fBuffer.createGraphics().drawImage(sprite, (int)pos.x, (int) pos.y, sprite.getWidth() * scaleX, sprite.getHeight() * scaleY, null);
	}
	
	// draw rotated decal - new
	// TUTORIAL---Decals
	// "The first argument is the location in screen space where the decal should be drawn. 
	// The second is the decal itself. The third is what angle (in radians) should the decal 
	// be rotated. The forth argument specifies the origin of rotation about where the decal 
	// should be rotated. The decal is 8x8 pixels, but I want it to rotate around its center, 
	// so I specify an offset of 4x4. The fifth argument is optional and it represents the scale. 
	// We could enlarge, shrink or mirror the decal here, but I'm keeping it "as is". The final 
	// argument is the tint. This allows us to colour the decal with a bias towards the colour 
	// specified. In our case, the colour is determined by the tile ID at the point of collision, 
	// and as the fragment is updated, we reduce its alpha component. This allows us to make the 
	// coloured fragment appear to fade out."
	//
	// In my case
	//	- the 4th/5th arguments are the rotation points.
	//	- the 6th/7th arguments are the scale
	// 	- the 8th argument is the alpha
	//	- the last argument is the rotation direction i.e. CW/CCW
	public void drawSprite(Vf2d pos, BufferedImage img, float angle, int midX, int midY, int scaleX, int scaleY, float alpha, boolean cwr) {
		drawRotatedDecal((int)pos.x, (int)pos.y, img, angle, midX, midY, scaleX, scaleY, alpha, cwr);
	}
	
	// drawSprite()
	// len of pixels array must be = w * h;
	// e.g. a 16x16 pixels sprite is 256 in size.
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
	
	// drawSprite()
	// A simpler method
	private void drawSprite(int x, int y, BufferedImage img) {
		fBuffer.createGraphics().drawImage(img, x, y, null);
	}
	
	// drawSprite() alpha/transparency
	// must draw immediately!
	private void drawSprite(int x, int y, BufferedImage img, float alpha) {
		Graphics2D g2d = fBuffer.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2d.drawImage(img, x, y, null);
	}
	
	// draw rotated decal with alpha
	// must draw immediately
	private void drawRotatedDecal(int x, int y, BufferedImage img, float angle, int midX, int midY, int scaleX, int scaleY, float alpha, boolean cwr) {
		midX = midX * scaleX / 2; // not sure why midX *= scaleX / 2 doesn't work
		midY = midY * scaleY / 2; // not sure why midY *= scaleY / 2 doesn't work
        int roDirection = cwr ? 1 : -1;
		AffineTransform affineTransform = new AffineTransform(); 
        affineTransform.rotate(Math.toRadians(angle * roDirection), x + midY, y + midY);
        Graphics2D g2d = fBuffer.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2d.setTransform(affineTransform);
		g2d.drawImage(img, x, y, img.getWidth() * scaleX, img.getHeight() * scaleY, null);
       
	}
	
	// drawPartialSprite()
	// stackoverflow.com/questions/19601116/how-to-draw-part-of-a-large-bufferedimage
	// indexX = tile x coord
	// indexY = tile y coord
	// x = x draw position on pane (console)
	// y = y draw position on pane (console)
	// w = width of tile to draw
	// h = hei of tile to draw
	private void drawPartialSprite(int indexX, int indexY, int x, int y, int w, int h, BufferedImage img) {
		fBuffer.createGraphics().drawImage(img.getSubimage(indexX, indexY, w, h), x, y, null);
	}
	
	private void drawPartialSpriteUnscaled(int indexX, int indexY, int x, int y, int w, int h, BufferedImage img) {
		img = img.getSubimage(indexX, indexY, w, h);
		fBuffer.createGraphics().drawImage(img, x, y, img.getWidth() / pixelW, img.getHeight() / pixelH, null);
	}
	
	// New - Aug 27
	private void drawPartialSpriteScaled(int indexX, int indexY, int x, int y, int w, int h, int scaleX, int scaleY, BufferedImage img) {
		img = img.getSubimage(indexX, indexY, w, h);
		fBuffer.createGraphics().drawImage(img, x, y, img.getWidth() * scaleX, img.getHeight() * scaleY, null);
	}
	
	private void drawPixel(int x, int y, Color c) {
		try { fBuffer.setRGB(x,y,c.getRGB()); } catch(Exception e) {}
	}
	
	private void drawPixel(int x, int y, int rgb) {
		try { fBuffer.setRGB(x,y,rgb); } catch(Exception e) {}
	}
	
	public void drawLine(int x, int y, int w, int h, Color c) {
		Graphics2D g2d = fBuffer.createGraphics();
		g2d.setColor(c);
		g2d.drawLine(x, y, w, h);
	}
	
	public void drawVf2d(Vf2d v, Color c) {
		Graphics2D g2d = fBuffer.createGraphics();
		g2d.setColor(c);
		g2d.drawLine(0, 0, (int)v.x, (int)v.y);
		g2d.drawLine(0, 0, (int)v.x, 0);
		g2d.drawLine((int)v.x, 0, (int)v.x, (int)v.y);
	}
	
	public void fillCircle(int x, int y, int w, int h, Color c) {
		Graphics2D g2d = fBuffer.createGraphics();
		g2d.setColor(c);
		g2d.fillOval(x, y, w, h);
	}
	
	public void fillPolygon(Polygon p, Color c) {
		Graphics2D g2d = fBuffer.createGraphics();
		g2d.setColor(c);
		g2d.fillPolygon(p);
	}
	
	public void showVf2dPoint(Vf2d v, int w, int h, Color c, boolean showlines) {
		Graphics2D g2d = fBuffer.createGraphics();
		g2d.setColor(c);
		g2d.fillOval((int)v.x, (int)v.y, w, h);
		if(showlines) drawVf2d(v, c);
	}
	
	public void translateVf2dPoint(Vf2d v, Vf2d translateStart, Color c) {
		Graphics2D g2d = fBuffer.createGraphics();
		g2d.setColor(c);
		int x1 = (int)translateStart.x;
		int y1 = (int)translateStart.y;
		translateStart.add(v);
		int x2 = (int) translateStart.x;
		int y2 = (int) translateStart.y;
		g2d.drawLine(x1, y1, x2, y2);
	}
	
	public void translateVf2dFromTo(Vf2d from, Vf2d to, Color c, float transparency) {
		Graphics2D g2d = fBuffer.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
		g2d.setColor(c);
		int x1 = (int)from.x;
		int y1 = (int)from.y;
		int x2 = (int) to.x;
		int y2 = (int) to.y;
		g2d.drawLine(x1, y1, x2, y2);
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
	 
	public void drawFrame(Graphics2D g2d) {
		g2d.drawImage(fBuffer, 0, 0, len * pixelW, hei * pixelH, null);
	}
	
	public void clearConsole(Color c) {
		Graphics2D g2d = fBuffer.createGraphics();
		g2d.setColor(c);
		g2d.fillRect(0, 0, getConsoleWidth(), getConsoleHeight());
		
		// you can also do this. But it doesn't work. Why?
		// setBackground(c);
	}
	
	public void clearConsoleRegion(int x, int y, int w, int h, Color c) {
		Graphics2D g2d = fBuffer.createGraphics();
		g2d.setColor(c);
		g2d.fillRect(x, y, w, h);
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        drawFrame(g2d);
        
        // Viewport
      	if(viewport != null) viewport.setViewPosition(new Point(VP_X, VP_Y));
        
        g2d.dispose();
    }
	
	// This method is called by the timer automatically
	public void actionPerformed(ActionEvent e) {
		switch(EventListener.keyPressedEvent) {
			case KeyEvent.VK_P:
				pause = allowPause;
			break;
			
			case KeyEvent.VK_C:
				pause = false;
			break;
			
			default:
				//
			break;
		}
		
		// handle timing
		// JavidX9 and stackify.com/heres-how-to-calculate-elapsed-time-in-java/
		then = System.nanoTime();
		float fElapsedTime = then - now;
		fElapsedTime /= 1E9;
		now = then;
		
		// handle KB events
		eventCode = !getEvents().isEmpty() ? getEvents().remove() : -1;
		
		// handle mouse move events
		mouseX = (int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().peek().x : mouseX);
		mouseY = (int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().peek().y : mouseY);
		mouseMoveEventCodeX = (int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().peek().x : -1);
		mouseMoveEventCodeY = (int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().remove().y : -1);
		
		// handle frame update
		if(!OnUserUpdate(fElapsedTime)) timer.stop();
		
		// update title
		console.setTitle(title + "  - FPS " + (int) (1 / fElapsedTime));
		
		// call paintComponent and show screen buffer
		if(!pause) repaint();
	}
	
	private void initGame() { 
		now = System.nanoTime();
		if(!OnUserCreate()) timer.stop();
	}
	
	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::
	// startGui()
	//
	// Opacity/Transparency
	// Javidx9: "Our coloured tiles have black centers. 
	//			"Here we need to think about transparency."
	// 
	// I did not have to worry about this here. Worked find
	// for me. Hmm. Read up on setOpaque().
	//
	// https://github.com/OneLoneCoder/olcPixelGameEngine/wiki/TUTORIAL---Sprites
	// https://docs.oracle.com/javase/7/docs/api/javax/swing/JComponent.html#setOpaque(boolean)
	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::
	private void startGui(int len, int hei, int pixelW, int pixelH) {
		 setPreferredSize(new Dimension(len * pixelW, hei * pixelH));
		 
		 console = new JFrame(title);
		 
		 console.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
		 //console.setLocationRelativeTo(null);
		 
		 console.setLocation(0,0);
		 
		 if(USING_VP) {
			 viewport = new JViewport();
			 
			 viewport.setSize(VP_LEN, VP_HEI);
			 
			 viewport.setViewPosition(new Point(VP_X, VP_Y));
			 
			 viewport.add(this);
			 
			 console.setContentPane(viewport);
			 
			 // Can also use this instead of setContentPane for VP
			 
			 // console.add(viewport, BorderLayout.CENTER);  
			 
			 console.setPreferredSize(new Dimension(VP_LEN * pixelW, VP_HEI * pixelH));
		 }
		 
		 else 
			 console.setContentPane(this);
		 
		 console.setResizable(false);
		 
		 console.setVisible(true);
		 
		 console.pack();
		 
		 console.addKeyListener(eventListener);
		 
		 console.addMouseMotionListener(mouseMotionListener);
		 
		 console.addMouseListener(mouseMotionListener);
		 
		 // !!! Event dispatcher shouldn't call this here but whatever
		 // we are only doing this once.
		 initGame();
    }
	
	public void createStatusWindow(int w, int h) {
	 if(statusWindow == null) {
	  JFrame jfrStatus = new JFrame();
	  //jfrStatus.setUndecorated(true);
	  jfrStatus.setSize(w, h);
	  jfrStatus.setLocationRelativeTo(null);
	  statusWindow = new JTextArea();
	  statusWindow.setBackground(Color.black);
	  statusWindow.setForeground(Color.yellow);
	  statusWindow.setFont(new Font("courier new", Font.BOLD, 12));
	  statusWindow.setEditable(false);
	  jfrStatus.add(statusWindow);
	  jfrStatus.setVisible(true);
	 }
	}
	
	public void setStatus(String s) {
		if (statusWindow == null) return;
		statusWindow.setText(s);
	}
	
	// user must implement these public methods
	public abstract boolean OnUserUpdate(float fElapsedTime);
	public abstract boolean OnUserCreate();
}
