/*
 * Main test code for Cousera cryptocurrency assignment1
 * Based on code by Sven Mentl and Pietro Brunetti
 * 
 * Copyright:
 * - Sven Mentl
 * - Pietro Brunetti
 * - Bruce Arden
 * - Tero Keski-Valkama
 */

import java.math.BigInteger;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

   public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
     
        /*
         * Set up the UTXOPool
         */        
        UTXOPool utxoPool = new UTXOPool();
        List<Tx> txs = setUpTx(utxoPool);
        int i = 0;
        for (Transaction tx : txs) {
        	System.out.println("" + i++ + ":" + tx.hashCode());
        }
        
        /*
         * Start the test
         */
        // Remember that the utxoPool contains a single unspent Transaction.Output which is
        // the coin from Scrooge.
        MaxFeeTxHandler txHandler = new MaxFeeTxHandler(utxoPool);
        Transaction[] result = txHandler.handleTxs(txs.toArray(new Tx[0]));
        System.out.println("txHandler.handleTxs() returns: " +
            result.length + " transaction(s)");
        i = 0;
        for (Transaction tx : result) {
        	System.out.println("" + i++ + ":" + tx.hashCode() + 
        			":" + getFees(tx, utxoPool));
        }
    }

    public static class Tx extends Transaction { 
        public void signTx(PrivateKey sk, int input) throws SignatureException {
            Signature sig = null;
            try {
                sig = Signature.getInstance("SHA256withRSA");
                sig.initSign(sk);
                sig.update(this.getRawDataToSign(input));
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
            this.addSignature(sig.sign(),input);
            // Note that this method is incorrectly named, and should not in fact override the Java
            // object finalize garbage collection related method.
            this.finalize();
        }
    }
    
    private static double getFees(Transaction tx, UTXOPool utxoPool) {
    	double sumIn = 0.0;
    	for (Transaction.Input in: tx.getInputs()) {    			
			UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);
			sumIn += utxoPool.getTxOutput(utxo).value;
    	}    	
    	
    	double sumOut = 0.0;    	
    	for (Transaction.Output out : tx.getOutputs()) {    			
			sumOut += out.value;			
		}    	
    	
    	return sumIn-sumOut;
    }
    
    private static List<Tx> setUpTx(UTXOPool utxoPool) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    	
    	/*
         * Generate key pairs, for Scrooge & Alice
         */
        KeyPair pk_scrooge = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        KeyPair pk_alice   = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        /*
         * Set up the root transaction:
         *
         * Generating a root transaction tx out of thin air, so that Scrooge owns a coin of value 10
         * By thin air I mean that this tx will not be validated, I just need it to get
         * a proper Transaction.Output which I then can put in the UTXOPool, which will be passed
         * to the TXHandler.
         */
        Tx tx = new Tx();
        tx.addOutput(10, pk_scrooge.getPublic());

        // This value has no meaning, but tx.getRawDataToSign(0) will access it in prevTxHash;
        byte[] initialHash = BigInteger.valueOf(0).toByteArray();
        tx.addInput(initialHash, 0);

        tx.signTx(pk_scrooge.getPrivate(), 0);

        /*
         * Set up the UTXOPool
         */
        // The transaction output of the root transaction is the initial unspent output.        
        UTXO utxo = new UTXO(tx.getHash(),0);
        utxoPool.addUTXO(utxo, tx.getOutput(0));

        List<Tx> txs = new ArrayList<Tx>();
        
        //transaction with no fees
        Tx tx0 = new Tx();

        // the Transaction.Output of tx at position 0 has a value of 10
        tx0.addInput(tx.getHash(), 0);

        tx0.addOutput(5, pk_alice.getPublic());
        tx0.addOutput(1, pk_alice.getPublic());
        tx0.addOutput(1, pk_alice.getPublic());
        // Note that in the real world fixed-point types would be used for the values, not doubles.
        // Doubles exhibit floating-point rounding errors. This type should be for example BigInteger
        // and denote the smallest coin fractions (Satoshi in Bitcoin).

        // There is only one (at position 0) Transaction.Input in tx2
        // and it contains the coin from Scrooge, therefore I have to sign with the private key from Scrooge
        tx0.signTx(pk_scrooge.getPrivate(), 0);
        
        //transaction with fees that uses the same input as tx2
        Tx tx1 = new Tx();
        tx1.addInput(tx.getHash(), 0);
        tx1.addOutput(5, pk_alice.getPublic());
        tx1.addOutput(3, pk_alice.getPublic());
        tx1.addOutput(1, pk_alice.getPublic());
        tx1.signTx(pk_scrooge.getPrivate(), 0);        
        
        Tx tx2 = new Tx();
        tx2.addInput(tx1.getHash(), 0);
        tx2.addOutput(5, pk_alice.getPublic());        
        tx2.signTx(pk_alice.getPrivate(), 0);        
       
        txs.add(tx1);
        txs.add(tx0);
        txs.add(tx2);
        
        return txs;
    	
    }
}
