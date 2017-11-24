import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

public class Environment {
	public static final double targetDist = 20;
	public static final double period = 10;
	public static final double switchTime = 1;
	
	List<Robot> robots; 
	
	boolean NSgreen, EWgreen;
	double time = 0;
	
	boolean printed = false;
	List<Double> delayTimes = new ArrayList<Double>();
	List<Robot> robotsToRemove = new ArrayList<Robot>();
	
	public Environment(List<Robot> robots) {
		this.robots = robots;
	}
	
	public void tick(double dt) {
		time += dt;
		double t = time % period;
		if (t < switchTime) { NSgreen = false; EWgreen = false; }
		else if (t < period / 2) { NSgreen = true; EWgreen = false; }
		else if (t < period / 2 + switchTime ) { NSgreen = false; EWgreen = false; }
		else if (t < period ) { NSgreen = false; EWgreen = true; }
	}
	
	public double getTimeTilNextRed(Point2D dir) {
		if (NSgreen == false && EWgreen == false) return 0;
		if (dir.equals(new Point2D(1,0)) || dir.equals(new Point2D(-1,0))) {
			if (EWgreen == false) return 0;
			return period - time % period;
		}
		else {
			if (NSgreen == false) return 0;
			return period / 2 - (time % period);
		}
	}
	
	public void logTime(double time) {
		delayTimes.add(time - 5);
	}
	
	public void removeRobot(Robot r) {
		robotsToRemove.add(r);
	}
	
	public void removeRobots() {
		for (Robot r : robotsToRemove) 
			robots.remove(r);
		robotsToRemove.clear();
	}
	
	public void printAverageDelay() {
		double sum = 0;
		for (Double d : delayTimes) {
			sum += d;
		}
		double averageDelay = sum/delayTimes.size();
		System.out.println("Delay: " + averageDelay);
	}
	
	public List<Robot> getRobots() { return robots; }
	
	public boolean isNSgreen() { return NSgreen; }

	public boolean isEWgreen() { return EWgreen; }
}
