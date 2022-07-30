import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
	
	private static final int R = 256;
	
	// apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
    	
    	char[] sequence = new char[R]; //has the index of the character
    	char[] index = new char[R]; //has the character in the index
    	
    	init(sequence, index);
    	
    	//Read binary input one char at a time
    	//find position of char in sequence, output index
    	//move char to beginning of sequence
    	
    	while (!BinaryStdIn.isEmpty()) {
    		char c = BinaryStdIn.readChar();
    		char i = sequence[c];
    		BinaryStdOut.write(i);
    		
    		movefront(sequence, index, i);    		
    	}    	
    	BinaryStdOut.flush();
    }

	

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
    	
    	char[] sequence = new char[R]; //has the index of the character
    	char[] index = new char[R]; //has the character in the index
    	
    	init(sequence, index);
    	
    	while (!BinaryStdIn.isEmpty()) {
    		char i = BinaryStdIn.readChar();
    		char c = index[i];
    		BinaryStdOut.write(c);
    		
    		movefront(sequence, index, i);    		
    	}
    	BinaryStdOut.flush();
    }

	private static void init(char[] sequence, char[] index) {
		//setup sequence and index
    	for (char i = 0; i < R; i++) {
    		sequence[i] = i;
    		index[i] = i;
    	}
	}
	
	private static void movefront(char[] sequence, char[] index, char i) {
		for (int j = i; j > 0; j--) {
			char k = index[j];
			index[j] = index[j-1];
			index[j-1] = k;
			
			sequence[index[j]]   =  (char) j;
			sequence[index[j-1]] =  (char) (j-1);
		}
	}
	
    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
    
    	if (args.length == 0) {
    		throw new IllegalArgumentException("Specify either - (encode) or + (decode)");
    	}
    	
    	switch (args[0]) {
    	case "-": encode(); break;
    	case "+": decode(); break;
    	default :           break;
    	}
    	
    }
}