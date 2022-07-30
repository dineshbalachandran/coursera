import java.util.Arrays;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private Node solution;

    private class Node implements Comparable<Node> {
        private Node prev;
        private int moves;
        private Board board;

        public Node(Board b, int m, Node p) {
            prev = p;
            board = b;
            moves = m;
        }

        public int priority() {
            return board.manhattan() + moves;
        }

        @Override
        public int compareTo(Node arg0) {
            int p = priority();
            int ap = arg0.priority();

            if (p > ap) {
                return 1;
            } else if (p < ap) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public Solver(Board initial) { // find a solution to the initial board
                                   // (using the A* algorithm)

        if (initial == null) {
            throw new NullPointerException();
        }
        solve(initial);
    }

    private void solve(Board initial) {

        MinPQ<Node> pq = new MinPQ<>();
        MinPQ<Node> twinpq = new MinPQ<>();

        Node search = new Node(initial, 0, null);
        Node twinsearch = new Node(initial.twin(), 0, null);
        
        pq.insert(search);
        twinpq.insert(twinsearch);

        search = pq.delMin();
        twinsearch = twinpq.delMin();

        while (!search.board.isGoal() && !twinsearch.board.isGoal()) {

            Node prev = search.prev;
            Node twinprev = twinsearch.prev;

            for (Board neighbor : search.board.neighbors()) {
                if (prev == null || !neighbor.equals(prev.board)) {
                    pq.insert(new Node(neighbor, search.moves + 1, search));
                }
            }

            for (Board neighbor : twinsearch.board.neighbors()) {
                if (twinprev == null || !neighbor.equals(twinprev.board)) {
                    twinpq.insert(new Node(neighbor, twinsearch.moves + 1, twinsearch));
                }
            }

            search = pq.delMin();
            twinsearch = twinpq.delMin();

        }

        if (search.board.isGoal()) {            
            solution = search;
        }
    }

    public boolean isSolvable() { // is the initial board solvable?
        return (solution != null) ? true : false;
    }

    public int moves() { // min number of moves to solve initial board; -1 if
                         // unsolvable
        return isSolvable() ? solution.moves : -1;
    }

    public Iterable<Board> solution() { // sequence of boards in a shortest
                                        // solution; null if unsolvable
        if (solution == null) {
            return null;
        }

        Board[] boards = new Board[solution.moves + 1];
        Node n = solution;

        while (n != null) {
            boards[n.moves] = n.board;
            n = n.prev;
        }

        return Arrays.asList(boards);

    }

    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }

        return;
    }

}
