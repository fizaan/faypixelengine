package fay.math;

import java.util.Random;

import shiffman.StaticHelper;

public class Vf2d {
	public float x, y;
	private static Random rand = new Random();
	
	public Vf2d() { x = 0; y = 0; }
	public Vf2d(float x, float y) { this.x = x; this.y = y; }
	
	public static Vf2d createRandomVf2d() { 
		Vf2d v = new Vf2d(rand.nextFloat() * 2 - 1, rand.nextFloat() * 2 - 1);
		v.normalize();
		return v;
	} 
	
	public static Vf2d createSlowRandomVf2d() { 
		Vf2d v = new Vf2d(rand.nextFloat() * 0.2f - 0.1f, rand.nextFloat() * 0.2f - 0.1f);
		return v;
	} 
	
	public static Vf2d createRandomVf2d(float min, float max) { 
		return new Vf2d(rand.nextFloat() * (max - min) + min, rand.nextFloat() * (max - min) + min); 
	}
	
	public void add(Vf2d v) { x += v.x; y += v.y; }
	public void sub(Vf2d v) { x -= v.x; y -= v.y; }
	public void mul(Vf2d v) { x *= v.x; y *= v.y; }
	
	public void add(float v) { x += v; y += v; }
	public void sub(float v) { x -= v; y -= v; }
	public void mul(float v) { x *= v; y *= v; }
	
	public void setPosition(float x, float y) { this.x = x; this.y = y; }
	public void setAngledPosition(float angleDeg) {
	 setPosition(
	  (float) (Math.cos(StaticHelper.degToRad(angleDeg))), 
	  (float) (Math.sin(StaticHelper.degToRad(angleDeg))));
	}
	
	// sept 3 2021
	// new function: setAngledPosition(float angleDeg, float fXSpeedAtJump)
	// 
	// when mario jumps, his x velocity at the time of jump should 
	// be relative to his ground x speed just before the jump. Hence why
	// I made this function. This function is called when pressing the
	// keys below. The value I chose for fXSpeedAtJump is for the divisor 
	// value of 1.2f. Note: only affects x.
	//		- D+J
	//		- S+J
	public void setAngledPosition(float angleDeg, float fXSpeedAtJump) {
	 Vf2d v = clone();
	 setPosition(
	  (float) (Math.cos(StaticHelper.degToRad(angleDeg))), 
	  (float) (Math.sin(StaticHelper.degToRad(angleDeg))));
	 x += v.x / fXSpeedAtJump;
	 
	 // But ... make sure x is not too big.. so I cap it here
	 
	 if (x >= 1.0f) 			x = 1.0f;
	 
	 else if (x <= -1.0f) 	x = -1.0f;
	}
	
	public void setMagUnused(float f) {
		float mag = mag();
		y = (y / mag) * (f * f);
		x = (float) Math.sqrt((f * f) - (y * y));
	}
	
	public float mag() { return (float) Math.sqrt(x * x + y * y); }
	
	public void normalize() { 
		float mag = mag();
		x /= mag;
		y /= mag;
	}
	
	// normalizedAngle(angleDeg)
	// returns a unit vector of magnitude 1 but
	// who's x and y are based on a given angle
	public static Vf2d normalizedAngle(float angleDeg) {
	 return new Vf2d(
	  (float)Math.cos(StaticHelper.degToRad(angleDeg)), 
	  (float)Math.sin(StaticHelper.degToRad(angleDeg)));
	}
	
	public void limit(float f) { 
		if (mag() >= f) {
			x = x / f;
			y = y / f;
		}
	}
	
	public void setMag(float m) { normalize(); mul(m); }
	
	public static Vf2d add(Vf2d a, Vf2d b) { return new Vf2d(a.x + b.x, a.y + b.y); }
	public static Vf2d sub(Vf2d a, Vf2d b) { return new Vf2d(a.x - b.x, a.y - b.y); }
	public static Vf2d mul(Vf2d a, Vf2d b) { return new Vf2d(a.x * b.x, a.y * b.y); }
	
	public static Vf2d add(Vf2d a, float v) { return new Vf2d(a.x + v, a.y + v); }
	public static Vf2d sub(Vf2d a, float v) { return new Vf2d(a.x - v, a.y - v); }
	public static Vf2d mul(Vf2d a, float v) { return new Vf2d(a.x * v, a.y * v); }
	
	public static float dist(Vf2d a, Vf2d b) {
		// distance formula: calculating distance between any two coordinates
		// https://www.khanacademy.org/
		float x1 = a.x;
		float y1 = a.y;
		float x2 = b.x;
		float y2 = b.y;
		return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	// Calculates and returns the angle of rotation 
	// for this vector(only 2D vectors) in radians
	// https://p5js.org/reference/#/p5.Vector/heading
	// https://stackoverflow.com/questions/3449826/
	// https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html
	public float heading() { return (float) Math.atan2(y, x); }
	
	@Override
	public String toString() {
		return String.format("(%f, %f) %f", x, y, mag());
	}
	
	public Vf2d clone() { return new Vf2d(x, y); }

}
