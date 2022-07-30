import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;



public class GraphDiameter {

	private static final int INFINITY = Integer.MAX_VALUE;
	private boolean[] marked;  // marked[v] = is there an s-v path
	private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
	private int[] distTo;      // distTo[v] = number of edges shortest s-v path

	int v;
	int center;

	public GraphDiameter(Graph G, int s) {
		marked = new boolean[G.V()];
		distTo = new int[G.V()];
		edgeTo = new int[G.V()];
		for (int v = 0; v < G.V(); v++)
			distTo[v] = INFINITY;

		bfs(G,s);
		int w = maxVertex(distTo);

		bfs(G, w);
		v = maxVertex(distTo);

	}
	
	public int maxV() {
		return v;
	}

	public Iterable<Integer> diameter() {        
		List<Integer> path = new ArrayList<Integer>();        
		int x;
		for (x = v; distTo[x] != 0; x = edgeTo[x])
			path.add(x);
		path.add(x);
		
		center = path.get(path.size()/2);
		
		return path;
	}

	private int maxVertex(int[] distTo) {		

		int index = 0;
		int max = distTo[0];

		for (int i = 1; i < distTo.length; i++) {
			if (distTo[i] > max)  {
				max = distTo[i];
				index = i;
			}	        
		}

		return index;

	}

	private void bfs(Graph G, int s) {
		Queue<Integer> q = new Queue<Integer>();
		for (int v = 0; v < G.V(); v++) {
			distTo[v] = INFINITY;
			marked[v] = false;
		}	
		
		distTo[s] = 0;
		marked[s] = true;
		q.enqueue(s);

		while (!q.isEmpty()) {
			int v = q.dequeue();
			for (int w : G.adj(v)) {
				if (!marked[w]) {
					edgeTo[w] = v;
					distTo[w] = distTo[v] + 1;
					marked[w] = true;
					q.enqueue(w);
				}
			}
		}
	}


	public static void main(String[] args) {
		In in = new In(args[0]);
		Graph G = new Graph(in);
		int s = Integer.parseInt(args[1]);
		GraphDiameter d = new GraphDiameter(G, s);        
		for (int x : d.diameter()) {
			StdOut.print("-" + x);
		}
		StdOut.println();
		StdOut.print("center " + d.center);		
		StdOut.println();

	}
}
