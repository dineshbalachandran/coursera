import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

	private Digraph G;	

	private int[] vlengthTo;
	private int[] wlengthTo;
	
	private int ancestor;
	private int sapLength;

	public SAP(Digraph G) {

		if (G == null) {
			throw new IllegalArgumentException("Digraph object is null.");
		}

		this.G = new Digraph(G);		
	}

	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {

		List<Integer> vl = new ArrayList<Integer>();
		List<Integer> wl = new ArrayList<Integer>();
		vl.add(v);
		wl.add(w);

		return length(vl, wl); 

	}
	// a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
	public int ancestor(int v, int w) {

		List<Integer> vl = new ArrayList<Integer>();
		List<Integer> wl = new ArrayList<Integer>();

		vl.add(v);
		wl.add(w);

		return ancestor(vl, wl);

	}
	// length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w) {

		return ancestor(v, w) == -1 ? -1 : sapLength; 
		
	}
	// a common ancestor that participates in shortest ancestral path; -1 if no such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		
		validate(v);
		validate(w);
		
		Queue<Integer> vq = new Queue<Integer>();
		Queue<Integer> wq = new Queue<Integer>();

		boolean[] vmarked = new boolean[G.V()];
		boolean[] wmarked = new boolean[G.V()];

		vlengthTo = new int[G.V()];
		wlengthTo = new int[G.V()];

		initialize(v, vq, vmarked, vlengthTo);
		initialize(w, wq, wmarked, wlengthTo);

		ancestor = -1;		
		sapLength = Integer.MAX_VALUE;
		
		while (!vq.isEmpty() || !wq.isEmpty()) {
			
			findAncestor(vq, vmarked, vlengthTo, wmarked);			
			findAncestor(wq, wmarked, wlengthTo, vmarked);						
		}
		
		return ancestor;
	}

	private void validate(Iterable<Integer> v) {
		
		if (v == null) {
			throw new IllegalArgumentException("Iterable should be non null.");
		}
		
		for (Integer x : v) {
			if (x < 0 || x >= G.V()) {
				throw new IllegalArgumentException("Out of bound vertex: " + x);
			}
		}		
	}

	private int ancestralLength(int a) {
		return vlengthTo[a] + wlengthTo[a];
	}

	private void initialize(Iterable<Integer> v, Queue<Integer> q, 
			boolean[] marked, int[] lengthTo) {
		for (int x : v) {
			if (!marked[x]) {				
				marked[x] = true;
				lengthTo[x] = 0;
				q.enqueue(x);
			}
		}
	}

	private void findAncestor(Queue<Integer> q, boolean[] marked, int[] lengthTo, 
			boolean[] othermarked) {		

		if (q.isEmpty())
			return;
		
		int x = q.dequeue();		
		if (othermarked[x]) { //common ancestor found				
			if (ancestralLength(x) < sapLength) {
				ancestor = x;
				sapLength = ancestralLength(x);
			}
		}
		
		if (lengthTo[x] >= sapLength)
			return;
		
		for (int y : G.adj(x)) {
			if (!marked[y]) {
				marked[y] = true;
				lengthTo[y] = lengthTo[x] + 1;						
				q.enqueue(y);
			}
		}		
	}

	public static void main(String[] args) {
		
		In in = new In(args[0]);
		Digraph G = new Digraph(in);
		SAP sap = new SAP(G);
		
		while (!StdIn.isEmpty()) {
			int v = StdIn.readInt();
			int w = StdIn.readInt();
			int length = sap.length(v, w);
			int ancestor = sap.ancestor(v, w);
			StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
		}
	}
}