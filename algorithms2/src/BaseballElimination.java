import java.util.HashMap;
import java.util.Map;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {

	private int n;
	private int[] w;
	private int[] l;
	private int[] r;
	private int[][] g;
	
	private Map<String, Integer> teams;
	
	private Bag<String> eliminationCertificate;

	// create a baseball division from given filename in format specified below
	public BaseballElimination(String filename) {
		if (filename == null) {
			throw new IllegalArgumentException("Filename should not be null.");
		}

		In in = new In(filename);
		n = in.readInt();
		teams = new HashMap<String, Integer>(n);
		
		w = new int[n];
		l = new int[n];
		r = new int[n];		
		g = new int[n][n];
		
		for (int i = 0; i < n; i++) {
			String team = in.readString();
			teams.put(team, i);

			w[i] = in.readInt();
			l[i] = in.readInt();
			r[i] = in.readInt();

			for (int j = 0; j < n; j++) {
				g[i][j] = in.readInt();
			}
		}		
	}

	// number of teams
	public int numberOfTeams() {
		return n;

	}

	// all teams
	public Iterable<String> teams() {
		return teams.keySet();
	}

	// number of wins for given team
	public int wins(String team) {
		validateTeam(team);		
		return w[teams.get(team)];		
	}

	// number of losses for given team
	public int losses(String team) {
		validateTeam(team);		
		return l[teams.get(team)];
	}

	// number of remaining games for given team
	public int remaining(String team) {
		validateTeam(team);		
		return r[teams.get(team)];
	}

	// number of remaining games between team1 and team2
	public int against(String team1, String team2) {
		validateTeam(team1);
		validateTeam(team2);

		return g[teams.get(team1)][teams.get(team2)];
	}

	// is given team eliminated?
	public boolean isEliminated(String team) {		
		validateTeam(team);		
		
		eliminationCertificate = null;
		
		if (trivialElimination(team))			
			return true;		
				
		Map<Integer, String> teamvs = setupteamvs(team);
		FlowNetwork nw = setupFlowNetwork(team, teamvs);
		
		int s = 0, t = nw.V() - 1;
		FordFulkerson maxflow = new FordFulkerson(nw, s, t);
		
		for (FlowEdge e : nw.adj(0)) {
			if (e.flow() != e.capacity()) {
				setupCertificateofElimination(maxflow, teamvs);
				return true;
			}
		}
		
		return false;
	}
	
	// subset R of teams that eliminates given team; null if not eliminated
	public Iterable<String> certificateOfElimination(String team) {
		isEliminated(team);		
		return eliminationCertificate;
	}

	private void setupCertificateofElimination(FordFulkerson maxflow, Map<Integer, String> teamvs) {
		
		eliminationCertificate = new Bag<String>();
		for (int i = 1; i < n; i++) {
			if (maxflow.inCut(i)) {
				eliminationCertificate.add(teamvs.get(i));
			}
		}		
	}

	private boolean trivialElimination(String team) {
		
		int x = teams.get(team);		
		int possiblewins = w[x] + r[x];
		
		for (String tm : teams.keySet()) {			
			int i = teams.get(tm);
			if (possiblewins < w[i]) {				
				eliminationCertificate = new Bag<String>();
				eliminationCertificate.add(tm);
				
				return true;
			}
		}
		
		return false;
	}

	
	private void validateTeam(String team) {
		if (!teams.containsKey(team)) {
			throw new IllegalArgumentException("Team " + team + " not found.");
		}
	}
	

	private FlowNetwork setupFlowNetwork(String team, Map<Integer, String> teamvs) {
		
		//(n-1)(n-2) team combinations + (n-1) teams + 2 source and target vertices each
		int vertices = (n-1)*(n-2)+(n-1)+2;
		FlowNetwork nw = new FlowNetwork(vertices);
		
		int i = n;		
		int s = 0; //source vertex index
		int t = vertices - 1; //target vertex index
		
		int x = teams.get(team);
		for (int j = 1; j < n; j++) {			
			//edge from team to target
			int wi = teams.get(teamvs.get(j));
			int cap = w[x] + r[x] - w[wi];		
			nw.addEdge(new FlowEdge(j, t, cap));
			
			for (int k = j + 1; k < n; k++) {				
				//edge from team-game (v) to teams (k and j)
				int v = i++;
				nw.addEdge(new FlowEdge(v, k, Double.POSITIVE_INFINITY));
				nw.addEdge(new FlowEdge(v, j, Double.POSITIVE_INFINITY));
				
				//edge from source to team-game
				String team1 = teamvs.get(j);
				String team2 = teamvs.get(k);				
				nw.addEdge(new FlowEdge(s, v, g[teams.get(team1)][teams.get(team2)]));				
			}
		}
		
		return nw;
	}

	private Map<Integer, String> setupteamvs(String team) {
		int i = 1;		
		Map<Integer, String> teamvs = new HashMap<Integer, String>(n-1);
		for (String tm : teams.keySet()) {
			if (tm.equals(team)) 
				continue;
			teamvs.put(i++, tm);			
		}
		return teamvs;
	}
	
	public static void main(String[] args) {
		BaseballElimination division = new BaseballElimination(args[0]);
		for (String team : division.teams()) {
			if (division.isEliminated(team)) {
				StdOut.print(team + " is eliminated by the subset R = { ");
				for (String t : division.certificateOfElimination(team)) {
					StdOut.print(t + " ");
				}
				StdOut.println("}");
			}
			else {
				StdOut.println(team + " is not eliminated");
			}
		}
	}
}

