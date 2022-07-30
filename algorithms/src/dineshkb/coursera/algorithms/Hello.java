package dineshkb.coursera.algorithms;

import java.util.ArrayList;
import java.util.List;

public class Hello {
	
	public static void main (String[] args){
		System.out.println("Hello World");
		List<String> s = new ArrayList<String>();
		
		for (String st : s) {
			System.out.println("in loop" + st);
		}
		
		System.out.println(s);
	}
	
}
