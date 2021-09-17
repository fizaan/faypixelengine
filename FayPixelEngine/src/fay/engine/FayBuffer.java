package fay.engine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import fay.abstct.Console;
import fay.math.Vf2d;

@SuppressWarnings("serial")
public abstract class FayBuffer extends Console {
	
	public FayBuffer() {
		super();
		init();
	}
	
	public FayBuffer(int len, int hei, int pixelW, int pixelH, String title) {
		super(len, hei, pixelW, pixelH, title);
		System.out.println("With Buffer");
		init();
	}

	@Override
	public void drawScaledSprite(Vf2d pos, Image img) {
		getFrameBufferImg().createGraphics().drawImage(img, (int)pos.x, (int) pos.y, null);
	}

	@Override
	public void drawScaledSprite(Vf2d pos, BufferedImage sprite, int scale) {
		getFrameBufferImg().createGraphics().drawImage(sprite, (int)pos.x, (int) pos.y, sprite.getWidth() * scale, sprite.getHeight() * scale, null);
	}

	@Override
	public void drawSprite(int x, int y, BufferedImage img) {
		getFrameBufferImg().createGraphics().drawImage(img, x, y, null);
	}

	@Override
	public void drawSprite(int x, int y, BufferedImage img, float alpha) {
		Graphics2D g2d = getFrameBufferImg().createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2d.drawImage(img, x, y, null);
	}

	@Override
	public void drawRotatedDecal(int x, int y, BufferedImage img, float angle, int midX, int midY, int scaleX,
			int scaleY, float alpha, boolean cwr) {
		midX = midX * scaleX / 2; // not sure why midX *= scaleX / 2 doesn't work
		midY = midY * scaleY / 2; // not sure why midY *= scaleY / 2 doesn't work
        int roDirection = cwr ? 1 : -1;
		AffineTransform affineTransform = new AffineTransform(); 
        affineTransform.rotate(Math.toRadians(angle * roDirection), x + midY, y + midY);
        Graphics2D g2d = getFrameBufferImg().createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2d.setTransform(affineTransform);
		g2d.drawImage(img, x, y, img.getWidth() * scaleX, img.getHeight() * scaleY, null);
	}

	@Override
	public void drawPartialSprite(int indexX, int indexY, int x, int y, int w, int h, BufferedImage img) {
		getFrameBufferImg().createGraphics().drawImage(img.getSubimage(indexX, indexY, w, h), x, y, null);
	}

	@Override
	public void drawPartialSpriteUnscaled(int indexX, int indexY, int x, int y, int w, int h, BufferedImage img) {
		img = img.getSubimage(indexX, indexY, w, h);
		getFrameBufferImg().createGraphics().drawImage(img, x, y, img.getWidth() / getPixelX(), img.getHeight() / getPixelY(), null);
	}

	@Override
	public void drawPixel(int x, int y, Color c) {
		try { getFrameBufferImg().setRGB(x,y,c.getRGB()); } catch(Exception e) {}
	}

	@Override
	public void drawPixel(int x, int y, int rgb) {
		try { getFrameBufferImg().setRGB(x,y,rgb); } catch(Exception e) {}
	}

	@Override
	public void drawLine(int x, int y, int w, int h, Color c) {
		Graphics2D g2d = getFrameBufferImg().createGraphics();
		g2d.setColor(c);
		g2d.drawLine(x, y, w, h);
	}

	@Override
	public void drawVf2d(Vf2d v, Color c) {
		Graphics2D g2d = getFrameBufferImg().createGraphics();
		g2d.setColor(c);
		g2d.drawLine(0, 0, (int)v.x, (int)v.y);
		g2d.drawLine(0, 0, (int)v.x, 0);
		g2d.drawLine((int)v.x, 0, (int)v.x, (int)v.y);
	}

	@Override
	public void fillCircle(int x, int y, int w, int h, Color c) {
		Graphics2D g2d = getFrameBufferImg().createGraphics();
		g2d.setColor(c);
		g2d.fillOval(x, y, w, h);
	}

	@Override
	public void fillPolygon(Polygon p, Color c) {
		Graphics2D g2d = getFrameBufferImg().createGraphics();
		g2d.setColor(c);
		g2d.fillPolygon(p);
	}

	@Override
	public void showVf2dPoint(Vf2d v, int w, int h, Color c, boolean showlines) {
		Graphics2D g2d = getFrameBufferImg().createGraphics();
		g2d.setColor(c);
		g2d.fillOval((int)v.x, (int)v.y, w, h);
		if(showlines) drawVf2d(v, c);
	}

	@Override
	public void translateVf2dPoint(Vf2d v, Vf2d translateStart, Color c) {
		Graphics2D g2d = getFrameBufferImg().createGraphics();
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
		Graphics2D g2d = getFrameBufferImg().createGraphics();
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
		g2d.drawImage(getFrameBufferImg(), 0, 0, getConsoleWidth() * getPixelX(), getConsoleHeight() * getPixelY(), null);
	}

	@Override
	public void clearConsole(Color c) {
		Graphics2D g2d = getFrameBufferImg().createGraphics();
		g2d.setColor(c);
		g2d.fillRect(0, 0, getConsoleWidth(), getConsoleHeight());
	}

	@Override
	public void clearConsoleRegion(int x, int y, int w, int h, Color c) {
		Graphics2D g2d = getFrameBufferImg().createGraphics();
		g2d.setColor(c);
		g2d.fillRect(x, y, w, h);
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        drawFrame(g2d);
        g2d.dispose();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// handle timing
		// JavidX9 and stackify.com/heres-how-to-calculate-elapsed-time-in-java/
		setThen(System.nanoTime());
		float fElapsedTime = then() - now();
		fElapsedTime /= 1E9;
		setNow(then());
		
		// handle KB events
		setEventCode(!getEvents().isEmpty() ? getEvents().remove() : -1);
		
		// handle mouse move events
		setMouseX((int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().peek().x : mouseX()));
		setMouseY((int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().peek().y : mouseY()));
		setMouseMoveEventCodeX((int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().peek().x : -1));
		setMouseMoveEventCodeY((int) (!getMouseMoveEvents().isEmpty() ? getMouseMoveEvents().remove().y : -1));
		
		// handle frame update
		if(!initializeGameSettingsManually) {
			if(!OnUserUpdate(fElapsedTime)) stopTimer();
			
			// update title
			setTitle(title() + "  - FPS " + (int) (1 / fElapsedTime));
		}
		
		// call paintComponent and show screen buffer
		repaint();
		
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
        
        try {
			SwingUtilities.invokeAndWait(() -> startGui(getConsoleWidth(),getConsoleHeight(),getPixelX(),getPixelY()));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// user must implement these public methods
	public abstract boolean OnUserUpdate(float fElapsedTime);
	public abstract boolean OnUserCreate();

}
