import java.util.Map;
import java.util.HashMap;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class EulerCycle {
	
	private int count = 0;
	private Map<String, Boolean> marked;
	private Stack<String> edgeTo;

	public EulerCycle(EulerGraph G, int s) {
		
		edgeTo = new Stack<String>();
		marked = new HashMap<String, Boolean>();
		for (String edge : G.allEdges()) {
			marked.put(edge, false);
		}

		edgeTo.push(""+s);
		dfs(G, ""+s);
	}


	public static void main(String[] args) {
		
		In in = new In(args[0]);
		EulerGraph G = new EulerGraph(in);
		int s = Integer.parseInt(args[1]);
		EulerCycle ep = new EulerCycle(G, s);
		
		if (ep.hasEulerPath(G)) {	    		 
			for (String x : ep.eulerPath()) {
				StdOut.print("-" + x);
			}
			StdOut.println();
		}
		else {
			StdOut.printf("no euler cycle possible\n");
		}
	}	

	private boolean hasEulerPath(EulerGraph G) {
		
		boolean eulerPath = true;
		
		for (int i = 0; i < G.V(); i++) {
			if (G.degree(i) % 2 != 0)  {
				StdOut.printf("%d \n", i);
				eulerPath = false;
				break;
			}
		}
		return eulerPath;
	}

	private Iterable<String> eulerPath() {		
		return edgeTo;
	}

	// depth first search from v
	private void dfs(EulerGraph G, String v) {
		
		for (String w : G.adj(v)) {
			for (String edge : G.edges(v, w)) {
				if (!marked.get(edge).booleanValue()) {				
					
					marked.put(edge, true);
					edgeTo.push(w);
					count++;
					dfs(G, w);
					
					if (count != G.E()) {
						edgeTo.pop();
						count--;
						marked.put(edge, false);
					}
				}
			}
		}
	}

}
