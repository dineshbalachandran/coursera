import java.util.HashMap;
import java.util.Map;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.DirectedCycle;

public class WordNet {
	
	private Digraph graph;
	private SAP sap;
	
	private Map<String, Bag<Integer>> nouns = new HashMap<String, Bag<Integer>>();
	private Map<Integer, String> synsets = new HashMap<Integer, String>();
	
	private int V;	

	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {		
		
		extractNounsAndSynsets(synsets);
		createDigraph(hypernyms);
		
		validateSingleRoot();		
		validateNoCycles();
		
		 sap = new SAP(graph);
	}		

	// returns all WordNet nouns
	public Iterable<String> nouns() {
		return nouns.keySet();
	}

	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		if (word == null) {
			throw new IllegalArgumentException("Noun should not be null." );
		}
		return nouns.containsKey(word);
	}

	// distance between nounA and nounB (defined below)
	public int distance(String nounA, String nounB) {
		validateNoun(nounA);
		validateNoun(nounB);
		
		int l = sap.length(nouns.get(nounA), nouns.get(nounB));
		
		return l;
	}	

	// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	// in a shortest ancestral path (defined below)
	public String sap(String nounA, String nounB) {		
		validateNoun(nounA);
		validateNoun(nounB);
				
		int a = sap.ancestor(nouns.get(nounA), nouns.get(nounB));
		
		String ancestor = synsets.get(a);
		
		return ancestor;
	}

	public static void main(String[] args) {		
		WordNet wn = new WordNet(args[0], args[1]);
		
		for (String word : wn.nouns()) {
			StdOut.println(word);
		}
	}
	
	private void extractNounsAndSynsets(String synsets) {
		
		validateSynsets(synsets);
		
		In in = new In(synsets);
		String line = in.readLine();
		int v = -1;
		while (line != null && !line.isEmpty()) {
			String[] tokens = line.split(",");
			v = Integer.parseInt(tokens[0]);			
			String[] nns = tokens[1].split(" +");
			for (String noun : nns) {
				getNoun(noun).add(v);				
			}
			this.synsets.put(v, tokens[1]);
			line = in.readLine();
		}
		
		V = v + 1;
	}

	private void validateSynsets(String synsets) {
		if (synsets == null)  {
			throw new IllegalArgumentException("Synset file name is null.");
		}
	}
	

	private void createDigraph(String hypernyms) {
		
		validateHyperNym(hypernyms);
		
		graph = new Digraph(V);
		
		In in = new In(hypernyms);		
		String line = in.readLine();
		while (line != null && !line.isEmpty()) {
			String[] tokens = line.split(",");
			int v = Integer.parseInt(tokens[0]);
			
			for (int i = 1; i < tokens.length; i++) {
				int w = Integer.parseInt(tokens[i]);
				graph.addEdge(v, w);
			}
			
			line = in.readLine();
		}
	}

	private void validateHyperNym(String hypernyms) {
		if (hypernyms == null)  {
			throw new IllegalArgumentException("HyperNym file name is null.");
		}
	}

	private Bag<Integer> getNoun(String noun) {		
		Bag<Integer> vertices = nouns.get(noun);
		if (vertices == null) {
			vertices = new Bag<Integer>();
			nouns.put(noun, vertices);
		}		
		return vertices;		
	}
	
	private int findRoot() {
		int n = 0;
		int root = -1;
		for (int i = 0; i < V; i++) {
			if (graph.outdegree(i) == 0) { //a bottom to top root
				n++;
				root = i;
			}
		}
		if (n == 1) { //single rooted
			return root;
		} else {			
			return -1;
		}
	}
	
	private void validateNoun(String noun) {
		
		if (!isNoun(noun)) {
			throw new IllegalArgumentException(noun + " is not a WordNet noun.");
		}
		
	}
	
	private void validateNoCycles() {
		DirectedCycle dcycle = new DirectedCycle(graph);
		if (dcycle.hasCycle()) {
			throw new IllegalArgumentException("Graph has cycles.");
		}
	}

	private void validateSingleRoot() {
		if (findRoot() == -1) {
			throw new IllegalArgumentException("Graph is not a single root graph.");
		}
	}
}