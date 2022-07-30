import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    private final int[][] blocks;
    private int blank_row;
    private int blank_col;
    private int manhattan = -1;
    private int hamming = -1;

    public Board(int[][] blocks) { // construct a board from an n-by-n array of
                                   // blocks
        // (where blocks[i][j] = block in row i, column j)

        if (blocks == null) {
            throw new NullPointerException();
        }

        this.blocks = new int[blocks.length][];

        for (int i = 0; i < blocks.length; i++) {
            this.blocks[i] = new int[blocks[i].length];
            for (int j = 0; j < blocks[i].length; j++) {
                this.blocks[i][j] = blocks[i][j];
                if (blocks[i][j] == 0) {
                    blank_row = i;
                    blank_col = j;
                }
            }
        }
    }

    public int dimension() { // board dimension n
        if (blocks != null) {
            return blocks.length;
        } else {
            return 0;
        }
    }

    public int hamming() { // number of blocks out of place

        if (hamming != -1) {
            return hamming;
        }

        int ctr = 1;
        hamming = 0;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (blocks[i][j] != 0 && blocks[i][j] != ctr) {
                    hamming++;
                }
                ctr++;
            }
        }

        return hamming;
    }

    public int manhattan() { // sum of Manhattan distances between blocks and
                             // goal
        if (manhattan != -1) {
            return manhattan;
        }

        int row, col;
        int dim = dimension();
        manhattan = 0;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {

                if (blocks[i][j] == 0) {
                    continue;
                }

                row = (int) (Math.ceil(1.0 * blocks[i][j] / dim) - 1);
                col = blocks[i][j] % dim;
                col = (col == 0) ? (dim - 1) : (col - 1);

                manhattan += Math.abs(i - row) + Math.abs(j - col);
            }
        }
        return manhattan;
    }

    public boolean isGoal() { // is this board the goal board?

        if (hamming() == 0) {
            return true;
        }

        return false;
    }

    public Board twin() { // a board that is obtained by exchanging any pair of
                          // blocks

        int dim = dimension();
        int frow = 0;
        int fcol = 0;
        if (frow == blank_row && fcol == blank_col) {
            frow = 1;
        }

        int nrow = frow + 1;
        int ncol = fcol;
        if ((nrow == blank_row && ncol == blank_col) || (nrow >= dim)) {
            nrow = frow;
            ncol = fcol + 1;
        }

        int temp = blocks[frow][fcol];
        blocks[frow][fcol] = blocks[nrow][ncol];
        blocks[nrow][ncol] = temp;

        Board twin = new Board(blocks);

        // reswap
        temp = blocks[frow][fcol];
        blocks[frow][fcol] = blocks[nrow][ncol];
        blocks[nrow][ncol] = temp;

        return twin;
    }

    public boolean equals(Object y) { // does this board equal y?

        if (y == null)
            return false;

        if (this == y) {
            return true;
        }

        if (this.getClass() != y.getClass()) {
            return false;
        }

        Board o = (Board) y;

        if (o.dimension() != dimension()) {
            return false;
        }

        int dim = dimension();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (blocks[i][j] != o.blocks[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    public Iterable<Board> neighbors() { // all neighboring boards

        List<Board> neighbors = new ArrayList<>();
        int[][] blcks = getCopy();
        int dim = dimension();

        int brow = blank_row;
        int bcol = blank_col;

        // col
        for (int i = -1; i <= 1; i += 2) {
            bcol = blank_col + i;
            if (bcol >= 0 && bcol < dim) {
                blcks[blank_row][blank_col] = blocks[brow][bcol];
                blcks[brow][bcol] = 0;

                neighbors.add(new Board(blcks));

                blcks[brow][bcol] = blocks[brow][bcol];
                blcks[blank_row][blank_col] = 0;
            }
        }

        bcol = blank_col;
        // row
        for (int i = -1; i <= 1; i += 2) {
            brow = blank_row + i;
            if (brow >= 0 && brow < dim) {
                blcks[blank_row][blank_col] = blocks[brow][bcol];
                blcks[brow][bcol] = 0;

                neighbors.add(new Board(blcks));

                blcks[brow][bcol] = blocks[brow][bcol];
                blcks[blank_row][blank_col] = 0;
            }
        }

        return neighbors;
    }

    public String toString() { // string representation of this board (in the
                               // output format specified below)

        int dim = dimension();
        StringBuilder buf = new StringBuilder();

        buf.append(dim).append("\n");
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                buf.append(" ").append(blocks[i][j]);
            }
            buf.append("\n");
        }

        return buf.toString();
    }

    public static void main(String[] args) {

    }

    private int[][] getCopy() {

        int dim = dimension();
        int[][] blcks = new int[dim][];
        for (int i = 0; i < dim; i++) {
            blcks[i] = Arrays.copyOfRange(blocks[i], 0, dim);
        }

        return blcks;

    }
}
