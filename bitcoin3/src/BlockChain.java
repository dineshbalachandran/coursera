import java.util.ArrayList;
import java.util.List;

// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.

public class BlockChain {
    public static final int CUT_OFF_AGE = 10;
	
    /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    public BlockChain(Block genesisBlock) {
    	
        txPool = new TransactionPool();
        UTXOPool utxoPool = new UTXOPool();  
        
        Transaction coinbase = genesisBlock.getCoinbase();
        for (int i = 0; i < coinbase.getOutputs().size(); i++) {    		
    		UTXO utxo = new UTXO(coinbase.getHash(), i);
    		utxoPool.addUTXO(utxo, coinbase.getOutput(i));
    	}
        
        GN = new Node(genesisBlock, utxoPool);
        maxNode = GN;
        maxHeight = 1;
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        return maxNode.block;
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() {
        return maxNode.utxoPool;
    }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
        return txPool;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     * 
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {
        
    	if(block.getPrevBlockHash() == null) {
    		return false;
    	}    	
    	List<Node> branch = getBranch(GN, new ByteArrayWrapper(block.getPrevBlockHash()));
    	
    	int height = branch.size() + 1;    	
    	if (height == 1) { //could not find previous block, for valid blocks height will be > 1
    		return false;
    	}
    	
    	if (height <= maxHeight - CUT_OFF_AGE) { // less than cut off age
    		return false;
    	}
    	
    	Node parent = branch.get(0);
    	if (!areTransactionsValid(block, parent)) {
    		return false;
    	}    	
    	
    	Node blockNode = createBlockNode(block, parent);
    	
    	if (height > maxHeight) {
    		maxHeight = height; // this can equivalently be written as maxHeight++
    		maxNode = blockNode;
    	}
    	
    	return true;
    }

	private boolean areTransactionsValid(Block block, Node parent) {		
		
		TxHandler txHandler = new TxHandler(parent.utxoPool);		
		ArrayList<Transaction> txs = block.getTransactions();		

		Transaction[] txsArray = txs.toArray(new Transaction[0]);
		Transaction[] correctTxs = txHandler.handleTxs(txsArray);
		if (correctTxs.length != txsArray.length)
		  return false;		
			
		return true;
	}

	private Node createBlockNode(Block block, Node parent) {		
			
		UTXOPool utxoPool = new UTXOPool(parent.utxoPool);
		
    	for (Transaction tx : block.getTransactions()) {    		
    		txPool.removeTransaction(tx.getHash());
    		
    		for (int i = 0; i < tx.getInputs().size(); i++) {
    			UTXO utxo = new UTXO(tx.getInput(i).prevTxHash, i);
    			utxoPool.removeUTXO(utxo);
    		}
    		
    		for (int i = 0; i < tx.getOutputs().size(); i++) {
    			UTXO utxo = new UTXO(tx.getHash(), i);
    			utxoPool.addUTXO(utxo, tx.getOutput(i));
    		}    		
    	}
    	
    	Transaction coinbase = block.getCoinbase();
        for (int i = 0; i < coinbase.getOutputs().size(); i++) {    		
    		UTXO utxo = new UTXO(coinbase.getHash(), i);
    		utxoPool.addUTXO(utxo, coinbase.getOutput(i));
    	}
    	
    	Node blockNode = new Node(block, utxoPool);
    	parent.children.add(blockNode);
    	
    	return blockNode;    	
    }

	private List<Node> getBranch(Node node, ByteArrayWrapper blockHash) {    	
		
		List<Node> branch = new ArrayList<Node>();
    	if (blockHash.equals(new ByteArrayWrapper(node.block.getHash()))) {
    		branch.add(node);
    		return branch;
    	}
    	for (Node child : node.children) {
    		branch = getBranch(child, blockHash);
    		if (branch.size() > 0) {
    			branch.add(child);
    			return branch;
    		}
    	}    	
    	return branch;		
	}

	/** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {        
    	txPool.addTransaction(tx);    	
    }
    
    private Node GN; //the node representing the genesis block
    private Node maxNode; //the node representing the max height block
    private int maxHeight;
    private TransactionPool txPool;    
    
    private class Node {    	
    	private Block block;
    	private List<Node> children;
    	private UTXOPool utxoPool;
    	
    	public Node(Block block, UTXOPool utxoPool) {
    		this.block = block;    		
    		this.children = new ArrayList<Node>();
    		this.utxoPool = utxoPool;
    	}    	
    }    
}