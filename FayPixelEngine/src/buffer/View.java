package buffer;

import java.awt.Color;
import java.util.List;
import fay.engine.FayBuffer;
import fay.math.P5j;
import shiffman.Ray;

@SuppressWarnings("serial")
public class View extends FayBuffer {
	
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

	@Override
	public boolean OnUserCreate() {
		return true;
	}

}
