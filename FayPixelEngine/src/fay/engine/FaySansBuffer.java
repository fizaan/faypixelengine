package fay.engine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fay.abstct.Console;
import fay.math.Vf2d;

@SuppressWarnings("serial")
public abstract class FaySansBuffer extends Console {
	
	private Graphics2D g2d;
	
	public FaySansBuffer() {
		super();
		init();
	}
	
	public FaySansBuffer(int len, int hei, int pixelW, int pixelH, String title) {
		super(len, hei, pixelW, pixelH, title);
		System.out.println("Sans Buffer");
		init();
	}

	@Override
	public void drawScaledSprite(Vf2d pos, Image img) {
		g2d.drawImage(img, (int)pos.x, (int) pos.y, null);
	}

	@Override
	public void drawScaledSprite(Vf2d pos, BufferedImage sprite, int scale) {
		g2d.drawImage(sprite, (int)pos.x, (int) pos.y, sprite.getWidth() * scale, sprite.getHeight() * scale, null);
	}

	@Override
	public void drawSprite(int x, int y, BufferedImage img) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawSprite(int x, int y, BufferedImage img, float alpha) {
		g2d.drawImage(img, x, y, img.getWidth() * getPixelX(), img.getHeight() * getPixelY(), null);
	}

	@Override
	public void drawRotatedDecal(int x, int y, BufferedImage img, float angle, int midX, int midY, int scaleX,
			int scaleY, float alpha, boolean cwr) {
		midX = midX * scaleX / 2; 
		midY = midY * scaleY / 2;
        int roDirection = cwr ? 1 : -1;
		AffineTransform affineTransform = new AffineTransform(); 
        affineTransform.rotate(Math.toRadians(angle * roDirection), x + midY, y + midY);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2d.setTransform(affineTransform);
		g2d.drawImage(img, x, y, img.getWidth() * scaleX, img.getHeight() * scaleY, null);
	}

	@Override
	public void drawPartialSprite(int indexX, int indexY, int x, int y, int w, int h, BufferedImage img) {
		BufferedImage newImg = img.getSubimage(indexX, indexY, w, h);
		g2d.drawImage(newImg, x * getPixelX(), y * getPixelY(), newImg.getWidth() * getPixelX(), newImg.getHeight() * getPixelY(), null);
	}

	@Override
	public void drawPartialSpriteUnscaled(int indexX, int indexY, int x, int y, int w, int h, BufferedImage img) {
		img = img.getSubimage(indexX, indexY, w, h);
		g2d.drawImage(img, x, y, null);
	}

	@Override
	public void drawPixel(int x, int y, Color c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawPixel(int x, int y, int rgb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawLine(int x, int y, int w, int h, Color c) {
		g2d.setColor(c);
		g2d.drawLine(x, y, w, h);
	}

	@Override
	public void drawVf2d(Vf2d v, Color c) {
		g2d.setColor(c);
		g2d.drawLine(0, 0, (int)v.x, (int)v.y);
		g2d.drawLine(0, 0, (int)v.x, 0);
		g2d.drawLine((int)v.x, 0, (int)v.x, (int)v.y);
	}

	@Override
	public void fillCircle(int x, int y, int w, int h, Color c) {
		g2d.setColor(c);
		g2d.fillOval(x, y, w * getPixelX(), h * getPixelY());
	}

	@Override
	public void fillPolygon(Polygon p, Color c) {
		g2d.setColor(c);
		g2d.fillPolygon(p);
	}

	@Override
	public void showVf2dPoint(Vf2d v, int w, int h, Color c, boolean showlines) {
		g2d.setColor(c);
		g2d.fillOval((int)v.x, (int)v.y, w * getPixelX(), h * getPixelY());
		if(showlines) drawVf2d(v, c);
	}

	@Override
	public void translateVf2dPoint(Vf2d v, Vf2d translateStart, Color c) {
		g2d.setColor(c);
		int x1 = (int)translateStart.x;
		int y1 = (int)translateStart.y;
		translateStart.add(v);
		int x2 = (int) translateStart.x;
		int y2 = (int) translateStart.y;
		g2d.drawLine(x1, y1, x2, y2);
		
	}

	@Override
	public void translateVf2dFromTo(Vf2d from, Vf2d to, Color c, float transparency) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
		g2d.setColor(c);
		int x1 = (int)from.x;
		int y1 = (int)from.y;
		int x2 = (int) to.x;
		int y2 = (int) to.y;
		g2d.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawFrame(Graphics2D g2d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearConsole(Color c) {
		if(g2d == null) return;
		g2d.setColor(c);
		g2d.fillRect(0, 0, getConsoleWidth() * getPixelX(), getConsoleHeight() * getPixelY());
	}

	@Override
	public void clearConsoleRegion(int x, int y, int w, int h, Color c) {
		g2d.setColor(c);
		g2d.fillRect(x, y, w, h);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
		
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
        g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // handle timing
 		// JavidX9 and stackify.com/heres-how-to-calculate-elapsed-time-in-java/
 		setThen(System.nanoTime());
 		float fElapsedTime = then() - now();
 		fElapsedTime /= 1E9;
 		setNow(then());
 		
 		// handle KB events
 		setEventCode(!getEvents().isEmpty() ? getEvents().remove() : -1);
 		
 		// handle mouse move events
 		setMouseX ((int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().peek().x : mouseX()));
 		setMouseY ((int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().peek().y : mouseY()));
 		setMouseMoveEventCodeX((int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().peek().x : -1));
 		setMouseMoveEventCodeY((int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().remove().y : -1));
 		
 		// handle frame update
 		if(!OnUserUpdate(fElapsedTime)) stopTimer();
 		
 		// update title
 		setTitle(title() + "  - FPS " + (int) (1 / fElapsedTime));
     		
        g2d.dispose();
	}
	
	private void initGame() { 
		setNow(System.nanoTime());
		if(!initializeGameSettingsManually) {
			if(!OnUserCreate()) stopTimer();
		}
		else
			System.out.println("Warning: You must call onUserCreate manually");
				
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
        
        initGame(); // must call before invoking swing gui!
        
        SwingUtilities.invokeLater(() -> startGui(getConsoleWidth(),getConsoleHeight(),getPixelX(),getPixelY()));
	}
	
	// user must implement these public methods
	public abstract boolean OnUserUpdate(float fElapsedTime);
	public abstract boolean OnUserCreate();

}
