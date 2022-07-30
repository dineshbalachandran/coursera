import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class TxHandler {

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
    	
    	this.utxoPool = new UTXOPool(utxoPool);
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
			Transaction.Output out = utxoPool.getTxOutput(utxo);
			
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

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        
    	Transaction[] txsArray = new Transaction[0];
    	
    	if (possibleTxs==null)
    		return null;
    	
    	List<Transaction> txs = new ArrayList<Transaction>(possibleTxs.length);
    	
    	for (Transaction tx : possibleTxs) {    		
    		if (!isValidTx(tx)) //if tx is invalid, skip the remaining steps, continue to the next tx
    			continue;
    		
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
    		
    		txs.add(tx);
    	}    	
    	
    	if (txs.size() > 0)
    		txsArray = txs.toArray(new Transaction[0]);
    	
    	return txsArray;
    }
    
    public UTXOPool getUTXOPool() {
    	return utxoPool;
    }
    
    private UTXOPool utxoPool;
}
