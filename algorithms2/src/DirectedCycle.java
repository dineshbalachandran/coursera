import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class DirectedCycle {	

	private List<List<Integer>> paths = new ArrayList<List<Integer>>();
	private List<Integer> shortestCycle;
	private List<Map<Integer, Boolean>> marked;

	private class Node implements Comparable<Node>{

		private int v;
		private List<Integer> path;

		public Node(int v, List<Integer> path) {
			this.v = v;
			this.path = path;
		}

		@Override
		public int compareTo(Node arg0) {			
			return Integer.compare(v, arg0.v);
		}
	}

	public DirectedCycle(DAGraph G) {		

		marked = new ArrayList<Map<Integer, Boolean>>(G.V());		
		for (int i = 0; i < G.V(); i++) {			
			marked.add(new HashMap<Integer, Boolean>(G.outdegree(i)));
		}
		
		for (int i = 0; i < G.V(); i++) {			
			bfs(G, i);
		}
		
		int l = Integer.MAX_VALUE;
		for (List<Integer> path : paths) {
			if (path.size() < l) {
				l = path.size();
				shortestCycle = path;
			}
		}
	}

	private void bfs(DAGraph G, int s) {
		Queue<Node> q = new Queue<Node>();		

		List<Integer> path = new ArrayList<Integer>();
		path.add(s);
		q.enqueue(new Node(s, path));		

		while (!q.isEmpty()) {

			Node n = q.dequeue();			

			for (int w : G.adj(n.v)) {				
				boolean unmarked = (marked.get(n.v).get(w) == null) ? true : !marked.get(n.v).get(w);
				if (unmarked) {
					marked.get(n.v).put(w, true);
					constructCycle(w, n.path);				
					path = new ArrayList<Integer>(n.path);
					path.add(w);									
					q.enqueue(new Node(w, path));
				}
				
			}
		}
	}


	private void constructCycle(int w, List<Integer> path) {		
		int i = path.indexOf(w); //if present in path		
		if (i >= 0) {
			List<Integer> cycle = new ArrayList<Integer>();
			cycle.addAll(path.subList(i, path.size()));
			cycle.add(w);			
			paths.add(cycle);
		}
	}

	public static void main(String[] args) {
		In in = new In(args[0]);
		int V = Integer.parseInt(args[1]); // number of vertices
		DAGraph G = new DAGraph(in, V);
		
		DirectedCycle ep = new DirectedCycle(G);

		if (ep.hasCycle()) {	    		 
			for (Integer x : ep.cycle()) {
				StdOut.print("-" + x);
			}
			StdOut.println();
		}
		else {
			StdOut.println("no Directed cycle");
		}
	}


	public Iterable<Integer> cycle() {		
		return shortestCycle;
	}


	public boolean hasCycle() {		
		return !(shortestCycle == null);
	}

}
