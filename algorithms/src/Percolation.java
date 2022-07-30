import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private boolean[][] open;
    private int size;
    private int openSites;
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF uf_a;

    public Percolation(int n) { // create n-by-n grid, with all sites blocked
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        size = n;
        open = new boolean[n][n];
        openSites = 0;
        uf = new WeightedQuickUnionUF(n * n + 2);
        uf_a = new WeightedQuickUnionUF(n * n + 1);
    }

    private void connectNearSites(int row, int col) {

        int prev, next;

        prev = row - 2;

        int p = (row - 1) * size + col;
        int q;

        if (prev >= 0 && isOpen(prev + 1, col)) {
            q = prev * size + col;
            uf.union(p, q);
            uf_a.union(p, q);
        }

        next = row;
        if (next < size && isOpen(next + 1, col)) {
            q = next * size + col;
            uf.union(p, q);
            uf_a.union(p, q);
        }

        prev = col - 2;
        if (prev >= 0 && isOpen(row, prev + 1)) {
            q = (row - 1) * size + (prev + 1);
            uf.union(p, q);
            uf_a.union(p, q);
        }

        next = col;
        if (next < size && isOpen(row, next + 1)) {
            q = (row - 1) * size + (next + 1);
            uf.union(p, q);
            uf_a.union(p, q);
        }

        return;

    }

    public void open(int row, int col) { // open site (row, col) if it is not
                                         // open already

        if (row > size || col > size || row <= 0 || col <= 0) {
            throw new IndexOutOfBoundsException();
        }

        if (open[row - 1][col - 1])
            return;

        open[row - 1][col - 1] = true;

        int q = (row - 1) * size + col;

        if (row == 1) {
            uf.union(0, q);
            uf_a.union(0, q);
        }

        connectNearSites(row, col);

        if (row == size) {
            uf.union(q, size * size + 1);
        }

        openSites++;

        return;
    }

    public boolean isOpen(int row, int col) { // is site (row, col) open?
        if (row > size || col > size || row <= 0 || col <= 0) {
            throw new IndexOutOfBoundsException();
        }

        return open[row - 1][col - 1];
    }

    public boolean isFull(int row, int col) { // is site (row, col) full?
        if (row > size || col > size || row <= 0 || col <= 0) {
            throw new IndexOutOfBoundsException();
        }

        int q = (row - 1) * size + col;

        return uf_a.connected(0, q);
    }

    public int numberOfOpenSites() {
        return openSites;
    } // number of open sites

    public boolean percolates() { // does the system percolate?

        return uf.connected(0, size * size + 1);
    }

    public static void main(String[] args) { // test client (optional)

    }

}
