import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BiGraph {
	
	private boolean[] marked;   // marked[v] = has vertex v been marked?
    private int[] id;           // id[v] = id of connected component containing v
    private boolean[] group;      // size[id] = number of vertices in given component
    private int count;          // number of connected components
    private boolean bigraph = true;
	
	public BiGraph(Graph G, int s) {
		marked = new boolean[G.V()];
        id = new int[G.V()];
        group = new boolean[G.V()];
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
            	group[v] = true;
            	dfs(G, v);
                count++;
            }
        }
	}
	
	 private void dfs(Graph G, int v) {
		 marked[v] = true;
		 id[v] = count;
		 		 
	        for (int w : G.adj(v)) {
	            if (!marked[w]) {	            	
	            	group[w] = !group[v];
	                dfs(G, w);
	            } else {
	            	if (id[w] == id[v]) {
	            		if (group[w] == group[v])
	            			bigraph = false;
	            	}	            	
	            }
	        }
	    }


	public static void main(String[] args) {
		In in = new In(args[0]);
		Graph G = new Graph(in);
		int s = Integer.parseInt(args[1]);
		
		BiGraph b = new BiGraph(G, s);
		
		String answer = b.isBigraph()?"Yes":"No";
		StdOut.print("Is the graph bi partite? " + answer);		
		StdOut.println();

	}

	public boolean isBigraph() {
		return bigraph;
	}
}
