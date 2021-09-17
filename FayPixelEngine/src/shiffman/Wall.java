package shiffman;

import fay.math.Vf2d;

public class Wall {
	public Vf2d a, b;
	
	public void createWall(float x1, float y1, float x2, float y2) {
		a = new Vf2d(x1, y1);
		b = new Vf2d(x2, y2);
	}

}
