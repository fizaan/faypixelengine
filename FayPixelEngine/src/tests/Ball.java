package tests;

import java.awt.Color;
import java.util.Random;

import fay.math.Vf2d;

public class Ball {
	public Vf2d location, velocity, acceleration, gravity;
	private int radius;
	private Color c;
	private Random rand;
	
	public Ball(float x, float y, float speedx, float speedy) {
		rand = new Random();
		location = new Vf2d(x, y);
		velocity = new Vf2d(speedx, speedy);
		acceleration = new Vf2d(0.00001f, 0.00001f);
		gravity = new Vf2d(0,0);
		c = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
	}
	
	public Ball(float x, float y) {
		rand = new Random();
		location = new Vf2d(x, y);
		velocity = Vf2d.createSlowRandomVf2d();
		acceleration = Vf2d.createSlowRandomVf2d();
		gravity = Vf2d.createRandomVf2d();
		c = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
	}
	
	public void setRadius(int radius) { this.radius = radius; }
	public int radius() { return radius; }
	public Color colour() { return c; }
	
	public void setAcceleration(float x, float y) { acceleration.setPosition(x, y);} 
	public void setGravity(float x, float y) { gravity.setPosition(x, y); } 
	
	public void setRandomAccleration() {
		acceleration = Vf2d.createRandomVf2d(0.001f, 0.001f);
	}
	
	private void constantVel() { 
		location.add(velocity); 
	}
	
	private void move() { 
		velocity.add(acceleration);
		location.add(velocity); 
	}
	
	private void bounce(int w, int h) {
		if(location.x + radius > w || location.x < 0) velocity.x *= -1;
		if(location.y + radius > h || location.y < 0) velocity.y *= -1;
	}
	
	private void edges(int w, int h) {
		if(location.x + radius > w) 		location.x = 0;
		if(location.x < 0) 					location.x = w; 
		if(location.y + radius > h)			location.y = 0;
		if(location.y < 0)					location.y = h;
	}
	
	public void update2(int w, int h) {
		move();
		edges(w, h);
	}
	
	public void update(int w, int h) {
		move();
		bounce(w,h);
	}
	
	public void updateWithGravity(boolean airfriction) {
		if(airfriction)
			acceleration.add(Vf2d.mul(gravity, -0.45f));
		else
			acceleration.add(gravity);
		velocity.add(acceleration);
		location.add(velocity);
	}
	
	public void constantVel(int w, int h) {
		constantVel();
		bounce(w,h);
	}
} 
