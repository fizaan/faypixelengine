package fay.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import fay.math.Vf2d;
import shiffman.Ray;
import shiffman.Wall;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
 
/**
 * @author Crunchify.com
 * crunchify.com/how-to-read-json-object-from-file-in-java/
 * How to Read JSON Object From File in Java?
 */
 
public class FayUtils {
	
	private static Object obj;
	
	public static void loadJson(String jsonFile) {
		JSONParser parser = new JSONParser();
		try {
			obj = parser.parse(new FileReader(jsonFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Iterator<JSONObject> getIterator(String element) {
		// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
		JSONObject jsonObject = (JSONObject) obj;

		// A JSON array. JSONObject supports java.util.List interface.
		JSONArray wantedElement = (JSONArray) jsonObject.get(element);

		// An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
		// Iterators differ from enumerations in two ways:
		// 1. Iterators allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
		// 2. Method names have been improved.
		return wantedElement.iterator();
	}
	
	@SuppressWarnings("unchecked")
	public static Iterator<JSONObject> getIterator(JSONObject jsonObject, String element) {
		JSONArray wantedElement = (JSONArray) jsonObject.get(element);
		return wantedElement.iterator();
	}
	
	public static void sleep(int s) {
		Thread.currentThread();
		try {
			Thread.sleep(s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void waitInput(BufferedReader reader) {
		try {
			reader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Vf2d cast(Ray ray, Wall thiswall) {
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
	
	// Example usage: 
	// mario = new Ray(FayUtil.getabs(64,block.x),FayUtil.getabs(64,block.y),
	// 1.0f,StaticHelper.degToRad(45));
	// 
	// note that block is a tile block size e.g 16 or 8 pixels.
	public static int abs(float pos, float tilesize) {
		return (int) (pos / tilesize);
	}
}
