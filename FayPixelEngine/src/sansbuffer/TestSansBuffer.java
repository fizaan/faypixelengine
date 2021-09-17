package sansbuffer;

import java.awt.Color;

import fay.engine.FaySansBuffer;

@SuppressWarnings("serial")
public class TestSansBuffer extends FaySansBuffer {
	
	public static void main(String args[]) {
		new TestSansBuffer(400,400,1,1, "No buffer");
	}

	public TestSansBuffer(int len, int hei, int pX, int pY, String title) {
		super(len,hei,pX,pY,title);
	}

	@Override
	public boolean OnUserUpdate(float fElapsedTime) {
		clearConsole(Color.RED);
		return true;
	}

	@Override
	public boolean OnUserCreate() {
		return true;
	}

}
