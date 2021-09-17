// 1.3 Random vectors - the nature of code by D.Shiffman

package sansbuffer;

import java.awt.Color;

import fay.engine.FaySansBuffer;
import fay.math.Vf2d;

@SuppressWarnings("serial")
public class RandomVectors extends FaySansBuffer {
	
	public static void main(String args[]) {
		new RandomVectors(400,300,1,1, "Vectors - Random vectors");
	}

	public RandomVectors(int len, int hei, int pX, int pY, String title) {
		super(len,hei,pX,pY,title);
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		boolean square = false;
		
		if(square) { // square
		 Vf2d v = Vf2d.createRandomVf2d(-50.0f, 50.0f);
		 translateVf2dPoint(v, new Vf2d(getConsoleWidth()/2,getConsoleHeight()/2), Color.WHITE); 
		} 
		else { // circle
		 Vf2d v = Vf2d.createRandomVf2d();
		 v.mul(50);
		 translateVf2dPoint(v, new Vf2d(getConsoleWidth()/2,getConsoleHeight()/2), Color.WHITE); 
		}
		
		return true;
	}

	@Override
	public boolean OnUserCreate() {
		setBackground(Color.BLACK);
		return true;
	}

}
