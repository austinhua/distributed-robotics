import java.util.List;

import javafx.geometry.Point2D;

public class Environment {
	public static final double targetDist = 20;
	
	List<Robot> robots; 
	boolean turn = false;
	boolean goal = false;
	Point2D centroid, startPt, turnPt, goalPt;
	
	boolean printed = false;
	double[][] stats = new double[20][3];
	
	public Environment(List<Robot> robots, Point2D startPt) {
		this.robots = robots;
		this.startPt = startPt;
		this.turnPt = startPt.add(200, 0);
		this.goalPt = turnPt.add(0, 200);
		updateCentroid();
	}
	
	public void collectStats(int seconds) {
		int time_step = 50;
		if (seconds % time_step != 0 || seconds/time_step >= stats.length) return;
		int index = seconds/time_step;
		stats[index][0] = seconds;
		
		// calculate average robot dist
		double robotAvg = 0;
		for (Robot r : robots) {
			robotAvg += centroid.distance(r.getPoint());
		}
		robotAvg /= robots.size();
		stats[index][1] = robotAvg;
		
		// calculate average centroid dist
		double centroidDist = centroid.distance(this.getClosestPathPt(centroid));
		stats[index][2] = centroidDist;
		
		if (goal && !printed) {
			printed = true;
			for (int i = 0; i < stats.length; i++) {
				System.out.println("Time: " + stats[i][0] + ",\tAvg Robot Dist: " + stats[i][1] + ",\tAvg Centroid Dist: " + stats[i][2]);
//				System.out.printf("Time: %d,\tAvg Robot Dist: %d\t,Avg Centroid Dist: %d", stats[i][0], stats[i][1], stats[i][2]);
			}
		}
	}
	
	public List<Robot> getRobots() { return robots; }
	
	public void updateCentroid() {
		Point2D sum = new Point2D(0, 0);
		for (Robot r : robots) {
			sum = sum.add(r.getPoint());
		}
		centroid = sum.multiply(1./robots.size());
		
		if (centroid.distance(turnPt) <= 5) turn = true;
		if (centroid.distance(goalPt) <= 5) goal = true;
	}
	
	public Point2D getCentroid() {
		return centroid;
	}
	
	public Point2D getClosestPathPt(Point2D position) {
		double path1Y = startPt.getY();
		double path2X = turnPt.getX();
		double path1Dist = Math.abs(path1Y - position.getY());
		double path2Dist = Math.abs(path2X - position.getX());
		
		return (path1Dist > path2Dist || turn) ? new Point2D(path2X, position.getY()) : new Point2D(position.getX(), path1Y);
	}
	
	public Point2D getTargetPt() {
		Point2D offset = !turn ? new Point2D(targetDist, 0) : (!goal ? new Point2D(0, targetDist) : new Point2D(0, 0));
		return getClosestPathPt(getCentroid()).add(offset);
	}
	
}
