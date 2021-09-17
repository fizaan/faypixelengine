package shiffman;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fay.abstct.Console;
import fay.exceptions.FayPixelEngineException;
import fay.math.Vf2d;
import fay.original.FaySansBuffer;
import sansbuffer.Player;
import sansbuffer.JMario.JMarioRay;

public class StaticHelper {
	
	public static List<Ray> arrayOfRays(float startx, float starty, float angleStart, float angleEnd, float angleInc) {	
		List<Ray> rays = new ArrayList<Ray>();
		for(float i = angleStart / 2; i <= angleEnd / 2; i += angleInc) {
			float angle = degToRad(i);
			float x = (float) Math.cos(angle);
			float y  = (float) Math.sin(angle);
			rays.add(createRay(startx, starty, x, y));
		}
		System.out.println("Total rays cast from player's EYE: " + rays.size());
		
		return rays;		
	}
	
	private static Ray createRay(float x, float y, float angleX, float angleY) {
		return new Ray(x, y, angleX, angleY);
	}
	
	private static JMarioRay createJMarioRay(int number, float x, float y, float angleX, float angleY) {
		return new JMarioRay(number, x, y, angleX, angleY);
	}
	
	public static Wall createWall(float x1, float y1, float x2, float y2) {
		Wall wall = new Wall();
		wall.createWall(x1, y1, x2, y2);
		return wall;
	}
	
	public static void createBlock(float x, float y, float w, float h, List<Wall> walls) {
		walls.add(createWall(x,y,x,y+h));
		walls.add(createWall(x,y,x+w,y));
		walls.add(createWall(x+w,y,x+w,y+h));
		walls.add(createWall(x,y+h,x+w,y+h));
	}
	
	public static void showWalls(List<Wall> walls, Console obj) {
		// show all walls added to 'walls' array
		for(int i = 0; i < walls.size(); i++)
			showWall(walls.get(i), Color.WHITE, obj);
	}
	
	public static void showWalls(List<Wall> walls, FaySansBuffer buf, Color c) {
		// show all walls added to 'walls' array
		for(int i = 0; i < walls.size(); i++)
			showWall(walls.get(i), c, buf);
	}
	
	private static void showWall(Wall wall, Color c, Console obj) {
		obj.drawLine((int)wall.a.x, (int)wall.a.y, (int)wall.b.x, (int)wall.b.y, c);
	}
	
	public static void showWall(Wall wall, Color c, FaySansBuffer buf) {
		buf.drawLine((int)wall.a.x, (int)wall.a.y, (int)wall.b.x, (int)wall.b.y, c);
	}
	
	public static void showRays(List<Ray> rays, Console obj) {
		for(Ray ray: rays)
		 if(ray.intersectPoint != null)
		  showRay(ray, ray.intersectPoint, Color.WHITE, obj);
	}
	
	private static void showRay(Ray ray, Vf2d intersect, Color c, Console obj) {
		obj.translateVf2dFromTo(ray.position, intersect, c, 0.1f);
	}
	
	public static void showRays(List<Ray> rays, FaySansBuffer buf, Color c) {
		for(Ray ray: rays)
		 if(ray.intersectPoint != null)
		  showRay(ray, ray.intersectPoint, c, buf);
	}
	
