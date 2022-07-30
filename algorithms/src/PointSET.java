import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {

    private SET<Point2D> points = new SET<>();

    public PointSET() {
    } // construct an empty set of points

    public boolean isEmpty() {
        return points.isEmpty();
    } // is the set empty?

    public int size() {
        return points.size();
    } // number of points in the set

    public void insert(Point2D p) {
        if (!contains(p)) {
            points.add(p);
        }
    } // add the point to the set (if it is not already in the set)

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }
        return points.contains(p);
    } // does the set contain point p?

    public void draw() {

        StdDraw.setCanvasSize(400, 400);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.enableDoubleBuffering();

        for (Point2D p : points) {
            p.draw();
        }

    } // draw all points to standard draw

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new NullPointerException();
        }

        List<Point2D> plist = new ArrayList<>();
        for (Point2D p : points) {
            if (rect.contains(p))
                plist.add(p);
        }

        return plist;
    } // all points that are inside the rectangle

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }

        double nearest = Double.POSITIVE_INFINITY;
        double distance;
        Point2D nearestPoint = null;
        for (Point2D pt : points) {
            distance = pt.distanceSquaredTo(p);
            if (distance < nearest) {
                nearest = distance;
                nearestPoint = pt;
            }
        }

        return nearestPoint;
    } // a nearest neighbor in the set to point p; null if the set is empty

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);

        double x, y;
        PointSET pset = new PointSET();

        while (!in.isEmpty()) {
            x = in.readDouble();
            y = in.readDouble();

            pset.insert(new Point2D(x, y));
        }

        pset.draw();
        StdDraw.show();

    }

}
