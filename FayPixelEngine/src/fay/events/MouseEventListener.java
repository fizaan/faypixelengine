package fay.events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.Queue;

public class MouseEventListener implements MouseMotionListener, MouseListener {
	
	private Queue<Mouse> eventQueue;
	
	public MouseEventListener() {eventQueue = new LinkedList<Mouse>();}
	
	public Queue<Mouse> getEvents() { return eventQueue; };
	
	public static boolean mousePressed;

	@Override
	public void mouseDragged(MouseEvent e) {
		eventQueue.add(new Mouse(e.getX(), e.getY()));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		eventQueue.add(new Mouse(e.getX(), e.getY()));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
		eventQueue.add(new Mouse(e.getX(), e.getY()));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

}
