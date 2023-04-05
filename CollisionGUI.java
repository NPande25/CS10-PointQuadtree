import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 * @author nikhilpande
 * @author nathanmcallister
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else if (k=='t') {
			test();
		}
		else { // set the type for new blobs
			blobType = k;
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		// Ask all the blobs to draw themselves.
		g.setColor(Color.BLACK);
		if (blobs != null) {
			for (Blob blob : blobs) {
				g.fillOval((int)(blob.getX() - blob.getR()), (int)(blob.getY() - blob.getR()), (int)(2*blob.getR()), (int)(2*blob.getR()));
			}
		}
		// Ask the colliders to draw themselves in red.
		if (colliders != null) {
			for (Blob blob : colliders) {
				g.setColor(Color.RED);
				g.fillOval((int)(blob.getX() - blob.getR()), (int)(blob.getY() - blob.getR()), (int)(2*blob.getR()), (int)(2*blob.getR()));
				g.setColor(Color.BLACK);
			}
		}
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// TODO: YOUR CODE HERE
		// Create the tree
		PointQuadtree<Blob> blobTree = new PointQuadtree<>(blobs.get(0), 0, 0, 800, 600);
		for (int i = 1; i < blobs.size(); i++) {
			blobTree.insert(blobs.get(i));
		}

		colliders = new ArrayList<>();
		// For each blob, see if anybody else collided with it
		for (Blob blob : blobs) {
			List<Blob> temp = blobTree.findInCircle(blob.getX(), blob.getY(), blob.getR() * 2);
			temp.remove(blob);
			colliders.addAll(temp);
		}
	}

	private void test() {
		// two blobs that are collided
		blobs.add(new Blob(300, 300));
		blobs.add(new Blob(298, 302));

		// two blobs that are not
		blobs.add(new Blob(600, 400));
		blobs.add(new Blob(615, 400));

		// three in the same spot
		blobs.add(new Blob(300, 500));
		blobs.add(new Blob(305, 503));
		blobs.add(new Blob(303, 499));

		findColliders();
		if (colliders.size() == 8) { // 8 because there are duplicates when three or more overlap (each in the trio added twice)
			System.out.println("test passed!");
		}
		else System.out.println(colliders);
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}

