package buffer;

import java.awt.Color;

import fay.exceptions.FayPixelEngineException;
import fay.math.Vf2d;
import fay.original.FayBuffer;
import java.util.Random;

@SuppressWarnings("serial")
public class CustomTile extends FayBuffer {
	
	public static final int GB_COL_LIGHTER_GREEN = 0xe6f8da;
	public static final int GB_COL_LIGHT_GREEN   = 0x99c886;
	public static final int GB_COL_DARK_GREEN    = 0x437969;
	public static final int GB_COL_BLACK         = 0x051f2a;
	
	public CustomTile(int len, int hei, int pX, int pY, String title) {	 
	 super(len,hei,pX,pY,title);
	}
	
	public int[] getPixels(int hByte, int lByte) {
		int[] pixels = new int[8];
		int[] hBits, lBits;
		hBits = new int[8];
		lBits = new int[8];
		hBits[0] = (hByte & 0b10000000) >> 7;
		hBits[1] = (hByte & 0b01000000) >> 6;
		hBits[2] = (hByte & 0b00100000) >> 5;
		hBits[3] = (hByte & 0b00010000) >> 4;
		hBits[4] = (hByte & 0b00001000) >> 3;
		hBits[5] = (hByte & 0b00000100) >> 2;
		hBits[6] = (hByte & 0b00000010) >> 1;
		hBits[7] = hByte & 0b00000001;
		
		lBits[0] = (lByte & 0b10000000) >> 7;
		lBits[1] = (lByte & 0b01000000) >> 6;
		lBits[2] = (lByte & 0b00100000) >> 5;
		lBits[3] = (lByte & 0b00010000) >> 4;
		lBits[4] = (lByte & 0b00001000) >> 3;
		lBits[5] = (lByte & 0b00000100) >> 2;
		lBits[6] = (lByte & 0b00000010) >> 1;
		lBits[7] = lByte & 0b00000001;
		
		for(int i = 0; i < 8; i++) pixels[i] = hBits[i] << 1 | lBits[i];
		
		return pixels;
	}
	
	public static void main(String args[]) {
	 new CustomTile(200, 200, 3, 4, "Custom Tile example");
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		clearConsole(Color.WHITE);
		Random rand = new Random();
		rand.setSeed(System.nanoTime());
		int tile[] = new int[16*16];
		for(int i = 0; i < tile.length; i++) tile[i] = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()).getRGB();
		drawSprite(new Vf2d(100, 100), new Vf2d(16, 16), tile);
		
		tile = new int[16]; // The letter 'A'
		tile[0]  = 0x7c;    // low  byte
		tile[1]  = 0x7c;    // high byte
		tile[2]  = 0;
		tile[3]  = 0xc6;
		tile[4]  = 0xc6;
		tile[5]  = 0;
		tile[6]  = 0;
		tile[7]  = 0xfe;
		tile[8]  = 0xc6;
		tile[9]  = 0xc6;
		tile[10] = 0;
		tile[11] = 0xc6;
		tile[12] = 0xc6;
		tile[13] = 0;
		tile[14] = 0;
		tile[15] = 0;
		
		int newTile[] = new int[8*8];
		
		int index = 0;
		for(int i = 0; i < tile.length; i += 2) {
		 int[] pixels = getPixels(tile[i+1], tile[i] );
		 for(int j = 0; j < pixels.length; j++) 
		  try { newTile[index++] = getColor(pixels[j]); } 
		  catch(FayPixelEngineException fpe) { fpe.printStackTrace(); System.exit(-1); }
		}
		
		drawSprite(new Vf2d(100, 10), new Vf2d(8, 8), newTile);
		
		try {
		 fillRect(10, 10, 20, 20, new Color(getColor(0))); 
		 fillRect(10, 30, 20, 20, new Color(getColor(1))); 
		 fillRect(10, 50, 20, 20, new Color(getColor(2))); 
		 fillRect(10, 70, 20, 20, new Color(getColor(3)));
		} catch (FayPixelEngineException fpe) { fpe.printStackTrace(); System.exit(-1); } 
		
		return true;
	}

	@Override
	public boolean OnUserCreate() {
		addColor(GB_COL_LIGHTER_GREEN);
		addColor(GB_COL_LIGHT_GREEN);
		addColor(GB_COL_DARK_GREEN);
		addColor(GB_COL_BLACK);
		
		return true;
	}
	
	

}
