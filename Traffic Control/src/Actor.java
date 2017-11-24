import java.util.*;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class Actor {
	Point2D p;
	
	private int id;

	public Actor(Point2D p, int id) {
		this.p = p;
		
		this.id = id;
	}

	public int getId() { return id; }
	
	public Point2D getPoint() { return p; }
	public void setPoint(Point2D p) { this.p = p; }
	
	public Point2D getUnitVector(Point2D other) { return other.subtract(getPoint()).normalize(); }

	abstract public void draw(GraphicsContext gc, double scale);

	abstract public void tick(double dt);
}
