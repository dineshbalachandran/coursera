import java.util.Set;

public class MaliciousNode implements Node {

    public MaliciousNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
    	this.round = 0;    	
    }

    public void setFollowees(boolean[] followees) {
        return;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        txs = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
    	//System.out.println("M" + number + ":" + ++round + "\t" + txs.size());
        return txs;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        return;
    }
    
    private int round;    
    private Set<Transaction> txs;
}
