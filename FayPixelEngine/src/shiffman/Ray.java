package shiffman;

import fay.math.Vf2d;

public class Ray {
	// a ray has a position and a direction it is pointing to which is 
	// determined by the angle in the x and y plane.
	//
	// when a ray hits a wall, I set the intersectPoint but only if
	// the wall is the closest to the ray
	public Vf2d position, direction, intersectPoint;
	
	// Euclidean Distance is used by View to render the color of wall
	// the greater the distance to wall intersect, the lesser the intensity of wall color
	// the smaller the distance to wall intersect, the more intense the color
	private float euDistance;
	
	public Ray(float x, float y, float angleX, float angleY) {
		position = new Vf2d(x, y);
		direction = new Vf2d(angleX, angleY);
	}
	
	public float euDis() { return euDistance; }
	
	public Ray(float x, float y) {
		position = new Vf2d(x, y);
		direction = Vf2d.createSlowRandomVf2d();
	}
	
	public void setIntersect(Vf2d i) {
		if(intersectPoint == null) {
			intersectPoint = i;
			euDistance = Vf2d.dist(position, i);
		} else {
			float newIDistance = Vf2d.dist(position, i);
			if(newIDistance < euDistance) {
				intersectPoint = i;
				euDistance = newIDistance;
			}
		}
	}
} 
