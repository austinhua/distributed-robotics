import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Robot extends Actor {
	static final double SENSE_RANGE = 50;
	static final double COMFORT_RANGE = 5;
	
	static final double MAX_SPEED = 3;

	static final double ROBOT_RADIUS = .25;
	static final Color ROBOT_COLOR = Color.DARKBLUE;
	static final Color SENSE_COLOR = Color.rgb(0, 100, 255, 0.1);
	
	private Environment env;

	public Robot(double x, double y, int id, Environment env) {
		super(x, y, id);
		this.env = env;
	}

	@Override
	public void tick() {
		Point2D netForce = new Point2D(0, 0);
		
		Point2D targetDir = getUnitVector(env.getTargetPt());
		
		boolean inComfortZone = false;
		for (Robot r : env.getRobots()) {
			if (r.getId() != this.getId()) { 
				double dist = getPoint().distance(r.getPoint());
				if (dist <= COMFORT_RANGE) inComfortZone = true;
				
				Point2D unitVector = this.getUnitVector(r.getPoint());
				Point2D g = unitVector.multiply(calcRobotRepulsion(dist));
				netForce = netForce.add(g);
			}
		}
		netForce = netForce.normalize();
		if (!inComfortZone) {
			netForce = netForce.add(targetDir.multiply(COMFORT_RANGE/2));
		}
		
		
//		if(netForce.magnitude() > MAX_SPEED) netForce = netForce.normalize().multiply(MAX_SPEED);
		
		x += netForce.getX();
		y += netForce.getY();

		// keep in bounds
		if (x < 0) x = 0; 
		if (x > World.WORLD_SIZE) x = World.WORLD_SIZE;
		if (y < 0) y = 0;
		if (y > World.WORLD_SIZE) y = World.WORLD_SIZE;
		
//		for (Actor a : actors) {
//		double dist = getPoint().distance(a.getPoint());
//
//		if (a instanceof Target) {
//			Point2D unitVector = this.getUnitVector(a.getPoint());
//			Point2D f = unitVector.multiply(calcTargetForce(dist));
//			
//			double weight = 2;
//			if (dist >= DO1 && dist <= SENSE_RANGE) {
//				int otherObservers = 0;
//				for (int i = 0; i < obsMatrix.length; i++) {
//					if (i != this.getId() && obsMatrix[i][a.getId()] == 1) otherObservers++;
//				}
//				weight = weight * Math.exp(-otherObservers);
//			}
//			
//			netForce = netForce.add(f.multiply(weight));
//		}
//		else if (a instanceof Robot && a.getId() != this.getId()) { 
//			Point2D unitVector = this.getUnitVector(a.getPoint());
//			Point2D g = unitVector.multiply(calcRobotForce(dist));
//			netForce = netForce.add(g);
//		}
	}

	double calcRobotRepulsion(double dist) {
		if (dist >= SENSE_RANGE)
			return 0;
		return -Math.exp(-dist);
	}

	public double interpX(double u, double x1, double x2, double y1, double y2) {
		return (y2 - y1) / (x2 - x1) * (u - x1) + y1;
	}

	@Override
	public void draw(GraphicsContext gc, double scale) {
		gc.setFill(ROBOT_COLOR);

		double radius = Math.max(ROBOT_RADIUS * scale, 2);
		gc.fillOval(x * scale - radius, y * scale - radius, 2 * radius, 2 * radius);

//		double senseRadius = SENSE_RANGE * scale;
//		gc.setFill(SENSE_COLOR);
//		gc.fillOval(x * scale - senseRadius, y * scale - senseRadius, 2 * senseRadius, 2 * senseRadius);
	}

}
