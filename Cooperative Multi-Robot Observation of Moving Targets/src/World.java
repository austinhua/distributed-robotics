import java.util.*;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class World extends Application {
	public static final int numRobots = 3;
	public static final int numTargets = 6;
	public static final double WORLD_RAD = 100;
	
	public static final int ACCEL = 1;
	public static final int FPS = 10; // ticks per second
	public static final int SCREEN_LEN = 500;
	
	boolean started = false;
	long lastTime = 0;
	AnimationTimer timer = null;
	GraphicsContext gc = null;
	int seconds = 0;
	int[] targetsObserved = new int[120];
	
	List<Actor> actors = new ArrayList<Actor>();
	int[][] obsMatrix = new int[numRobots][numTargets];

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		init();
		
		primaryStage.setTitle("Assignment 1");
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
		
		for (int i = 0; i < numRobots; i++)
			actors.add(new Robot(i, obsMatrix));

		for (int i = 0; i < numTargets; i++)
			actors.add(new Target(i));

		timer = new AnimationTimer() {
			public void handle(long now) {
				if (now - lastTime >= 1e9 / FPS) {
					lastTime = now;
					updateAll();
					drawAll();
					
					// count the number of targets observed in a given moment
					if (seconds < 120) {
						int totalObserved = 0;
						for (int j = 0; j < obsMatrix[0].length; j++) {
							boolean observed = false;
							for (int i = 0; i < obsMatrix.length; i++) {
								if (obsMatrix[i][j] == 1) observed = true;
							}
							if (observed) totalObserved++;
						}
						targetsObserved[seconds] = totalObserved;
					}
					
					// average proportion of targets observed over 120 seconds
					if (seconds == 120) {
						int sum = 0;
						for(int i : targetsObserved) sum += i;
						System.out.println(sum/120./numTargets);
					}
					
					seconds++;
				}
			}
		};
		timer.start();
	}

	public void updateAll() {
		buildObsMatrix();
		for (Actor a : actors)
			a.tick(actors);
	}
	
	public void buildObsMatrix() {
		for (Actor a_r : actors) {
			if (a_r instanceof Robot) {
				for (Actor a_t : actors) {
					if (a_t instanceof Target) {
						if(a_r.getPoint().distance(a_t.getPoint()) <= Robot.DO3) {
							obsMatrix[a_r.getId()][a_t.getId()] = 1;
						}
						else {
							obsMatrix[a_r.getId()][a_t.getId()] = 0;
						}
					}
				}
			}
		}
	}

	public void drawAll() {
		if (gc == null)
			return;

		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, SCREEN_LEN, SCREEN_LEN);
		
		gc.strokeOval(0, 0, SCREEN_LEN, SCREEN_LEN);
		
		for (Actor a : actors)
			a.draw(gc, SCREEN_LEN / 2, SCREEN_LEN / 2, SCREEN_LEN / (WORLD_RAD * 2));
	}
}