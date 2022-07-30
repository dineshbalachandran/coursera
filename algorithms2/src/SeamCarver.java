import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

	private Picture pic;
	private double[][] e;

	private enum Orientation {
		VERTICAL, HORIZONTAL;
	}

	// create a seam carver object based on the given picture
	public SeamCarver(Picture picture) {
		validatePicture(picture);
		
		pic = new Picture(picture);
		calculatePictureEnergy();		
	}

	// current picture
	public Picture picture() {
		return new Picture(pic);
	}

	// width of current picture
	public int width() {		
		return pic.width();
	}

	// height of current picture
	public int height() {		
		return pic.height();
	}

	// energy of pixel at column x and row y
	public double energy(int x, int y) {

		validateCoordinate(x, 0, width()-1);
		validateCoordinate(y, 0, height()-1);
		
		if (e[x][y] > 0.0) {
			return e[x][y];
		}

		if (x == 0 || x == width()-1) {
			e[x][y] = 1000.0;
			return e[x][y];
		}

		if (y == 0 || y == height()-1) {
			e[x][y] = 1000.0;
			return e[x][y];
		}

		double delta_x = delta_square(pic.getRGB(x-1, y), pic.getRGB(x+1, y));
		double delta_y = delta_square(pic.getRGB(x, y-1), pic.getRGB(x, y+1));

		e[x][y] = Math.sqrt(delta_x + delta_y);
		return e[x][y];
	}

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam() {
		return shortestpath(Orientation.HORIZONTAL);
	}

	// sequence of indices for vertical seam
	public int[] findVerticalSeam() {
		return shortestpath(Orientation.VERTICAL);
	}

	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] seam) {
		
		if (height() <= 1) {
			throw new IllegalArgumentException("No more seam removal possible.");
		}
		
		validateSeam(seam, 0, height()-1, width());

		Picture picture = new Picture(width(), height()-1);
		for (int x = 0; x < width(); x++) {
			
			int offset = 0;
			for (int y = 0; y < height(); y++) {

				if (y == seam[x]) {// skip the horizontal seam position
					offset = -1;
					continue;
				}

				int rgb = pic.getRGB(x, y);
				picture.setRGB(x, y + offset, rgb);
			}
		}
		pic = picture;
		calculatePictureEnergy();
	}

	// remove vertical seam from current picture
	public void removeVerticalSeam(int[] seam) {
		
		if (width() <= 1) {
			throw new IllegalArgumentException("No more seam removal possible.");
		}

		validateSeam(seam, 0, width()-1, height());

		Picture picture = new Picture(width()-1, height());
		for (int y = 0; y < height(); y++) {
			
			int offset = 0;			
			for (int x = 0; x < width(); x++) {

				if (x == seam[y]) {// skip the vertical seam position
					offset = -1;
					continue;
				}

				int rgb = pic.getRGB(x, y);
				picture.setRGB(x + offset, y, rgb);
			}
		}
		pic = picture;
		calculatePictureEnergy();
	}
	
	private void calculatePictureEnergy() {
		
		e = new double[width()][height()];		
		for (int i = 0; i < width(); i++) 
			for (int j = 0; j < height(); j++)
				energy(i, j);
	}
	
	private double delta_square(int rgb0, int rgb1) {

		int r0 = (rgb0 >> 16) & 0xFF;
		int r1 = (rgb1 >> 16) & 0xFF;
		double r_2 = (r0 - r1) * (r0 - r1);

		int g0 = (rgb0 >> 8) & 0xFF;
		int g1 = (rgb1 >> 8) & 0xFF;
		double g_2 = (g0 - g1) * (g0 - g1);

		int b0 = (rgb0 >> 0) & 0xFF;
		int b1 = (rgb1 >> 0) & 0xFF;
		double b_2 = (b0 - b1) * (b0 - b1);

		return r_2 + g_2 + b_2;
	}

	private void validatePicture(Picture picture) {
		if (picture == null) {
			throw new IllegalArgumentException("A null picture object found.");
		}
	}

	private void validateCoordinate(int x, int low, int high) {
		if (x < low || x > high) {
			throw new IllegalArgumentException(
					"Range should be between " + low + " and " + high + ". Co-ordinate: " + x);
		}
	}

	private void validateSeam(int[] seam, int low, int high, int size) {
		if (seam == null) {
			throw new IllegalArgumentException("Seam object is null.");
		}

		if (seam.length != size) {
			throw new IllegalArgumentException(
					"Seam length(" + seam.length + ") is not " + "equal to picture dimension: " + size);
		}

		int prev = seam[0] - 1; // initial value
		for (int x : seam) {
			validateCoordinate(x, low, high);
			if (Math.abs(x-prev) > 1) {
				throw new IllegalArgumentException("Adjacent entries differ by > 1.");
			}
			prev = x;
		}
	}

	private int[] getEdge(int v, Orientation ot) {

		int x = getx(v, ot);
		int y = gety(v, ot);

		int[] edges = null;
		
		switch (ot) {
		case VERTICAL:
			if (y == -1) { // source vertex
				edges = new int[width()];
				for (int i = 0; i < width(); i++)
					edges[i] = index(i, 0, ot);
			} else if (y == height() - 1) { // bottom most pixel vertices
				edges = new int[] { index(0, height(), ot) };
			} else if (x == 0) { // left most pixel vertices
				edges = new int[] {index(0, y+1, ot), index(1, y+1, ot)};				
			} else if (x == width() - 1) { // right most pixel vertices
				edges = new int[] {index(x-1, y+1, ot), index(x-1, y+1, ot)};				
			} else { // all other vertices
				edges = new int[] {index(x-1, y+1, ot), index(x, y+1, ot), index(x+1, y+1, ot)};				
			}
			break;
		case HORIZONTAL:
			if (y == -1) { // source vertex
				edges = new int[height()];
				for (int i = 0; i < height(); i++)
					edges[i] = index(0, i, ot);
			} else if (x == width() - 1) { // right most pixel vertices
				edges = new int[] { index(width(), 0, ot) };
			} else if (y == 0) { // top most pixel vertices
				edges = new int[] {index(x+1, 0, ot), index(x+1, 1, ot)};
			} else if (y == height() - 1) { // bottom most pixel vertices
				edges = new int[] {index(x+1, y-1, ot), index(x+1, y, ot)};				
			} else { // all other vertices
				edges = new int[] {index(x+1, y-1, ot), index(x+1, y, ot), index(x+1, y+1, ot)};				
			}
			break;
		default:
			break;
		}		

		return edges;
	}

	private int index(int i, int j, Orientation ot) {
		return (ot == Orientation.VERTICAL) ? (j * width() + i + 1) : (i * height() + j + 1);
	}

	private int gety(int v, Orientation ot) {

		int y = -1;
		switch (ot) {
		case VERTICAL:
			y = (v != 0) ? (v - 1) / width() : -1;
			break;
		case HORIZONTAL:
			y = (v != 0) ? (v - 1) % height() : -1;
			break;
		}		

		return y;
	}

	private int getx(int v, Orientation ot) {

		int x = -1;
		switch (ot) {
		case VERTICAL:
			x =	(v != 0) ? (v - 1) % width() : width() - 1;
			break;
		case HORIZONTAL:
			x =	(v != 0) ? (v - 1) / height() : height() - 1;
			break;
		}		

		return x;
	}

	private int[] shortestpath(Orientation ot) {
		
		double[] distTo = new double[width() * height() + 2];
		int[] edgeTo = new int[width() * height() + 2];

		// source index
		distTo[0] = 0.0;
		for (int i = 0; i < width(); i++) {
			for (int j = 0; j < height(); j++) {
				distTo[index(i, j, ot)] = Double.POSITIVE_INFINITY;
			}
		}
		// target index
		distTo[width() * height() + 1] = Double.POSITIVE_INFINITY;


		for (int v = 0; v < width() * height() + 1; v++) {
			for (int w : getEdge(v, ot)) {
				relax(v, w, ot, distTo, edgeTo);
			}
		}

		int[] seam = getSeam(ot, edgeTo);

		return seam;
	}
	
	private void relax(int v, int w, Orientation ot, double[] distTo, int[] edgeTo) {

		int wx = getx(w, ot);
		int wy = gety(w, ot);

		double wt = 0.0;
		
		if ((ot == Orientation.VERTICAL && (wy == -1 || wy == height())) || 
			(ot == Orientation.HORIZONTAL && (wy == -1 || wx == width()))	) {
			wt = 0.0;
		} else {
			wt = energy(wx, wy); 
		}

		if (distTo[w] > distTo[v] + wt) {
			distTo[w] = distTo[v] + wt;
			edgeTo[w] = v;
		}
	}

	private int[] getSeam(Orientation ot, int[] edgeTo) {
		
		int[] seam = (ot == Orientation.VERTICAL) ? new int[height()] : new int[width()];

		int j = (ot == Orientation.VERTICAL) ? height() - 1 : width() - 1;
		
		int i = edgeTo[width()*height()+1];		
		while (i != 0) {
			seam[j--] = (ot == Orientation.VERTICAL) ? getx(i, ot) : gety(i, ot);
			i = edgeTo[i];
		}
		
		return seam;
	}

	public static void main(String[] args) {		
		
		Picture p = new Picture(args[0]);
		
		SeamCarver sc = new SeamCarver(p);
		double[][] energy = SCUtility.toEnergyMatrix(sc);
		
		int[] seam = sc.findVerticalSeam();
		for (int x : seam) {
			StdOut.printf("%d ", x);
		}
		StdOut.println();
		
		double sum = 0.0;
		for (int i = 0; i < sc.height(); i++) {
            for (int j = 0; j < sc.width(); j++) {
            	if (seam[i] == j) {
            		StdOut.printf("%4.2f* ", energy[j][i]);
            		sum += energy[j][i];
            	}
            	else
            		StdOut.printf("%4.2f ", energy[j][i]);            	
            }
            StdOut.println();            
		}
		StdOut.printf("Total energy: %f", sum);
        StdOut.println();
		
        seam = sc.findHorizontalSeam();
		for (int x : seam) {
			StdOut.printf("%d ", x);
		}
		StdOut.println();
        sum = 0.0;
		for (int i = 0; i < sc.height(); i++) {
            for (int j = 0; j < sc.width(); j++) {
            	if (seam[j] == i) {
            		StdOut.printf("%4.2f* ", energy[j][i]);
            		sum += energy[j][i];
            	}
            	else
            		StdOut.printf("%4.2f ", energy[j][i]);
            }
            StdOut.println();            
		}
		StdOut.printf("Total energy: %f", sum);
	}

}
