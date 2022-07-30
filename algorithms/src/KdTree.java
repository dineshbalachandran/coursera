import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {

    private Node root;
    private int size = 0;
    private int count = 1;

    private class Node {

        private Point2D p;
        private Node left;
        private Node right;
        private RectHV[] rectangles;

        public Node(Point2D pt, Node l, Node r) {
            p = pt;
            left = l;
            right = r;
        }

        public void setRect(RectHV parentRect, int level) {

            RectHV[] rect = new RectHV[2];

            if (parentRect == null) { // root node
                rect[0] = new RectHV(0.0, 0.0, p.x(), 1.0);
                rect[1] = new RectHV(p.x(), 0.0, 1.0, 1.0);
            } else {
                if (level % 2 == 0) { // left and right
                    rect[0] = new RectHV(parentRect.xmin(), parentRect.ymin(), p.x(), parentRect.ymax());
                    rect[1] = new RectHV(p.x(), parentRect.ymin(), parentRect.xmax(), parentRect.ymax());
                } else { // bottom and top
                    rect[0] = new RectHV(parentRect.xmin(), parentRect.ymin(), parentRect.xmax(), p.y());
                    rect[1] = new RectHV(parentRect.xmin(), p.y(), parentRect.xmax(), parentRect.ymax());
                }
            }

            rectangles = rect;

            return;
        }

        public RectHV[] getRect() {
            return rectangles;
        }
    }

    public KdTree() {
    };

    public boolean isEmpty() {
        return size() == 0 ? true : false;
    } // is the set empty?

    public int size() {
        return size;
    }

    public void insert(Point2D p) {

        if (p == null) {
            throw new NullPointerException();
        }

        if (root == null) {
            root = new Node(p, null, null);
            root.setRect(null, 0);
            size++;

            return;
        }

        Node n = root;
        Node parent = null;        

        int level = 0;

        while (n != null) {

            Comparator<Point2D> c = null, altc = null;

            switch (level%2) {
            case 0:
                c = Point2D.X_ORDER;
                altc = Point2D.Y_ORDER;
                break;
            
            case 1:
                c = Point2D.Y_ORDER;
                altc = Point2D.X_ORDER;
                break;
            } 

            parent = n;
            
            switch (comparePoints(p, n.p, c, altc)) {
            case +1:
                n = n.right;
                if (n == null) {
                    parent.right = new Node(p, null, null);
                    parent.right.setRect(parent.getRect()[1], level + 1);
                    size++;
                }
                break;
            
            case -1:
                n = n.left;
                if (n == null) {
                    parent.left = new Node(p, null, null);
                    parent.left.setRect(parent.getRect()[0], level + 1);
                    size++;
                }
                break;
            
            case 0:
                // point already present
                n = null;
                break;
            }

            level++;
        }

        return;
    } // add the point to the set (if it is not already in the set)

    public boolean contains(Point2D p) {

        if (p == null) {
            throw new NullPointerException();
        }

        Node n = root;
        Node pt = null;
        int level = 0;

        while (n != null && pt == null) {

            Comparator<Point2D> c = null, altc = null;

            switch (level%2) {
            case 0:
                c = Point2D.X_ORDER;
                altc = Point2D.Y_ORDER;
                break;
            
            case 1:
                c = Point2D.Y_ORDER;
                altc = Point2D.X_ORDER;
                break;
            }            
            

            switch (comparePoints(p, n.p, c, altc)) {
            case +1:
                n = n.right;
                break;
            case -1:
                n = n.left;
                break;
            case 0:
                pt = n;
                break;
            }
            level++;
        }

        return pt != null ? true : false;
    } // does the set contain point p?

    public void draw() {

        StdDraw.setCanvasSize(400, 400);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.enableDoubleBuffering();

        if (root != null)
            draw(root, 0);

        return;

    } // draw all points to standard draw

    public Iterable<Point2D> range(RectHV rect) {

        if (rect == null) {
            throw new NullPointerException();
        }

        List<Point2D> plist = new ArrayList<>();
        rangeSearch(root, rect, plist);

        return plist;
    } // all points that are inside the rectangle

    public Point2D nearest(Point2D p) {

        if (p == null) {
            throw new NullPointerException();
        }

        if (root == null)
            return null;

        Point2D pt = nearest(p, root, root.p);

        return pt;
    } // a nearest neighbor in the set to point p; null if the set is empty

    private void draw(Node n, int level) {

        if (n.left != null)
            draw(n.left, level + 1);
        drawNode(n, level);

        if (n.right != null)
            draw(n.right, level + 1);
        drawNode(n, level);

        return;
    }

    private void drawNode(Node n, int level) {

    	StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        n.p.draw();

        RectHV[] rects = n.getRect();
        switch (level%2) {
        case 0:
        	StdDraw.setPenRadius(0.001);
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(rects[0].xmax(), rects[0].ymin(), rects[0].xmax(), rects[0].ymax());
            break;
        case 1:
        	StdDraw.setPenRadius(0.001);
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rects[0].xmin(), rects[0].ymax(), rects[0].xmax(), rects[0].ymax());
            break;
        }        

        return;
    }

    private int comparePoints(Point2D p, Point2D np, Comparator<Point2D> c, Comparator<Point2D> altc) {

        int order = c.compare(p, np);
        int altOrder;

        if (order != 0) {
            return order;
        } else {
            altOrder = altc.compare(p, np);
        }

        if (altOrder != 0) {
            altOrder = +1; // force to the right;
        }

        return altOrder;
    }

    private void rangeSearch(Node n, RectHV rect, List<Point2D> plist) {

        if (n == null)
            return;

        if (rect.contains(n.p)) {
            plist.add(n.p);
        }

        RectHV[] rects = n.getRect();

        if (rects[0].intersects(rect)) {
            rangeSearch(n.left, rect, plist);
        }
        if (rects[1].intersects(rect)) {
            rangeSearch(n.right, rect, plist);
        }

        return;
    }

    private Point2D nearest(Point2D p, Node n, Point2D nearest) {

        if (n == null)
            return nearest;
        
        StdOut.println("Count :" + count++);

        double distance = p.distanceSquaredTo(n.p);
        if (distance < p.distanceSquaredTo(nearest))
            nearest = n.p;

        RectHV[] rects = n.getRect();

        double distanceleft = rects[0].distanceSquaredTo(p);
        double distanceright = rects[1].distanceSquaredTo(p);

        if (distanceleft < distanceright) {

            nearest = nearest(p, n.left, nearest);

            if (distanceright < p.distanceSquaredTo(nearest)) {
                nearest = nearest(p, n.right, nearest);
            }

        } else {

            nearest = nearest(p, n.right, nearest);

            if (distanceleft < p.distanceSquaredTo(nearest)) {
                nearest = nearest(p, n.left, nearest);
            }
        }

        return nearest;
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);

        double x, y;
        KdTree kd = new KdTree();

        while (!in.isEmpty()) {
            x = in.readDouble();
            y = in.readDouble();

            kd.insert(new Point2D(x, y));
        }

        kd.draw();
        StdDraw.show();

        StdDraw.setPenRadius(0.01);
        Point2D pt = new Point2D(0.81, 0.3);
        pt.draw();
        StdDraw.setPenColor(StdDraw.GREEN);
        kd.nearest(pt).draw();
        StdDraw.show();

        return;
    }

}
