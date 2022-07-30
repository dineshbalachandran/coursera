import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class MaxFeeTxHandler {
    
	private class Node implements Comparable<Node>{		
		public Node(TransactionFee tx, double currentFee, double potentialFee, 
				List<UTXO> potentialTxs, Node prev) {
			this.tx = tx;
			this.currentFee = currentFee;
			this.potentialFee = potentialFee;
			this.potentialTxs = potentialTxs;
			this.prev = prev;
		}		
		
		public TransactionFee tx;
		public double currentFee;
		public double potentialFee;
		public List<UTXO> potentialTxs;
		public Node prev;
		
		public int compareTo(Node x) {			
			// reverse compare to use in Java's min priority queue
			if (currentFee + potentialFee > x.currentFee + x.potentialFee)
				return -1;
			else if(currentFee + potentialFee < x.currentFee + x.potentialFee)
				return 1;			
			return 0; 
		}
	}
	
	private class TransactionFee {		
		public TransactionFee(Transaction tx, double fee) {
			this.tx = tx;
			this.fee = fee;
		}		
		public Transaction tx;
		public double fee;
	}	
	
	/**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public MaxFeeTxHandler(UTXOPool utxoPool) {    	
    	this.utxoPool = new UTXOPool(utxoPool);
    	this.validationPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {    	
    	if (tx==null)
    		return false;
    	
		int i = 0;
		double sumOut = 0.0;
		for (Transaction.Output out : tx.getOutputs()) {			
			if (out.value < 0.0) //fails (4)
				return false;			
			sumOut += out.value;
			i++;    			
		}    		
		
		i = 0;
		double sumIn = 0.0;
		Set<UTXO> s = new HashSet<UTXO>();
		for (Transaction.Input in : tx.getInputs()) {			
			UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
			Transaction.Output out = validationPool.getTxOutput(utxo);
			
			if (out==null) //fails (1)
				return false;
			
			if (s.contains(utxo))
				return false; //fails (3)
			else
				s.add(utxo);
			
			if (!Crypto.verifySignature(out.address, 
					tx.getRawDataToSign(i), in.signature)) //fails (2)
				return false;
			
			sumIn += out.value;
			i++;   			
		}		
		if (sumIn < sumOut)
			return false; //fails (5)	
		
    	return true; //passes all tests    	
    }
    
    /** ALGORITHM
     * Considered the possibility of transactions being out of order, there is logic to make multiple 
     * passes on the transaction list till such time that a pass brings no change to the number of 
     * valid transactions.
     * A branch and bound implementation using best first search to find the maximum fee set.
     * The transactions are sorted by descending order of fees, so that higher fee transactions are 
     * considered ahead by best first search.
     * During search, if a transaction is 'removed' from consideration, all "dependent" transactions 
     * if any are also removed recursively. "Dependent" transactions are those in which inputs consume 
     * outputs from the 'removed' transaction.
     * If a transaction is 'accepted' during search. All other transactions that are mutually exclusive 
     * (i.e. refer to UTXOs used by the accepted transaction) are 'removed' from consideration.
     * The search proceeds by picking the best transaction (utilizing a priority queue) at any given 
     * point based on the currently accumulated fees of all 'accepted' transactions + potential fee 
     * that can be realized with the remaining transactions.
     * The search terminates when there are no more transactions to consider. At which point, the set 
     * of 'accepted' transactions form the maximum set.
     */

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {    	
    	if (possibleTxs==null)
    		return null;   	
    	
    	/**
    	 * house keeping map data structures
    	 * txMap - looking up a list of UTXO that are referred to by a transaction 'tx'
    	 *         since Transaction does not implement hashCode, UTXO is used as a proxy for 'tx'
    	 * utxoMap - for looking up UTXO to the list of transactions that use up the 
    	 *         - UTXO. The transactions are proxied by using UTXO.  
    	 * validTXs - for looking up transaction to its fee 
    	*/    	
    	Map<UTXO, List<UTXO>> txMap = new HashMap<UTXO, List<UTXO>>();
    	Map<UTXO, List<UTXO>> utxoMap = new HashMap<UTXO, List<UTXO>>();
    	Map<UTXO, TransactionFee> validTXs = new HashMap<UTXO, TransactionFee>();    	
    	    	
    	double maxFees = validateTxsAndGetMaxFees(txMap, utxoMap, validTXs, possibleTxs);
    	
    	Node root = new Node(null, 0.0, maxFees, new ArrayList<UTXO>(validTXs.keySet()), null);
    	Transaction[] txsArray = getMaximumFeeTXsAndUpdatePool(txMap, utxoMap, 
    									validTXs, root).toArray(new Transaction[0]);    	
    	
    	return txsArray;
    }
    
    private double validateTxsAndGetMaxFees(Map<UTXO, List<UTXO>> txMap, Map<UTXO, 
    		List<UTXO>> utxoMap, Map<UTXO, TransactionFee> validTXs, 
    		Transaction[] txs) {    	
    	
    	List<Transaction> possibleTxs = Arrays.asList(txs);
    	double potentialfees = 0.0;
    	
    	int prev_pass_size = 0;
    	int current_pass_size = possibleTxs.size();
    	while (current_pass_size != prev_pass_size) {    		
    		List<Transaction> possibleInvalidTxs = new ArrayList<Transaction>();
    		prev_pass_size = possibleTxs.size();    		
    		for (Transaction tx : possibleTxs) {
	    		if (!isValidTx(tx)) {
	    			possibleInvalidTxs.add(tx);
	    			continue;
	    		}    		
	    		UTXO txUTXO = new UTXO(tx.getHash(), 0);	//proxy for transaction, tx
	    		if (validTXs.containsKey(txUTXO)) {			//if tx is in the map, 
	    			continue;								//then this is a duplicate, skip
	    		}    		
	    		addValidationPool(tx);    		
	    		potentialfees += addToMapsAndGetFee(txMap, utxoMap, validTXs, txUTXO, tx); 
	    	}    		
	    	possibleTxs = possibleInvalidTxs;
	    	current_pass_size = possibleTxs.size();
       	}    	
    	return potentialfees;    	
    }
    
    private double addToMapsAndGetFee(Map<UTXO, List<UTXO>> txMap, Map<UTXO, List<UTXO>> utxoMap, 
    		Map<UTXO, TransactionFee> validTXs, UTXO txUTXO, Transaction tx) {   	
    	
    	List<UTXO> txUTXOs = txMap.get(txUTXO);    	
    	if (txUTXOs==null) {
    		txUTXOs = new ArrayList<UTXO>();
    		txMap.put(txUTXO, txUTXOs);
    	}
    	
    	double sumIn = 0.0;
    	for (Transaction.Input in: tx.getInputs()) {    			
			UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
			
			txUTXOs.add(utxo);
			List<UTXO> utxoTXs = utxoMap.get(utxo);
	    	if (utxoTXs==null) {
	    		utxoTXs = new ArrayList<UTXO>();
	    		utxoMap.put(utxo, utxoTXs);
	    	}
	    	utxoTXs.add(txUTXO);
	    	
	    	sumIn += validationPool.getTxOutput(utxo).value;
    	}    	
    	
    	double sumOut = 0.0;    	
    	for (Transaction.Output out : tx.getOutputs()) {    			
			sumOut += out.value;			
		}    	
    	
    	double fee = sumIn-sumOut;
    	validTXs.put(txUTXO, new TransactionFee(tx, fee));    	
    	
    	return fee;    	
    }
    
    private List<Transaction> getMaximumFeeTXsAndUpdatePool(Map<UTXO, List<UTXO>> txMap, Map<UTXO, 
    		List<UTXO>> utxoMap, Map<UTXO, TransactionFee> validTXs, Node node) {    	
    	
    	PriorityQueue<Node> q = new PriorityQueue<Node>();    	
    	q.add(node);
        
    	node = q.poll();    	
    	while (node.potentialTxs.size() > 0) {
    		UTXO txUTXO = node.potentialTxs.get(0);
    		
    		//set up left node ('tx' chosen) and right node ('tx' not chosen)    		
    		List<UTXO> leftPotentialTxs = new ArrayList<UTXO>(node.potentialTxs);
    		List<UTXO> rightPotentialTxs = new ArrayList<UTXO>(node.potentialTxs);    		
    		    		
    		//find other transactions if any, that claim the same UTXO as the current tx
    		//and remove those transactions from the potential tx list
    		leftPotentialTxs.remove(txUTXO);
    		List<UTXO> utxos = txMap.get(txUTXO);
    		for (UTXO utxo : utxos) {
    			for (UTXO transUTXO : utxoMap.get(utxo)) {
    					if (!transUTXO.equals(txUTXO)) {
    						leftPotentialTxs.remove(transUTXO);
    						leftPotentialTxs.removeAll(findDependantTxs(transUTXO, 
    															utxoMap, validTXs));
    					}
    			}
    		}
    		
    		rightPotentialTxs.remove(txUTXO);
    		rightPotentialTxs.removeAll(findDependantTxs(txUTXO, 
					utxoMap, validTXs));
    		
    		TransactionFee txfee = validTXs.get(txUTXO);
    		Node left = new Node(txfee, node.currentFee + txfee.fee, 
    				calculateFees(leftPotentialTxs, validTXs), leftPotentialTxs, node);
    		Node right = new Node(null, node.currentFee, 
    				calculateFees(rightPotentialTxs, validTXs), rightPotentialTxs, node);   		
		
    		q.add(left);
    		q.add(right);
    		
    		node = q.poll();    		
    	}  
    	
    	List<Transaction> txs = getTXsAndUpdatePool(node);    	
    	return txs;    	
    }
    
    private List<UTXO> findDependantTxs(UTXO txUTXO, Map<UTXO, List<UTXO>> utxoMap, 
    		Map<UTXO, TransactionFee> validTXs) {    	
    	Transaction tx = validTXs.get(txUTXO).tx;
    	
    	List<UTXO> result = new ArrayList<UTXO>();
		for (int i = 0; i < tx.getOutputs().size(); i++) {
			List<UTXO> dependantTxs = utxoMap.get(new UTXO(tx.getHash(), i));
			if (dependantTxs != null) {
				for (UTXO transUTXO : dependantTxs) {
					result.add(transUTXO);
					result.addAll(findDependantTxs(transUTXO, utxoMap, validTXs));
				}
			}
		}		
		return result;
    }
    
    private List<Transaction> getTXsAndUpdatePool(Node node) {    	
    	
    	List<Transaction> txs = new ArrayList<Transaction>();
    	while (node != null) {
    		TransactionFee txfee = node.tx;    		
    		if (txfee != null) {    		
	    		txs.add(txfee.tx);
	    		updateUTXOPool(txfee.tx);
    		}			
			node = node.prev;
    	}    	
    	return txs;    	
    }
    
    private void updateUTXOPool(Transaction tx) {    	
    	
    	for (Transaction.Input in: tx.getInputs()) {    			
			UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
			utxoPool.removeUTXO(utxo); //remove the consumed UTXO by this tx	
		}    	
    	
    	int i = 0;
		for (Transaction.Output out : tx.getOutputs()) {    			
			UTXO utxo = new UTXO(tx.getHash(), i);
			utxoPool.addUTXO(utxo, out);//add the created UTXO by this tx
			i++;
		}
		return;
    }
    
    private void addValidationPool(Transaction tx) {
    	
    	int i = 0;
		for (Transaction.Output out : tx.getOutputs()) {    			
			UTXO utxo = new UTXO(tx.getHash(), i);
			validationPool.addUTXO(utxo, out);//add the created UTXO by this tx
			i++;
		}
		return;
    }    
    
    private double calculateFees(List<UTXO> txs, Map<UTXO, TransactionFee> validTXs) {    	
    	
    	double fees = 0.0;    	
    	for (UTXO txUTXO : txs) {
    		TransactionFee txfee = validTXs.get(txUTXO);    		       	
        	fees += txfee.fee;
    	}    	
    	return fees;
    }
    
    private List<Transaction> testFees(Map<UTXO, List<UTXO>> txMap, Map<UTXO, 
    		List<UTXO>> utxoMap, Map<UTXO, TransactionFee> validTXs, Node node) {    	
    	
    	List<Transaction> txs = new ArrayList<Transaction>();    	
    	for (UTXO txUTXO : validTXs.keySet()) {
    		TransactionFee txfee = validTXs.get(txUTXO);
    		txs.add(txfee.tx);
    	}    	
    	return txs;    	
    }
    
    private double testAddToMapsAndGetFee(Map<UTXO, List<UTXO>> txMap, Map<UTXO, List<UTXO>> utxoMap, 
    		Map<UTXO, TransactionFee> validTXs, UTXO txUTXO, Transaction tx) {    	
    	
    	validTXs.put(txUTXO, new TransactionFee(tx, 0.0));    	
    	return 0.0;
    }
    
    private List<UTXO> getSortedKeyByFee(Map<UTXO, TransactionFee> validTXs) {
    	
    	List<Map.Entry<UTXO, TransactionFee>> sortedEntries = 
    			new ArrayList<Map.Entry<UTXO, TransactionFee>>(validTXs.entrySet());
    	Collections.sort(sortedEntries, 
    			new Comparator<Map.Entry<UTXO, TransactionFee>> (){
    				public int compare(Map.Entry<UTXO, TransactionFee> o1, 
    						Map.Entry<UTXO, TransactionFee> o2) {
    					if (o1.getValue().fee > o2.getValue().fee)
    						return -1;
    					else if(o1.getValue().fee < o2.getValue().fee)
    						return 1;
    					return 0;
    				}
    			});
    	
    	List<UTXO> sortedKey = new ArrayList<UTXO>();
    	for (Map.Entry<UTXO, TransactionFee> entry : sortedEntries) {
    		sortedKey.add(entry.getKey());
    	}
    	return sortedKey;
    }
    
    private UTXOPool utxoPool;
    private UTXOPool validationPool;
}
