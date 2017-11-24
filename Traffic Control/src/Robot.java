import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Robot extends Actor {
	static final double SENSE_RANGE = 50;
	static final double STOP_RANGE = 2;
	static final double COMFORT_RANGE = 10;
	static final double MAX_SPEED = 20;
	static final double MAX_ACCEL = 10;

	static final double ROBOT_RADIUS = 1;
	static final Color ROBOT_COLOR = Color.BLUE;
	static final Color SENSE_COLOR = Color.rgb(0, 100, 255, 0.1);
	
	Environment env;
	double speed;
	Point2D dir;
	boolean pastLight = false;
	double time = 0;
	
	public Robot(Point2D p, double speed, Point2D dir, Environment env) {
		super(p, 0);
		this.env = env;
		this.speed = speed;
		this.dir = dir;
	}

	@Override
	public void tick(double dt) {
		time += dt;
		
		boolean inComfortZone = false;
		for (Robot r : env.getRobots()) {
			if (r != this && r.dir.equals(this.dir) && this.dir.dotProduct(this.p) < r.dir.dotProduct(r.p)) { 
				double dist = this.getPoint().distance(r.getPoint());
				if (dist <= STOP_RANGE) { this.speed = 0; inComfortZone = true; break; }
				if (dist <= COMFORT_RANGE) { this.speed -= Math.min(this.speed, dist/COMFORT_RANGE * dt); inComfortZone = true; }// decelerate 
			}
		}
		if (!inComfortZone && !pastLight) { // poll traffic light
			double time = env.getTimeTilNextRed(dir);
			double distRemaining = this.getPoint().distance(100, 100) - 4;
			double d = distRemaining;
			double s = this.speed;
			while (time > 0 && d > 0) { // check whether car can make it before the red light
				time -= dt;
				s += Math.min(MAX_ACCEL * dt, MAX_SPEED - s);
				d -= s * dt; 
			}
			if (time > 0) { // can make it, speed up
				this.speed += Math.min(MAX_ACCEL * dt, MAX_SPEED - this.speed);
			}
			else { // can't make it, stop before light
				double targetSpeed = distRemaining > 30? this.speed : (MAX_SPEED * distRemaining/30);
				if (targetSpeed < this.speed) this.speed -= Math.min(MAX_ACCEL * dt, this.speed);
			}
		}
		else if (pastLight) {
			this.speed += Math.min(MAX_ACCEL * dt, MAX_SPEED - this.speed);
		}
		
		this.setPoint( this.getPoint().add(this.dir.multiply(speed * dt)) );
		checkIfPastLight();
		if (p.getX() < 0 || p.getX() > 200 || p.getY() < 0 || p.getY() > 200 ) env.removeRobot(this);
	}

	void checkIfPastLight() {
		if (dir.equals(new Point2D(1, 0))) { this.pastLight = this.getPoint().getX() >= 98; }
		else if (dir.equals(new Point2D(-1, 0))) { this.pastLight = this.getPoint().getX() <= 102; }
		else if (dir.equals(new Point2D(0, 1))) { this.pastLight = this.getPoint().getY() >= 98; }
		else if (dir.equals(new Point2D(0, -1))) { this.pastLight = this.getPoint().getY() <= 102; }
		if (this.pastLight) {
			env.logTime(this.time);
		}
		
	}

	public double interpX(double u, double x1, double x2, double y1, double y2) {
		return (y2 - y1) / (x2 - x1) * (u - x1) + y1;
	}

	@Override
	public void draw(GraphicsContext gc, double scale) {
		gc.setFill(ROBOT_COLOR);

		double radius = Math.max(ROBOT_RADIUS * scale, 2);
		gc.fillOval(this.getPoint().getX() * scale - radius, this.getPoint().getY() * scale - radius, 2 * radius, 2 * radius);
	}

}
