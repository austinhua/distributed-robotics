import java.util.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class World extends Application {
	public static final int numRobots = 25;
	public static final double WORLD_SIZE = 300;
	static final Point2D startPt = new Point2D(WORLD_SIZE/2 - 100, WORLD_SIZE/2 - 100);
	
	public static final int SCREEN_LEN = 800;
	public static final int ACCEL = 1;
	public static final int FPS = 50; // ticks per second
	
	boolean started = false;
	long lastTime = 0;
	AnimationTimer timer = null;
	GraphicsContext gc = null;
	int seconds = 0;
	
	List<Actor> actors = new ArrayList<Actor>();
	List<Robot> robots = new ArrayList<Robot>();
	Environment env;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		init();
		
		primaryStage.setTitle("Assignment 2: Collectivity");
		Group root = new Group();
		Canvas canvas = new Canvas(SCREEN_LEN, SCREEN_LEN);
		gc = canvas.getGraphicsContext2D();

		root.getChildren().add(canvas);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	public void init() {
		if(started)
			return;
		started = true;
		
		env = new Environment(robots, startPt);
		for (int i = 0; i < numRobots; i++) {
			Point2D robotStartPt = generateStartPt(startPt, 50);
			Robot r = new Robot(robotStartPt.getX(), robotStartPt.getY(), i, env);
			robots.add(r);
			actors.add(r);
		}

		timer = new AnimationTimer() {
			public void handle(long now) {
				if (now - lastTime >= 1e9 / FPS) {
					lastTime = now;
					updateAll();
					drawAll();
					
					seconds++;
				}
			}
		};
		timer.start();
	}

	public void updateAll() {
		env.updateCentroid();
		for (Actor a : actors)
			a.tick();
		env.collectStats(seconds);
	}
	
	public Point2D generateStartPt(Point2D startPt, double rad) {
		Point2D p;
		do {
			p = startPt.add( (Math.random()-.5) * rad, (Math.random()-.5) * rad);
		} while (startPt.distance(p) > rad);
		return p;
	}

	public void drawAll() {
		if (gc == null)
			return;

		double scale = SCREEN_LEN / WORLD_SIZE;
		
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, SCREEN_LEN, SCREEN_LEN);
		
		// draw start, turn, and goal pts
		double rad = 3 * scale;
		double lineDist = 200 * scale;
		double startX = startPt.getX() * scale;
		double startY = startPt.getY() * scale;
		
		gc.setFill(Color.BLACK);
		gc.strokeLine(startX, startY, startX + lineDist, startY);
		gc.strokeLine(startX + lineDist, startY, startX + lineDist, startY + lineDist);
		gc.fillOval(startX - rad, startY - rad, 2*rad, 2*rad);
		gc.fillOval(startX - rad + lineDist, startY - rad, 2*rad, 2*rad);
		gc.fillOval(startX - rad + lineDist, startY - rad + lineDist, 2*rad, 2*rad);

		// draw centroid
		gc.setFill(Color.YELLOW);
		gc.fillOval(env.getCentroid().getX() * scale - rad, env.getCentroid().getY() * scale - rad, 2*rad, 2*rad);
		
		for (Actor a : actors)
			a.draw(gc, scale);
	}
}