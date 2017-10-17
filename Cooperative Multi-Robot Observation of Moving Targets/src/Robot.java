import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Robot extends Actor {
	static final double SENSE_RANGE = 30;
	static final double DO1 = 4;
	static final double DO2 = 25;
	static final double DO3 = 30;
	static final double DR1 = 12.5;
	static final double DR2 = 20;
	static final double MAX_SPEED = 2;

	static final double ROBOT_RADIUS = 2;
	static final Color ROBOT_COLOR = Color.DARKBLUE;
	static final Color SENSE_COLOR = Color.rgb(0, 100, 255, 0.1);
	
	private int[][] obsMatrix;

	public Robot(int id, int[][] obsMatrix) {
		super(id);
		this.obsMatrix = obsMatrix;
	}

	@Override
	public void tick(List<Actor> actors) {
		Point2D netForce = new Point2D(0, 0);

		for (Actor a : actors) {
			double dist = getPoint().distance(a.getPoint());

			if (a instanceof Target) {
				Point2D unitVector = this.getUnitVector(a.getPoint());
				Point2D f = unitVector.multiply(calcTargetForce(dist));
				
				double weight = 2;
				if (dist >= DO1 && dist <= SENSE_RANGE) {
					int otherObservers = 0;
					for (int i = 0; i < obsMatrix.length; i++) {
						if (i != this.getId() && obsMatrix[i][a.getId()] == 1) otherObservers++;
					}
					weight = weight * Math.exp(-otherObservers);
				}
				
				netForce = netForce.add(f.multiply(weight));
			}
			else if (a instanceof Robot && a.getId() != this.getId()) { 
				Point2D unitVector = this.getUnitVector(a.getPoint());
				Point2D g = unitVector.multiply(calcRobotForce(dist));
				netForce = netForce.add(g);
			}
		}
		
		if(netForce.magnitude() > MAX_SPEED) netForce = netForce.normalize().multiply(MAX_SPEED);
		
		x += netForce.getX();
		y += netForce.getY();

		// keep in bounds
		double theta = Math.atan2(y, x);
		double r = Math.sqrt(x * x + y * y);

		if (r > World.WORLD_RAD) {
			y = World.WORLD_RAD * Math.sin(theta);
			x = World.WORLD_RAD * Math.cos(theta);
		}
	}

	double calcRobotForce(double dist) {
		if (dist <= DR1)
			return -1;
		if (dist <= DR2)
			return interpX(dist, DR1, DR2, -1, 0);
		return 0;
	}

	double calcTargetForce(double dist) {
		if (dist <= DO1)
			return interpX(dist, 0, DO1, -1, 0);
		if (dist <= DO2)
			return interpX(dist, DO1, DO2, 0, 1);
		if (dist <= DO3)
			return 1;
		if (dist <= SENSE_RANGE)
			return interpX(dist, DO3, SENSE_RANGE, 1, 0);

		return 0;
	}

	public double interpX(double u, double x1, double x2, double y1, double y2) {
		return (y2 - y1) / (x2 - x1) * (u - x1) + y1;
	}

	@Override
	public void draw(GraphicsContext gc, int xoff, int yoff, double scale) {
		gc.setFill(ROBOT_COLOR);

		double radius = ROBOT_RADIUS * scale;
		gc.fillOval(x * scale + xoff - radius, y * scale + yoff - radius, 2 * radius, 2 * radius);

		double senseRadius = SENSE_RANGE * scale;
		gc.setFill(SENSE_COLOR);
		gc.fillOval(x * scale + xoff - senseRadius, y * scale + yoff - senseRadius, 2 * senseRadius, 2 * senseRadius);
	}

}
