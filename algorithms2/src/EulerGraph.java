import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.StdOut;

public class EulerGraph {

	private static final String NEWLINE = System.getProperty("line.separator");

	private final int V;
	private int E = 0;
	private Map<String, Map<String, Bag<String>>> adj = new HashMap<String, Map<String, Bag<String>>>();
	private Map<String, Integer> edgeCount = new HashMap<String, Integer>();

	public EulerGraph(In in) {
		try {
			this.V = in.readInt();
			if (V < 0)
				throw new IllegalArgumentException("number of vertices in a Graph must be nonnegative");

			for (int i = 0; i < V; i++) {				
				adj.put(""+i, new HashMap<String, Bag<String>>());				
			}
			
			int E = in.readInt();
			if (E < 0)
				throw new IllegalArgumentException("number of edges in a Graph must be nonnegative");
			for (int i = 0; i < E; i++) {
				int v = in.readInt();
				int w = in.readInt();
				addEdge(v, w);
			}
		} catch (NoSuchElementException e) {
			throw new IllegalArgumentException("invalid input format in Graph constructor", e);
		}
	}
	
	public void addEdge(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		
		E++;				
		String edge = constructEdge(v, w);		
		addEdge(v, w, edge);
		addEdge(w, v, edge);
	}
	
	private void addEdge(int v, int w, String edge) {
		Bag<String> edges = adj.get(""+v).get(""+w);
		
		if (edges == null) {
			edges = new Bag<String>();
			adj.get(""+v).put(""+w, edges);
		}
		edges.add(edge);		
	}

	private String constructEdge(int v, int w) {
		//get next number for edge
		
		String key = (v < w) ? ""+v+w : ""+w+v;				
		Integer count = edgeCount.get(key);
		
		count = (count == null) ? 0 : count.intValue() + 1;
		edgeCount.put(key, count);
		
		return key+"_"+count;		
	}

	private void validateVertex(int v) {
		if (v < 0 || v >= V)
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
	}

	public Bag<String> edges(int v) {
		validateVertex(v);
		
		Bag<String> edges = new Bag<String>();
		for (String w: adj.get(""+v).keySet()) {
			for (String edge : adj.get(""+v).get(w)) {
				edges.add(edge);
			}
		}		
		return edges;
	}
	
	public Bag<String> edges(String v, String w) {				
		return adj.get(v).get(w);
	}
	
	public Iterable<String> adj(String v) {			
		return adj.get(v).keySet();
	}

	public int degree(int v) {
		validateVertex(v);
		return edges(v).size();
	}

	public int V() {
		return V;
	}

	public int E() {
		return E;
	}
	
	public Iterable<String> allEdges() {
		
		Bag<String> result = new Bag<String>();
		
		for (int i = 0; i < V; i++) {
			for (String edge : edges(i)) {
				result.add(edge);
			}
		}
		
		return result;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(V + " vertices, " + E + " edges " + NEWLINE);
		for (int v = 0; v < V; v++) {
			s.append(v + ": ");
			for (String w : edges(v)) {
				s.append(w + " ");
			}
			s.append(NEWLINE);
		}
		return s.toString();
	}

	public static void main(String[] args) {
		In in = new In(args[0]);
		EulerGraph G = new EulerGraph(in);
		StdOut.println(G);
	}
}
