package fay.math;

public final class P5j {
	
	public static float constrain(float n, float low, float high) {
		return Math.max(Math.min(n, high), low);
	}
	
	// https://p5js.org/reference/#/p5/map
	// start1 : lower bound of the value's current range
	// stop1  : upper bound of the value's current range
	// start2 : lower bound of the value's target range
	// stop2  : upper bound of the value's target range
	public static float map(float n, float start1, float stop1, float start2, float stop2, boolean withinBounds) {
		float newval = (n - start1) / (stop1 - start1) * (stop2 - start2) + start2;
		
		if (!withinBounds)
		    return newval;
		
		if (start2 < stop2)
		    return constrain(newval, start2, stop2);
		else
		    return constrain(newval, stop2, start2);
	}

}
