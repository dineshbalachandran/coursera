import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private int n;
    private int trials;
    private double[] threshold;

    public PercolationStats(int n, int trials) {
        
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }
        
        this.n = n;
        this.trials = trials;
        threshold = new double[trials];        
        
        for (int i = 0; i < trials; i++)
            conductTrial(i);

    } // perform trials independent experiments on an n-by-n grid

    private void conductTrial(int trial) {

        Percolation p = new Percolation(n);
        int row, col;

        while (!p.percolates()) {

            row = StdRandom.uniform(n) + 1;
            col = StdRandom.uniform(n) + 1;

            p.open(row, col);
        }

        threshold[trial] = 1.0 * p.numberOfOpenSites() / (n * n);

        return;
    }

    public double mean() {
        return StdStats.mean(threshold);
    } // sample mean of percolation threshold

    public double stddev() {
        return StdStats.stddev(threshold);
    } // sample standard deviation of percolation threshold

    public double confidenceLo() {
        return mean() - (1.96 * stddev() / Math.sqrt(trials));
    } // low endpoint of 95% confidence interval

    public double confidenceHi() {
        return mean() + (1.96 * stddev() / Math.sqrt(trials));
    } // high endpoint of 95% confidence interval

    public static void main(String[] args) {      

        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        
        StdRandom.setSeed(1234);
        
        PercolationStats ps = new PercolationStats(n, trials);        

        StdOut.printf("mean                    = %f\n", ps.mean());
        StdOut.printf("stddev                  = %f\n", ps.stddev());
        StdOut.printf("95%% confidence interval = [%f, %f]\n", ps.confidenceLo(), ps.confidenceHi());

        return;
    }

}
