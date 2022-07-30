import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {       
    	this.numRounds = numRounds;
        this.round = 0;
        this.m = new HashMap<Transaction, Integer>();
        this.followees = new ArrayList<Integer>();
    }

    public void setFollowees(boolean[] followees) {        
    	for (int i = 0; i < followees.length; i++)
    		if (followees[i]) this.followees.add(i);
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {    	
    	for (Transaction tx : pendingTransactions) m.put(tx, 1);    		   	
    }

    public Set<Transaction> sendToFollowers() {
    	Set<Transaction> txs;
    	if (++round > (numRounds-1)) {
    		txs = new HashSet<Transaction>();
    		for (Map.Entry<Transaction, Integer> e : m.entrySet()) {
    			if (e.getValue() > followees.size()*1) txs.add(e.getKey());
    		}
    	} else {
    		txs = m.keySet();
    	}
    	return txs;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {    		    	
    	for (Candidate c : candidates) {
    		if (!m.containsKey(c.tx)) m.put(c.tx, 0);
    		m.put(c.tx, m.get(c.tx) + 1);
    	}
    }

    private int numRounds;
    private List<Integer> followees;    
    private int round;
    private Map<Transaction, Integer> m;    
}
