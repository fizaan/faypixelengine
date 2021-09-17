package fay.events;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import fay.exceptions.FayPixelEngineException;

public class EventListener extends KeyAdapter {
	
	private Queue<Integer> eventQueue;
	private Map<Integer, Integer> log;
	public static int keyPressedEvent;
	private long then;
	public static float duration;
	
	public EventListener() {
	 eventQueue = new LinkedList<Integer>();
	 log = new HashMap<Integer, Integer>();
	 keyPressedEvent = 0;
	 then = -1;
	 duration = -1;
	}
	
	public Queue<Integer> getEvents() { return eventQueue; };
	
	@Override
    public void keyPressed(KeyEvent event) {
	 eventQueue.add(event.getKeyCode());
	 log.put(event.getKeyCode(),event.getKeyCode());
	 if(then == -1) then = System.nanoTime();
	 try {determineKey();} catch(FayPixelEngineException fe) {
		 fe.printStackTrace(); System.exit(1);
	 }
	}
	
	@Override
    public void keyReleased(KeyEvent event) {
	 log.remove(event.getKeyCode());
     duration = (float) System.nanoTime() - then;
     duration /= 1E9;
     then = -1;
     try {determineKey();} catch(FayPixelEngineException fe) {
		 fe.printStackTrace(); System.exit(1);
	 }
	}
	
	private void determineKey() throws FayPixelEngineException {
	 if(log.size() > 4) 
		 throw new FayPixelEngineException("Too many key events: " + log.size());
	 keyPressedEvent = 0;
	 for(Integer key:log.keySet())
	  keyPressedEvent |= log.get(key);
	}

}
