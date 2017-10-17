import java.util.*;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class Actor {
	double x;
	double y;
	Point2D p;
	
	private int id;

	public Actor(int id) {
		do {
			x = (Math.random() - 0.5) * World.WORLD_RAD * 2;
			y = (Math.random() - 0.5) * World.WORLD_RAD * 2;
		} while (new Point2D(x, y).magnitude() > World.WORLD_RAD);
		
		this.id = id;
	}

	public int getId() { return id; }
	
	public Point2D getPoint() { return new Point2D(x, y); }
	
	public Point2D getUnitVector(Point2D other) { return other.subtract(getPoint()).normalize(); }

	abstract public void draw(GraphicsContext gc, int xoff, int yoff, double scale);

	abstract public void tick(List<Actor> actors);
}
