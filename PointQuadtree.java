import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 * @author nikhilpande
 * @author nathanmcallister
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children



	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters

	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		int x = (int) p2.getX(); // location of point you are inserting
		int y = (int) p2.getY();
		int xc = (int) point.getX(); // location of parent point
		int yc = (int) point.getY();
		int quadrant;

		if (x >= xc && y < yc) {
			quadrant = 1;
			if (hasChild(quadrant)) { c1.insert(p2); } // if it has child, recurse for that child
			else { c1 = new PointQuadtree<>(p2, xc, y1, x2, yc); } // if not, it is just a child of the current point
		}
		else if (x < xc && y <= yc) {
			quadrant = 2;
			if (hasChild(quadrant)) { c2.insert(p2); }
			else { c2 = new PointQuadtree<>(p2, x1, y1, xc, yc); }
		}
		else if (x <= xc && y > yc) {
			quadrant = 3;
			if (hasChild(quadrant)) { c3.insert(p2); }
			else { c3 = new PointQuadtree<>(p2, x1, yc, xc, y2); }
		}
		else if (x > xc && y >= yc) {
			quadrant = 4;
			if (hasChild(quadrant)) { c4.insert(p2); }
			else { c4 = new PointQuadtree<>(p2, xc, yc, x2, y2); }
		}
	}

	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE
		// create list of all the points in the tree (through allPoints method) and return its size
		return allPoints().size();
	}

	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		List<E> points = new ArrayList<>();
		addPoints(points);
		return points;
	}


	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		// method finds all the points in an inputted circle
		List<E> pointsFound = new ArrayList<E>();
		pointsFound = findInCircleHelper(pointsFound, cx, cy, cr); // call the helper method
		return pointsFound;
	}

	// TODO: YOUR CODE HERE for any helper methods.

	/**
	 * helper method for allPoints. Takes the list of points, adds the current point, and recursively calls for each child
	 * @param pointsList list of E objects to be filled
	 */
	private void addPoints(List<E> pointsList) {
		pointsList.add(point); // add current point
		// recursively call for each child, if one exists in the quadrant
		if (hasChild(1)) c1.addPoints(pointsList);
		if (hasChild(2)) c2.addPoints(pointsList);
		if (hasChild(3)) c3.addPoints(pointsList);
		if (hasChild(4)) c4.addPoints(pointsList);
	}

	/**
	 * helper method for findInCircle. Takes the list of points, adds the current point, and recursively calls for each child
	 * @param points list of E objects to be filled
	 * @param cx 	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 */
	private List<E> findInCircleHelper(List<E> points, double cx, double cy, double cr) {
			// helper method to find points in a given circle
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) { // if the inputted circle intersects current rectangle
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) { // also check if current point is in inputted circle
				points.add(point); // if so, add point to the list
			}
			for (int i=1; i <= 4; i++) { // for the points whose rectangles the circles lie on, recurse to the children
				if(hasChild(i)) getChild(i).findInCircleHelper(points, cx, cy, cr);
			}
		}

		return points;
	}


}

