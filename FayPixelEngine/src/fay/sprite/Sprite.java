package fay.sprite;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;

import javax.imageio.ImageIO;

import fay.exceptions.FayPixelEngineException;
import fay.math.Vf2d;

public class Sprite {
	private BufferedImage sprite;
	
	public Sprite(String file) {
		try { sprite = ImageIO.read(new File(file)); } 
		catch (Exception e) { e.printStackTrace(); } 	
	}
	
	public BufferedImage sprite() { return sprite; }
	
	public Image scaledSprite(int scale) {
		return sprite.getScaledInstance(sprite.getWidth() * scale, sprite.getHeight() * scale, Image.SCALE_DEFAULT);
	}
	
	public Image partialSprite(Vf2d index, Vf2d length) {
		return partialSprite((int)index.x, (int)index.y, (int)length.x, (int)length.y);
	}
	
	private Image partialSprite(int indexX, int indexY, int w, int h) {
		return sprite.getSubimage(indexX, indexY, w, h);
	}
	
	// Function multiplyXPlane()
	
	// https://stackoverflow.com/questions/21218557
	
	// returns number of requested tiles horizontally
	
	public BufferedImage multipleXPlaneImage(BufferedImage image, int xLength) throws FayPixelEngineException {
		
	 if(xLength < 1) throw new FayPixelEngineException("New image X length must be > 1");
	
	 BufferedImage joined = new BufferedImage(image.getWidth() * xLength, image.getHeight(), image.getType());
	
	 Graphics2D g2d = joined.createGraphics();
	
	 for(int i = 0; i < xLength; i++) 
		 g2d.drawImage(image, i * image.getWidth(), 0, null);	
	 
	 return joined;
	 
	}
	
	public byte[] pixels() { 
		return ((DataBufferByte) raster().getDataBuffer()).getData();
	}
	
	public int[] pixelsInt() {
		int[] pList = new int[sprite.getWidth() * sprite.getHeight()];
		int i = 0;
		for(int x = 0; x < sprite.getWidth(); x++)
			for(int y = 0; y < sprite.getHeight(); y++)
				pList[i++] = sprite.getRGB(x, y);
		return pList;
	}
	
	// https://docs.oracle.com/javase/7/docs/api/java/awt/image/Raster.html
	public Raster raster() { return sprite.getRaster(); }
	
	public static void main(String args[]) {
		Sprite tile = new Sprite("C:\\Users\\Alifa\\Desktop\\lc3-backup\\eclipse\\Abstract\\images\\tiles\\tut_tile.png");
		byte[] pixels = tile.pixels();
		
		// https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
		final boolean hasAlphaChannel = tile.sprite().getAlphaRaster() != null;
		if(hasAlphaChannel) {
			final int pixelLength = 4;
			for(int pixel = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
	            argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
	            argb += ((int) pixels[pixel + 1] & 0xff); // blue
	            argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
	            argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
	            System.out.println(argb);
			}
		} 
		else {
			final int pixelLength = 3;
			for(int pixel = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
	            argb += -16777216; // 255 alpha
	            argb += ((int) pixels[pixel] & 0xff); // blue
	            argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
	            argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
	            System.out.println(argb);
			}
		}
		
		System.out.println("----------------------------------");
		
		try { Thread.sleep(3000); } catch(Exception e) {}
		
		// Another way of getting pixels:
		int count = 0;
		for(int rgb:tile.pixelsInt()) {
			System.out.println(rgb);
			count++;
		}
		
		System.out.printf("Total pixels: %d", count);
			
	}
	
	
}