	// Sept 6 2021
	public static void showMarioRays(Map<Integer, JMarioRay> rays, FaySansBuffer buf, Color c, Player p, boolean showRay) {
		for(Integer s:rays.keySet()) {
		 JMarioRay ray = rays.get(s);
		 if(ray.intersectPoint != null) {
		  switch(ray.number) {
		   case JMarioRay.B_TOP: 
			   float diff = ray.intersectPoint.y - p.radius();
			   if(diff > 0) p.setGroundPosition(ray.intersectPoint.y - p.radius()); 				break;
		   case JMarioRay.H_LEFT: 			p.setEBoundCP(ray.intersectPoint.x); 					break;
		   case JMarioRay.D_RIGHT:			p.setWBoundCP(ray.intersectPoint.x);					break;
		   case JMarioRay.F_BOTTOM:			p.setNBoundCP(ray.intersectPoint.y);					break;
		   
		   // SE SW NE NW collision points. But...why am I using the y coordinate and not x coordinate?
		   
		   case JMarioRay.A_TOP_LEFT: 		p.setSEBoundCP(ray.intersectPoint.y); 					break;
		   case JMarioRay.C_TOP_RIGHT: 		p.setSWBoundCP(ray.intersectPoint.y); 					break;
		   case JMarioRay.E_BOTTOM_RIGHT: 	p.setNWBoundCP(ray.intersectPoint.y); 					break;
		   case JMarioRay.G_BOTTOM_LEFT: 	p.setNEBoundCP(ray.intersectPoint.y); 					break;
		   default: break;
		  
		  }
		  if (showRay) showRay(ray, ray.intersectPoint, c, buf);
		 }
		}
	}
	
	// Sept 5 2021
	public static void showMarioRays(Map<Integer, JMarioRay> rays, FaySansBuffer buf, Color c, Vf2d tile) {
		for(Integer s:rays.keySet()) {
		 JMarioRay ray = rays.get(s);
		 if(ray.intersectPoint != null) {
		  detectCollision(ray,tile);
		  showRay(ray, ray.intersectPoint, c, buf);
		 }
		}
	}
	
	public static void detectCollision(JMarioRay ray, Vf2d tile) {
	  float distance = Vf2d.dist(ray.position, ray.intersectPoint);
	  if(distance < tile.x)
		  ray.collided = true;
	  else
		  ray.collided = false;
	}
	
	private static void showRay(Ray ray, Vf2d intersect, Color c, FaySansBuffer buf) {
		buf.translateVf2dFromTo(ray.position, intersect, c, 1.0f);
	}
	
	public static void lookAround(List<Ray> rays, List<Wall> walls, Console obj) {
		// cast each ray to each wall
		// and find the closest intersect point
		for(Ray ray: rays) {
		 ray.intersectPoint = null;
		 for(Wall wall: walls)
		  look(ray, wall, obj);
		}
	}
	
