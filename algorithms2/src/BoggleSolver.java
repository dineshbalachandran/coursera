import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {

	private static final int R = 26;

	private static class Node {
		private Node[] next = new Node[R];
		private boolean isString;
	}
	
	private enum Direction { LEFT, RIGHT, UP, DOWN, LEFT_UP, RIGHT_UP, LEFT_DOWN, RIGHT_DOWN };

	private Node root;
	private int n;

	private Node add(Node x, String key, int d) {
		if (x == null) x = new Node();
		if (d == key.length()) {
			if (!x.isString) n++;
			x.isString = true;
		}
		else {
			char c = key.charAt(d);
			x.next[c-'A'] = add(x.next[c-'A'], key, d+1);
		}
		return x;
	}

	private Node get(Node x, String key, int d) {
		if (x == null) return null;
		if (d == key.length()) return x;
		char c = key.charAt(d);
		return get(x.next[c-'A'], key, d+1);
	}


	// Initializes the data structure using the given array of strings as the dictionary.
	// (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
	public BoggleSolver(String[] dictionary) {		

		if (dictionary == null) 
			throw new IllegalArgumentException("argument to BoggleSolver constructor is null");

		root = new Node();		
		for (String key : dictionary) {
			root = add(root, key, 0);
		}
	}

	// Returns the set of all valid words in the given Boggle board, as an Iterable.
	public Iterable<String> getAllValidWords(BoggleBoard board) {

		SET<String> words = new SET<String>();

		boolean[][] marked = new boolean[board.rows()][board.cols()];
		
		for (int i = 0; i < board.rows(); i++) {
			for (int j = 0; j < board.cols(); j++) {				
				char c = board.getLetter(i, j);
				Node n = root.next[c-'A'];
				StringBuilder prefix = new StringBuilder(""+c);
				if (c == 'Q') {
					prefix.append('U');
					if (n != null) {
						n = n.next['U'-'A'];
					}
				}
				marked[i][j] = true;
				addValidWords(board, i, j, n, prefix, marked, words);
				marked[i][j] = false;
			}
		}

		return words;		
	}

	private void addValidWords(BoggleBoard board, int i, int j, 
			Node x, StringBuilder prefix, boolean[][] marked, SET<String> words) {

		if (x == null) return;		

		if (x.isString) {
			String s = prefix.toString();
			if (s.length() > 2)
				words.add(s);
		}
		
		for (Direction d : Direction.values()) {
			
			int rw = -1; 
			int cl = -1;		
			
			switch (d) {
			
			case RIGHT: 		rw = i;   cl = j+1; break;
			case LEFT:  		rw = i;   cl = j-1; break;
			case DOWN:  		rw = i+1; cl = j; 	break;
			case UP:    		rw = i-1; cl = j; 	break;
			case RIGHT_UP:		rw = i-1; cl = j+1; break;
			case RIGHT_DOWN:	rw = i+1; cl = j+1; break;
			case LEFT_UP:		rw = i-1; cl = j-1; break;
			case LEFT_DOWN:		rw = i+1; cl = j-1; break;
			
			}
			
			if (rw >=0 && rw < board.rows() && cl >= 0 && cl < board.cols() && !marked[rw][cl]) {
				char c = board.getLetter(rw, cl);
				Node n = x.next[c-'A'];
				
				prefix.append(c);				
				if (c == 'Q') {
					prefix.append('U');
					if (n != null) {
						n = n.next['U'-'A'];
					}
				}				
				marked[rw][cl] = true;
				addValidWords(board, rw, cl, n, prefix, marked, words);
				marked[rw][cl] = false;
				if (c == 'Q') {
					prefix.deleteCharAt(prefix.length() - 1);
					prefix.deleteCharAt(prefix.length() - 1);
				}
				else
					prefix.deleteCharAt(prefix.length() - 1);				
			}
		}		
	}

	// Returns the score of the given word if it is in the dictionary, zero otherwise.
	// (You can assume the word contains only the uppercase letters A through Z.)
	public int scoreOf(String word) {

		int score = 0;

		Node x = get(root, word, 0);
		if (x != null && x.isString) {
			switch (word.length()) {
			case 0:
			case 1:
			case 2:  score = 0;  break;
			case 3:
			case 4:  score = 1;  break;
			case 5:  score = 2;  break;
			case 6:  score = 3;  break;
			case 7:  score = 5;  break;
			default: score = 11; break;
			}
		}

		return score;
	}

	public static void main(String[] args) {

		In in = new In(args[0]);
		String[] dictionary = in.readAllStrings();
		BoggleSolver solver = new BoggleSolver(dictionary);

		BoggleBoard board = new BoggleBoard(args[1]);
		int score = 0;
		for (String word : solver.getAllValidWords(board)) {
			StdOut.println(word);
			score += solver.scoreOf(word);
		}
		StdOut.println("Score = " + score);
	}

}
