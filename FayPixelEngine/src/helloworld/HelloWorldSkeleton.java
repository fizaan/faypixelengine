package helloworld;

import java.awt.Color;

import fay.original.FaySansBuffer;

@SuppressWarnings("serial")
public class HelloWorldSkeleton extends FaySansBuffer {
	
	public HelloWorldSkeleton(int len, int hei, int pX, int pY, String title) {
		 
	 super(len,hei,pX,pY,title);
	
	}
	
	public static void main(String args[]) {
		
	 new HelloWorldSkeleton(400,400,1,1, "Bare bones");
	 
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		
		clearConsole(Color.BLUE);
		
		return true;
	}

	@Override
	public boolean OnUserCreate() {
		return true;
	}
	
	

}
