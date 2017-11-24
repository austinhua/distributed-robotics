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
	public static final double WORLD_SIZE = 200;
	public static final double spawnProbability = .04;
	
	public static final int SCREEN_LEN = 800;
	public static final double dt = .2;
	public static final int FPS = 30; // dt per second
	
	boolean started = false;
	long lastTime = 0;
	AnimationTimer timer = null;
	GraphicsContext gc = null;
	double time = 0;
	
	List<Robot> robots = new ArrayList<Robot>();
	Environment env;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		init();
		
		primaryStage.setTitle("Assignment 3: Traffic Control");
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
		
		env = new Environment(robots);

		timer = new AnimationTimer() {
			public void handle(long now) {
				if (now - lastTime >= 1e9 / FPS) {
					lastTime = now;
					updateAll();
					drawAll();

//					if (Math.abs(time - 100) < 1e-9) env.printAverageDelay();
					
					time += dt;
				}
			}
		};
		timer.start();
	}

	public void updateAll() {
		env.tick(dt);
		for (Robot r : robots)
			r.tick(dt);
		if (Math.random() < spawnProbability) {
			double n = Math.random();
			if (n < .25) robots.add(new Robot(new Point2D(0, 101.5), Robot.MAX_SPEED, new Point2D(1, 0), env));
			else if (n < .5 ) robots.add(new Robot(new Point2D(200, 98.5), Robot.MAX_SPEED, new Point2D(-1, 0), env));
			else if (n < .75 ) robots.add(new Robot(new Point2D(98.5, 0), Robot.MAX_SPEED, new Point2D(0, 1), env));
			else robots.add(new Robot(new Point2D(101.5, 200), Robot.MAX_SPEED, new Point2D(0, -1), env));
		}
		env.removeRobots();
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
		
		// draw road		
		gc.setStroke(Color.BLACK);
		double a = 97*scale;
		double b = 103*scale;
		double c = 200*scale;
		
		gc.strokeLine(a, 0, a, a);
		gc.strokeLine(b, 0, b, a);
		gc.strokeLine(a, b, a, c);
		gc.strokeLine(b, b, b, c);

		gc.strokeLine(0, a, a, a);
		gc.strokeLine(0, b, a, b);
		gc.strokeLine(b, a, c, a);
		gc.strokeLine(b, b, c, b);
		
		
		gc.setStroke(env.isNSgreen()? Color.LIME : Color.RED);
		gc.strokeLine(a, a, b, a);
		gc.strokeLine(a, b, b, b);
		
		gc.setStroke(env.isEWgreen()? Color.LIME : Color.RED);
		gc.strokeLine(a, a, a, b);
		gc.strokeLine(b, a, b, b);
		
		for (Robot r : robots)
			r.draw(gc, scale);
	}
}