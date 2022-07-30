import java.util.Arrays;

import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
	private final String text;
	private final int n;
	private Suffix[] suffixes;

	private class Suffix implements Comparable<Suffix> {
		private final int index;

		public Suffix(int i) {
			index = i;
		}

		public char charAt(int i) {
			return (index + i < n) ? text.charAt(index + i) : text.charAt(index + i - n);
		}

		public int compareTo(Suffix that) {
			if (this == that) return 0;
			for (int i = 0; i < n; i++) {
				if (this.charAt(i) < that.charAt(i)) return -1;
				if (this.charAt(i) > that.charAt(i)) return +1;
			}			
			return 0;
		}
		
		public String toString() {
			StringBuilder b = new StringBuilder(n);
			for (int i = 0; i < n; i++) {
				b.append(charAt(i));
			}			
			return b.toString();
		}
	}

	public CircularSuffixArray(String s) {		
		if (s == null) {
			throw new IllegalArgumentException("Arguement null");
		}
		
		text = s;
		n = text.length();
		suffixes = new Suffix[n];
		for (int i = 0; i < n; i++) {
			suffixes[i] = new Suffix(i);
		}
		Arrays.sort(suffixes);		
	}

	public int length() {
		return n;
	}

	public int index(int i) {
		if (i < 0 || i >= n) {
			throw new IllegalArgumentException("Arguement out of range");
		}
		
		return suffixes[i].index;
	}

	public static void main(String[] args) {
		// unit testing (required)
		
		String s = args[0];
		
		CircularSuffixArray suffix = new CircularSuffixArray(s);
		for (int i = 0; i < suffix.length(); i++) {
			StdOut.println(suffix.suffixes[i] + ":" + suffix.index(i));
		}
	}
}