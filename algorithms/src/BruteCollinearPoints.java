import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {

    private Point[] points;
    private LineSegment[] linesegment = new LineSegment[1];
    private int segments = 0;

    public BruteCollinearPoints(Point[] pts) { // finds all line segments
        // containing 4 points

        if (pts == null) {
            throw new NullPointerException();
        }

        points = new Point[pts.length];
        for (int i = 0; i < pts.length; i++) {
            if (pts[i] == null) {
                throw new NullPointerException();
            }
            points[i] = pts[i];
        }

        Arrays.sort(points);

        Point prev = null;
        for (int i = 0; i < points.length; i++) {
            if ((prev != null) && (prev.compareTo(points[i]) == 0)) {
                throw new IllegalArgumentException();
            }
            prev = points[i];
        }

        double slope1, slope2, slope3;
        int i, j, k, l;

        for (i = 0; i < (points.length - 3); i++) {

            for (j = i + 1; j < (points.length - 2); j++) {
                slope1 = points[i].slopeTo(points[j]);

                for (k = j + 1; k < (points.length - 1); k++) {
                    slope2 = points[j].slopeTo(points[k]);

                    if (slope1 == slope2) {

                        for (l = k + 1; l < points.length; l++) {
                            slope3 = points[k].slopeTo(points[l]);

                            if (slope3 == slope2) {
                                insert(new LineSegment(points[i], points[l]));
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private void insert(LineSegment ls) {

        if (segments == linesegment.length) {
            resize(2 * segments);
        }

        linesegment[segments++] = ls;
    }

    private void resize(int max) {

        LineSegment[] temp = new LineSegment[max];

        for (int i = 0; i < linesegment.length; i++) {
            temp[i] = linesegment[i];
        }
        linesegment = temp;
    }

    public int numberOfSegments() {
        return segments;
    } // the number of line segments

    public LineSegment[] segments() {
        return Arrays.copyOfRange(linesegment, 0, segments);
    } // the line segments

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();

        return;

    }

}
