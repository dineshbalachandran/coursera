import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class DAGReachableVertex {	


	private List<Set<Integer>> paths;
	private boolean hasReachableVertex = false;
	private int reachableVertex;
	private int vertices;
	

	public DAGReachableVertex(Digraph G, int s) {		

		paths = new ArrayList<Set<Integer>>(G.V());
		for (int i = 0; i < G.V(); i++) {
			paths.add(new TreeSet<Integer>());
		}
		vertices = G.V();
		bfs(G, s);
	}

	private void bfs(Digraph G, int s) {
		Queue<Integer> q = new Queue<Integer>();
		
		q.enqueue(s);

		while (!q.isEmpty()) {			
			int v = q.dequeue();
			
			reachableVertex(v);
			if (hasReachableVertex())
				break;

			for (int w : G.adj(v)) {
				paths.get(w).addAll(paths.get(v));
				paths.get(w).add(v);
				
				q.enqueue(w);
			}
		}
	}


	private void reachableVertex(int v) {		
		if (paths.get(v).size() == vertices -  1) {
			hasReachableVertex = true;
			reachableVertex = v;
		}
	}

	public static void main(String[] args) {
		In in = new In(args[0]);
		Digraph G = new Digraph(in);
		int s = Integer.parseInt(args[1]);
		DAGReachableVertex ep = new DAGReachableVertex(G, s);

		if (ep.hasReachableVertex()) {	    		 
				StdOut.println("-" + ep.reachableVertex());
		}
		else {
			StdOut.println("no reachable vertex");
		}
	}


	public int reachableVertex() {		
		return reachableVertex;
	}


	public boolean hasReachableVertex() {		
		return hasReachableVertex;
	}

}