	public static void lookAround(Map<Integer, JMarioRay> rays, List<Wall> walls, float x, float y, Vf2d tile) {
		// cast each ray to each wall
		// and find the closest intersect point
		for(Integer s:rays.keySet()) {
		 JMarioRay ray = rays.get(s);
		 ray.intersectPoint = null;
		 for(Wall wall: walls)
			try {
				look(ray, wall, x, y, tile);
			} catch (FayPixelEngineException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	private static void look(Ray ray, Wall wall, Console obj) {
		ray.position.setPosition(obj.mouseXAdjust(), obj.mouseYAdjust());
		Vf2d intersect = cast(ray, wall);
		if(intersect != null) ray.setIntersect(intersect);
	}
	
	private static void look(Ray ray, Wall wall, float x, float y) {
		ray.position.setPosition(x, y);
		Vf2d intersect = cast(ray, wall);
		if(intersect != null) ray.setIntersect(intersect);
	}
	
	private static void look(JMarioRay ray, Wall wall, float x, float y, Vf2d tile) throws FayPixelEngineException {
		switch(ray.number) {
			case JMarioRay.A_TOP_LEFT: 			ray.position.setPosition(x, y); 							break;
			case JMarioRay.B_TOP: 				ray.position.setPosition(x+tile.x/2, y); 					break;
			case JMarioRay.C_TOP_RIGHT: 		ray.position.setPosition(x+tile.x, y); 						break;
			case JMarioRay.D_RIGHT: 			ray.position.setPosition(x+tile.x, y+tile.y/2); 			break;
			case JMarioRay.E_BOTTOM_RIGHT: 		ray.position.setPosition(x+tile.x, y+tile.y); 				break;
			case JMarioRay.F_BOTTOM: 			ray.position.setPosition(x+tile.x/2, y+tile.y); 			break;
			case JMarioRay.G_BOTTOM_LEFT: 		ray.position.setPosition(x, y+tile.y); 						break;
			case JMarioRay.H_LEFT: 				ray.position.setPosition(x, y+tile.y/2); 					break;
			
			default:
				throw new FayPixelEngineException("JMario ray id undefined: " + ray.number);
		}
		
		Vf2d intersect = cast(ray, wall);
		if(intersect != null) ray.setIntersect(intersect);
	}
	
	private static Vf2d cast(Ray ray, Wall thiswall) {
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
	
	public static List<Ray> rotate(float startx, float starty, float angleStart, float angleEnd, float angleInc) {	
		List<Ray> rays = new ArrayList<Ray>();
		for(float i = angleStart; i <= angleEnd; i += angleInc) {
			float angle = degToRad(i);
			float x = (float) Math.cos(angle);
			float y  = (float) Math.sin(angle);
			rays.add(createRay(startx, starty, x, y));
		}
		return rays;
	}
	
	// see handwritten notes Sept 5 2021 on page 5
	public static Map<Integer, JMarioRay> marioRays(float x, float y, Vf2d tile) {
		Map<Integer, JMarioRay> rays = new HashMap<Integer, JMarioRay>();
		
		// ray 'a'
		rays.put(JMarioRay.A_TOP_LEFT, createJMarioRay(JMarioRay.A_TOP_LEFT, x, y, (float)Math.cos(degToRad(JMarioRay.A_TOP_LEFT)), (float)Math.sin(degToRad(JMarioRay.A_TOP_LEFT))));					
		// ray 'b'
		rays.put(JMarioRay.B_TOP, createJMarioRay(JMarioRay.B_TOP, x+tile.x/2, y, (float)Math.cos(degToRad(JMarioRay.B_TOP)), (float)Math.sin(degToRad(JMarioRay.B_TOP))));			
		// ray 'c'
		rays.put(JMarioRay.C_TOP_RIGHT, createJMarioRay(JMarioRay.C_TOP_RIGHT, x+tile.x, y, (float)Math.cos(degToRad(JMarioRay.C_TOP_RIGHT)), (float)Math.sin(degToRad(JMarioRay.C_TOP_RIGHT))));			
		// ray 'd'
		rays.put(JMarioRay.D_RIGHT, createJMarioRay(JMarioRay.D_RIGHT, x+tile.x, y+tile.y/2, (float)Math.cos(degToRad(JMarioRay.D_RIGHT)), (float)Math.sin(degToRad(JMarioRay.D_RIGHT))));		
		// ray 'e'
		rays.put(JMarioRay.E_BOTTOM_RIGHT, createJMarioRay(JMarioRay.E_BOTTOM_RIGHT, x+tile.x, y+tile.y, (float)Math.cos(degToRad(JMarioRay.E_BOTTOM_RIGHT)), (float)Math.sin(degToRad(JMarioRay.E_BOTTOM_RIGHT))));		
		// ray 'f'
		rays.put(JMarioRay.F_BOTTOM, createJMarioRay(JMarioRay.F_BOTTOM, x+tile.x/2, y+tile.y, (float)Math.cos(degToRad(JMarioRay.F_BOTTOM)), (float)Math.sin(degToRad(JMarioRay.F_BOTTOM))));	
		// ray 'g'
		rays.put(JMarioRay.G_BOTTOM_LEFT, createJMarioRay(JMarioRay.G_BOTTOM_LEFT, x, y+tile.y, (float)Math.cos(degToRad(JMarioRay.G_BOTTOM_LEFT)), (float)Math.sin(degToRad(JMarioRay.G_BOTTOM_LEFT))));			
		// ray 'h'
		rays.put(JMarioRay.H_LEFT, createJMarioRay(JMarioRay.H_LEFT, x, y+tile.y/2, (float)Math.cos(degToRad(JMarioRay.H_LEFT)), (float)Math.sin(degToRad(JMarioRay.H_LEFT))));			
		
		return rays;
	}
	
	public static float degToRad(float a) {
		// google: 1 degrees to radians
		return (float) (a * Math.PI / 180);
	}

}
