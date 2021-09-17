// Code challenge #145: 2D Raycasting

package sansbuffer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fay.engine.FaySansBuffer;
import fay.math.Vf2d;
import shiffman.Ray;
import shiffman.StaticHelper;
import shiffman.Wall;

@SuppressWarnings("serial")
public class Ray2D extends FaySansBuffer {
	
	private List<Ray> rays;
	private List<Wall> walls;
	private Random rand;
	
	public static void main(String args[]) {
		new Ray2D(400,400,1,1, "2D Rays casting");
	}
	
	public Ray2D(int len, int hei, int pX, int pY, String title) {
		super(len,hei,pX,pY,title);
	}
	
	public Ray createRay(float x, float y, float angleX, float angleY) {
		return new Ray(x, y, angleX, angleY);
	}
	
	public Wall createWall(float x1, float y1, float x2, float y2) {
		Wall wall = new Wall();
		wall.createWall(x1, y1, x2, y2);
		return wall;
	}
	
	private void showWall(Wall wall, Color c) {
		drawLine((int)wall.a.x, (int)wall.a.y, (int)wall.b.x, (int)wall.b.y, c);
	}
	
	private void showRay(Ray ray, Vf2d intersect, Color c) {
		translateVf2dFromTo(ray.position, intersect, c, 0.02f);
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		switch(getEventCode()) {
			case KeyEvent.VK_SPACE:
				walls = arrayOfWalls();
				addborder();
			break;
			
			default:
				//
			break;
		}
		clearConsole(Color.BLACK);
		showWalls();
		lookAround();
		showRays();
		return true;
	}
	
	private void lookAround() {
		// cast each ray to each wall
		// and find the closest intersect point
		for(Ray ray: rays) {
		 ray.intersectPoint = null;
		 for(Wall wall: walls)
		  look(ray, wall);
		}
	}
	
	private void showRays() {
		for(Ray ray: rays)
		 if(ray.intersectPoint != null)
		  showRay(ray, ray.intersectPoint, Color.WHITE);
	}
	
	private void look(Ray ray, Wall wall) {
		ray.position.setPosition(mouseXAdjust(),mouseYAdjust());
		Vf2d intersect = cast(ray, wall);
		if(intersect != null) ray.setIntersect(intersect);
	}
	
	private void showWalls() {
		// show only the 5 main walls not borders
		for(int i = 0; i < 5; i++)
			showWall(walls.get(i), Color.WHITE);
	}

	@Override
	public boolean OnUserCreate() {
		rand = new Random();
		rays = arrayOfRays(getWidth() / 2, getHeight() / 2);
		walls = arrayOfWalls();
		addborder();
		return true;
	}
	
	private void addborder() {
		//Border wall
		Wall left = createWall(0, 0, 0, getConsoleHeight());
		Wall right = createWall(getConsoleWidth(), 0, getConsoleWidth(), getConsoleHeight());
		Wall top = createWall(0, 0, getConsoleWidth(), 0);
		Wall bottom = createWall(0, getConsoleHeight(), getConsoleWidth(), getConsoleHeight());
		
		walls.add(left);
		walls.add(right);
		walls.add(top);
		walls.add(bottom);
	}
	
	// This is his 'particle' class - clearly I'm approaching this differently :p
	// his 'angle' is my 'direction' in the Ray class
	private List<Ray> arrayOfRays(float startx, float starty) {
		List<Ray> rays = new ArrayList<Ray>();
		for(float i = 0; i < 360; i += 0.1f) {
			float angle = StaticHelper.degToRad(i);
			float x = (float) Math.cos(angle);
			float y  = (float) Math.sin(angle);
			rays.add(createRay(startx, starty, x, y));
		}
		return rays;
			
	}
	
	// create 5 walls
	private List<Wall> arrayOfWalls() {
		List<Wall> walls = new ArrayList<Wall>();
		for(int i = 0; i < 5; i++)
			walls.add(createWall(rand.nextInt(getConsoleWidth()), rand.nextInt(getConsoleHeight()), 
					rand.nextInt(getConsoleWidth()), rand.nextInt(getConsoleHeight())));
		return walls;
			
	}
	
	private Vf2d cast(Ray ray, Wall thiswall) {
		float x1 = thiswall.a.x;
		float y1 = thiswall.a.y;
		float x2 = thiswall.b.x;
		float y2 = thiswall.b.y;
		
		float x3 = ray.position.x;
		float y3 = ray.position.y;
		float x4 = ray.position.x + ray.direction.x;
		float y4 = ray.position.y + ray.direction.y;
		
		float den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		
		if(den == 0) return null; // parallel - will never intersect
		
		float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
		float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / den;
		
		if(t > 0 && t < 1 && u > 0) {
			float px = x1 + t * (x2 - x1);
			float py = y1 + t * (y2 - y1);
			return new Vf2d(px, py);
		}
		else 
			return null;
	}

}
