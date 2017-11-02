import java.util.*;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class Actor {
	double x;
	double y;
	Point2D p;
	
	private int id;

	public Actor(double x, double y, int id) {
		this.x = x;
		this.y = y;
		
		this.id = id;
	}

	public int getId() { return id; }
	
	public Point2D getPoint() { return new Point2D(x, y); }
	
	public Point2D getUnitVector(Point2D other) { return other.subtract(getPoint()).normalize(); }

	abstract public void draw(GraphicsContext gc, double scale);

	abstract public void tick();
}
