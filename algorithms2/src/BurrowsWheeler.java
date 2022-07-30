import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;


public class BurrowsWheeler {
	
	private static char R = 256;
	
	public static void transform() {
		String s = BinaryStdIn.readString();
        CircularSuffixArray suffix = new CircularSuffixArray(s);
        
        StringBuilder bwt = new StringBuilder(suffix.length());
        int first = -1;
        int n = suffix.length();
        for (int i = 0; i < n; i++) {        	
        	int pick = suffix.index(i) - 1;
        	if (suffix.index(i) == 0) {
        		first = i;
        		pick = n - 1;
        	}
        	bwt.append(s.charAt(pick));
        }
        
        BinaryStdOut.write(first);
        BinaryStdOut.write(bwt.toString());
        
        BinaryStdOut.flush();
	}
	
	public static void inverseTransform() {
		
		int first = BinaryStdIn.readInt();		
		String t = BinaryStdIn.readString();
		
		char[] s = new char[t.length()];
		int[] count = new int[R+1];
		int[] next = new int[t.length()];
		
		// suffix sort on a single character
		for (int i = 0; i < t.length(); i++) {
			count[t.charAt(i) + 1]++;
		}
		
		for (int r = 0; r < R; r++) {
			count[r+1] += count[r];
		}
		
		for (int i = 0; i < t.length(); i++) {
			s[count[t.charAt(i)]] = t.charAt(i);
			next[count[t.charAt(i)]] = i;
			count[t.charAt(i)]++;
		}
		
		int n = first;
		int j = 0;
		while (j < t.length())
		{
			BinaryStdOut.write(s[n]);
			n = next[n];
			j++;
		}
		
		BinaryStdOut.flush();
	}
	
	// if args[0] is '-', apply Burrows-Wheeler transform
	// if args[0] is '+', apply Burrows-Wheeler inverse transform
	public static void main(String[] args) {
		if (args.length == 0) {
    		throw new IllegalArgumentException("Specify either - (transform) or + (inverse transform)");
    	}
		
		switch (args[0]) {
    	case "-": transform(); 			break;
    	case "+": inverseTransform();	break;
    	default :           			break;
    	}
	}
}