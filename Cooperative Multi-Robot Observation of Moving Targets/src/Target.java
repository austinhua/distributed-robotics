import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Target extends Actor {
	public static final Color TARGET_COLOR = Color.RED;
	public static final double TARGET_RADIUS = 2;
	
	public static final double PATH_LENGTH = 50;
	public static final double MAX_SPEED = 1.5;
	
	double angle = 0;
	double pathRemaining = 0;
	double v = 0;
	
	public Target(int id) {
		super(id);
		v = Math.random() * MAX_SPEED;
	}


	@Override
	public void tick(List<Actor> actors) {
		if(pathRemaining <= 0) {
			pathRemaining = Math.random() * PATH_LENGTH;
			angle = Math.random() * 2 * Math.PI;
		}

		x += v * Math.cos(angle);
		y += v * Math.sin(angle);
		
		pathRemaining -= v;
		
		// keep in bounds
		double theta = Math.atan2(y, x);
		double r = Math.sqrt(x * x + y * y);
		
		if(r > World.WORLD_RAD) {
			y = World.WORLD_RAD * Math.sin(theta);
			x = World.WORLD_RAD * Math.cos(theta);
			angle += Math.PI;
		}
	}

	@Override
	public void draw(GraphicsContext gc, int xoff, int yoff, double scale) {
		gc.setFill(TARGET_COLOR);
		double radius = TARGET_RADIUS * scale;
		gc.fillOval(x * scale + xoff - radius, y * scale + yoff - radius, radius * 2, radius * 2);
	}

}
