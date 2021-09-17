package sansbuffer;

import java.awt.Color;
import java.awt.Polygon;
import java.util.List;

import fay.engine.FaySansBuffer;
import fay.math.P5j;
import fay.math.Vf2d;
import shiffman.Ray;

@SuppressWarnings("serial")
public class View extends FaySansBuffer {
	
	private List<Ray> rays;
	private int birdEyeW, birdEyeH;
	public boolean fishEYEOff, usingPositionHeading;
	
	public View(int len, int hei, int pX, int pY, String title) {
		super(len,hei,pX,pY,title);
	}
	
	public void setRays(List<Ray> rays) { this.rays = rays; }
	public void setBirdEyeWH(int w, int h) { birdEyeW = w; birdEyeH = h; }

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
	 clearConsole(Color.BLACK);
	 drawEachRayAsLine();	
	 //drawPolygon();
	 return true;
	}
	
	private void drawEachRayAsLine() {
	 int x = 0;
	 for(Ray ray: rays) {
		 if(ray.intersectPoint != null) {
			 
			 float dist = ray.euDis();
			 
			 if(fishEYEOff)
				 if(usingPositionHeading) {
				 	float angle = ray.position.heading() - ray.intersectPoint.heading();
				 	dist *= Math.cos(angle);
		 		} else {
		 			float angle = ray.direction.heading() - ray.intersectPoint.heading();
				 	dist *= Math.cos(angle);
		 		}
			 
			 // note: Shiffman's sceneW and sceneH are 400,400 and he uses
			 // them in his mapping. But I use the Views' height
			 
			 // as the distance between the player's eye and the wall decreases
			 // the brightness of the wall color INCREASES hence why you have
			 // 1.0f, 0 and not 0, 1.0f.
			 float redShade = P5j.map(dist, 0, birdEyeW, 1.0f, 0, true);
			 
			 // as the distance between the player's eye and the wall decreases
			 // the distance between the top of the wall and ceiling also decreases
			 // and so does the distance between the foot of the wall and the floor
			 float ceil = P5j.map(dist, 0, birdEyeW, 0, getConsoleHeight(), true);
			 ceil *= 0.5f;
			 
			 drawLine(x, (int) ceil, x, (int) (getConsoleHeight() - ceil), new Color(redShade, 0, 0));
		 }
		 x++;
	 }
	}
	
	private void drawPolygon() {
	 float x1 = 0,y1 = 0,x2 = 0,y2 = 0,x3 = 0,y3 = 0,x4 = 0,y4 = 0;
	 float ceil = 0;
	 float firstCeil = 0;
	 int x = 0;
	 int i = 0;
	 int firstXCoord = 0;
	 Vf2d firstIntersectPoint = null;
	 
	 for(Ray ray: rays) {
		 
		 if(ray.intersectPoint != null) {
			 
			 float dist = ray.euDis();
			 
			 if(fishEYEOff) {
				 float angle = ray.direction.heading() - ray.intersectPoint.heading();
				 dist *= Math.cos(angle);
			 }
			 
			 ceil = P5j.map(dist, 0, birdEyeW, 0, getConsoleHeight(), true);
			 
			 if(firstIntersectPoint == null) {
				 firstXCoord = i;
				 firstCeil = ceil;
				 firstIntersectPoint = ray.intersectPoint;
			 }
			 
			 x++;
		 }
		 
		 i++;
	 }
			 
	x1 = firstXCoord;
	y1 = firstCeil;
	x2 = firstXCoord;
	y2 = getConsoleHeight() - firstCeil;
	
	x3 = x;
	y3 = ceil;
	x4 = x;
	y4 = getConsoleHeight() - ceil;
			
	 
	// Polygon:
	// x1,y1 to x2,y2 to x4,y4 to x3,y3
	// xs = {x1, x2, x4, x3}
	// ys = {y1, y2, y4, y3}
	int xs[] = new int[] {(int) x1,(int) x2,(int) x4,(int) x3};
	int ys[] = new int[] {(int) y1,(int) y2,(int) y4,(int) y3};
	Polygon p = new Polygon(xs, ys, xs.length); 
	fillPolygon(p, new Color(255, 255, 255));
	
	}

	@Override
	public boolean OnUserCreate() {
		return true;
	}

}
