package sansbuffer.JMario;

import shiffman.Ray;

public class JMarioRay extends Ray {
	public int number;
	public boolean collided;
	public static final int A_TOP_LEFT 		= 45;	// South east bound collision point
	public static final int B_TOP 			= 90;	// South bound collision point
	public static final int C_TOP_RIGHT 	= 135;	// South west bound collision point
	public static final int D_RIGHT 		= 180;	// West bound collision point
	public static final int E_BOTTOM_RIGHT 	= 225;	// North west bound collision point
	public static final int F_BOTTOM 		= 270;	// North bound collision point
	public static final int G_BOTTOM_LEFT 	= 315;	// North east bound collision point
	public static final int H_LEFT 			= 0;	// East bound collision point
	
	public JMarioRay(int number, float x, float y, float angleX, float angleY) {
		super(x, y, angleX, angleY);
		this.number = number;
	}
}
