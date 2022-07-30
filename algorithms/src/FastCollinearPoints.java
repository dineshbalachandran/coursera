import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {

    private Point[] points;
    private LineSegment[] linesegment = new LineSegment[1];
    private int segments = 0;

    public FastCollinearPoints(Point[] pts) {

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

        findSegments();

    }

    private void insertLS(LineSegment ls) {

        if (segments == linesegment.length) {
            resizeLS(2 * segments);
        }

        linesegment[segments++] = ls;
    }

    private void resizeLS(int max) {

        LineSegment[] temp = new LineSegment[max];
        for (int i = 0; i < linesegment.length; i++) {
            temp[i] = linesegment[i];
        }
        linesegment = temp;
    }

    private void findSegments() {

        // Point[] pts = new Point[points.length];
        // for (int i = 0; i < points.length; i++) {
        // pts[i] = points[i];
        // }

        Point[] collinearPoints = new Point[points.length];
        int iCP = 0;
        int imxCP = 0, imnCP = 0; // track the max and min Point indexes within
                                  // collinearPoints
        double pslope, cslope;
        List<Double> slopes = new ArrayList<>();
        List<Point> mnPoints = new ArrayList<>();
        List<Point> mxPoints = new ArrayList<>();

        for (int i = 0; i < points.length; i++) {

            Comparator<Point> c = points[i].slopeOrder();
            Arrays.sort(points, i + 1, points.length, c);

            pslope = Double.NEGATIVE_INFINITY;
            for (int j = i + 1; j < points.length; j++) { // skip the i th item,
                                                          // which
                // will be points[i]

                cslope = points[i].slopeTo(points[j]);
                if (cslope == Double.NEGATIVE_INFINITY) {
                    throw new IllegalArgumentException();
                }

                if (cslope == pslope) {
                    collinearPoints[iCP] = points[j];
                    if (collinearPoints[iCP].compareTo(collinearPoints[imnCP]) < 0) {
                        imnCP = iCP;
                    } else if (collinearPoints[iCP].compareTo(collinearPoints[imxCP]) > 0) {
                        imxCP = iCP;
                    }
                    iCP++;
                } else {
                    if (iCP > 2) {
                        collinearPoints[iCP] = points[i];
                        if (collinearPoints[iCP].compareTo(collinearPoints[imnCP]) < 0) {
                            imnCP = iCP;
                        } else if (collinearPoints[iCP].compareTo(collinearPoints[imxCP]) > 0) {
                            imxCP = iCP;
                        }

                        if (!isSubSegment(pslope, slopes, mnPoints, mxPoints, collinearPoints[imnCP],
                                collinearPoints[imxCP])) {
                            slopes.add(pslope);
                            mnPoints.add(collinearPoints[imnCP]);
                            mxPoints.add(collinearPoints[imxCP]);
                            insertLS(new LineSegment(collinearPoints[imnCP], collinearPoints[imxCP]));
                        }
                    }
                    iCP = 0;
                    imnCP = 0;
                    imxCP = 0;
                    collinearPoints[iCP] = points[j];
                    iCP++;
                }
                pslope = cslope;
            }

            if (iCP > 2) {
                collinearPoints[iCP] = points[i];
                if (collinearPoints[iCP].compareTo(collinearPoints[imnCP]) < 0) {
                    imnCP = iCP;
                } else if (collinearPoints[iCP].compareTo(collinearPoints[imxCP]) > 0) {
                    imxCP = iCP;
                }

                if (!isSubSegment(pslope, slopes, mnPoints, mxPoints, collinearPoints[imnCP], collinearPoints[imxCP])) {
                    slopes.add(pslope);
                    mnPoints.add(collinearPoints[imnCP]);
                    mxPoints.add(collinearPoints[imxCP]);
                    insertLS(new LineSegment(collinearPoints[imnCP], collinearPoints[imxCP]));
                }
                iCP = 0;
                imnCP = 0;
                imxCP = 0;
            }
        }
    }

    private boolean isSubSegment(double slope, List<Double> slopes, List<Point> mnPoints, List<Point> mxPoints, Point p,
            Point q) {

        int n = slopes.size();

        Point r, s;
        for (int i = 0; i < n; i++) {
            r = mnPoints.get(i);
            s = mxPoints.get(i);

            if ((slope == slopes.get(i)) && 
                (p.compareTo(r) == 0 || q.compareTo(s) == 0 || slope == p.slopeTo(r))) {
                return true;
            }
        }

        return false;
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();

        return;

    }

}
