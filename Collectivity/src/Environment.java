import java.util.List;

import javafx.geometry.Point2D;

public class Environment {
	public static final double targetDist = 20;
	
	List<Robot> robots; 
	boolean turn = false;
	boolean goal = false;
	Point2D centroid, startPt, turnPt, goalPt;
	
	public Environment(List<Robot> robots, Point2D startPt) {
		this.robots = robots;
		this.startPt = startPt;
		this.turnPt = startPt.add(200, 0);
		this.goalPt = turnPt.add(0, 200);
		updateCentroid();
	}
	
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
