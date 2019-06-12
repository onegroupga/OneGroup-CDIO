package client;

import java.io.IOException;
import java.util.ArrayList;

import model.Ball;
import model.Coordinate;
import model.Goal;
import model.MapState;
import model.Route;
import model.Wall;

public class PathFinder {

	public static final int robotDiameter = 25; // The "diameter" of the robot - its thickness.
	public static final int shootSpace = 4; // The buffer distance we want between the robot and an edge.
	public static final int speedSlow = 10;
	public static final int speedFast = 500;
	public static final int sleepTime = 5; // Sleep time in seconds.
	
	private MapState mapState;
	
	
	Coordinate northWest;
	Coordinate northEast;
	Coordinate southWest;
	Coordinate southEast;
	Coordinate middleOfMap;

	// Create this PathFinder which will then find 4 distinct "quadrant
	// coordinates".
	// Also starts swallowing balls.
	public PathFinder(MapState mapState) {
		this.mapState = mapState;
		MainClient.pickUpBalls(true);
		calculateQuadrants(mapState);
		calculateGoalRobotLocations(mapState);
	}

	// We want to return route to a given ball.
	public Route getCalculatedRouteBall(MapState mapState, Ball ball) {
		Route route = new Route(mapState.robotLocation.coordinate, new ArrayList<Coordinate>());
		// This is where the magic happens.
		// First we find out which quadrant is nearest to the ball.
		Coordinate nearestToBall;
		nearestToBall = findNearestQuadrant(new Coordinate(ball.x, ball.y));
		// Now we find out which quadrant is nearest to the robot.
		Coordinate nearestToRobot;
		nearestToRobot = findNearestQuadrant(mapState.robotLocation.coordinate);
		// The first coordinate we go to is the one nearest to the robot.
		route.coordinates.add(new Coordinate(nearestToRobot.x, nearestToRobot.y));
		// Now we calculate a route between these two coordinates. A method has been
		// created, dedicated to finding a path between quadrants.
		route.coordinates.addAll(getRouteBetweenQuadrants(nearestToRobot, nearestToBall));
		// Now check if ball is near wall. If it isn't, then we end here.
		route.coordinates.add(new Coordinate(ball.x, ball.y));
		return route;
	}

	// We want to return routes to both goals.
	// TODO: Maybe we want to go to the "best" goal, always?
	public ArrayList<Route> getCalculatedRoutesGoals(MapState mapState) {
		ArrayList<Route> routes = new ArrayList<Route>();
		Route route;
		route = new Route(mapState.robotLocation.coordinate, new ArrayList<Coordinate>());
		// Now we find way to the goal's assigned "robotlocation" place.
		Coordinate nearestToRobot = findNearestQuadrant(mapState.robotLocation.coordinate);
		route.coordinates.add(nearestToRobot);
		Coordinate nearestToGoal = findNearestQuadrant(mapState.goal1.robotLocation.coordinate);
		route.coordinates.addAll(getRouteBetweenQuadrants(nearestToRobot, nearestToGoal));
		route.coordinates.add(mapState.goal1.robotLocation.coordinate);

		routes.add(route);

		route = new Route(mapState.robotLocation.coordinate, new ArrayList<Coordinate>());
		// Now we find way to the goal's assigned "robotlocation" place.
		nearestToRobot = findNearestQuadrant(mapState.robotLocation.coordinate);
		route.coordinates.add(nearestToRobot);
		nearestToGoal = findNearestQuadrant(mapState.goal2.robotLocation.coordinate);
		route.coordinates.addAll(getRouteBetweenQuadrants(nearestToRobot, nearestToGoal));
		route.coordinates.add(mapState.goal2.robotLocation.coordinate);

		routes.add(route);
		return routes;
	}

	// We want the robot to turn towards the goal, and then spit out balls.
	public void deliverBalls(MapState mapState) {
		int orientation1, orientation2;
		// Find the closest goal.
		Goal goal = null;
		if (calculateDistances(mapState.robotLocation.coordinate, mapState.goal1.coordinate1) > calculateDistances(
				mapState.robotLocation.coordinate, mapState.goal2.coordinate1)) {
			goal = mapState.goal1;
		} else {
			goal = mapState.goal2;
		}
		orientation1 = mapState.robotLocation.orientation;
		orientation2 = goal.robotLocation.orientation;
		MainClient.rotate(-orientation1);
		MainClient.rotate(orientation2);
		MainClient.pickUpBalls(false);
		// Wait for 5 seconds.
		try {
			Thread.sleep(sleepTime * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MainClient.pickUpBalls(true);
	}

	// 'Afstandsformlen' to calculate distance between two coordinates.
	public int calculateDistances(Coordinate coordinate1, Coordinate coordinate2) {
		return (int) Math.sqrt(Math.pow(coordinate1.x - coordinate2.x, 2) + Math.pow(coordinate1.y - coordinate2.y, 2));
	}

	// Gets a route between two quadrants. Has been created as a mess of if
	// statements - a manual deduction is the most effective.
	private ArrayList<Coordinate> getRouteBetweenQuadrants(Coordinate fromCoordinate, Coordinate toCoordinate) {
		ArrayList<Coordinate> output = new ArrayList<Coordinate>();
		// For northWest
		if (fromCoordinate.equals(northWest)) {
			if (toCoordinate.equals(northWest)) {
				// Do nothing.
			}
			if (toCoordinate.equals(northEast)) {
				output.add(northEast);
			}
			if (toCoordinate.equals(southEast)) {
				output.add(southWest);
				output.add(southEast);
			}
			if (toCoordinate.equals(southWest)) {
				output.add(southWest);
			}
		}
		// For northEast
		if (fromCoordinate.equals(northEast)) {
			if (toCoordinate.equals(northWest)) {
				output.add(northWest);
			}
			if (toCoordinate.equals(northEast)) {
				// Do nothing.
			}
			if (toCoordinate.equals(southEast)) {
				output.add(southEast);
			}
			if (toCoordinate.equals(southWest)) {
				output.add(southEast);
				output.add(southWest);
			}
		}
		// For SouthEast
		if (fromCoordinate.equals(southEast)) {
			if (toCoordinate.equals(northWest)) {
				output.add(northEast);
				output.add(northWest);
			}
			if (toCoordinate.equals(northEast)) {
				output.add(northEast);
			}
			if (toCoordinate.equals(southEast)) {
				// Do nothing.
			}
			if (toCoordinate.equals(southWest)) {
				output.add(southWest);
			}
		}
		// For southWest
		if (fromCoordinate.equals(southWest)) {
			if (toCoordinate.equals(northWest)) {
				output.add(northWest);
			}
			if (toCoordinate.equals(northEast)) {
				output.add(southEast);
				output.add(northEast);
			}
			if (toCoordinate.equals(southEast)) {
				output.add(southEast);
			}
			if (toCoordinate.equals(southWest)) {
				// Do nothing.
			}
		}

		return output;
	}

	// Creates RobotLocations for each of the two goals.
	private void calculateGoalRobotLocations(MapState mapState) {
		// Figure out which wall is left wall.
		if (mapState.goal1.coordinate1.x < mapState.wallList.get(1).upper.x) {
			// Then use hardcoded values to construct a robot location.
			mapState.goal1.robotLocation.orientation = 180;
			mapState.goal1.robotLocation.coordinate.x = mapState.goal1.coordinate1.x + (robotDiameter / 2 + shootSpace);
			mapState.goal1.robotLocation.coordinate.y = (mapState.goal1.coordinate1.y + mapState.goal1.coordinate2.y)
					/ 2;
			mapState.goal2.robotLocation.orientation = 0;
			mapState.goal2.robotLocation.coordinate.x = mapState.goal2.coordinate2.x - (robotDiameter / 2 + shootSpace);
			mapState.goal2.robotLocation.coordinate.y = (mapState.goal2.coordinate2.y + mapState.goal2.coordinate2.y)
					/ 2;
		} else {
			mapState.goal2.robotLocation.orientation = 180;
			mapState.goal2.robotLocation.coordinate.x = mapState.goal2.coordinate1.x + (robotDiameter / 2 + shootSpace);
			mapState.goal2.robotLocation.coordinate.y = (mapState.goal2.coordinate1.y + mapState.goal2.coordinate2.y)
					/ 2;
			mapState.goal1.robotLocation.orientation = 0;
			mapState.goal1.robotLocation.coordinate.x = mapState.goal1.coordinate2.x - (robotDiameter / 2 + shootSpace);
			mapState.goal1.robotLocation.coordinate.y = (mapState.goal1.coordinate2.y + mapState.goal1.coordinate2.y)
					/ 2;
		}
	}

	// Set the middle of the map and all quadrant points.
	private void calculateQuadrants(MapState mapState) {
		middleOfMap = new Coordinate(0, 0);
		middleOfMap.x = (mapState.cross.coordinate1.x + mapState.cross.coordinate2.x + mapState.cross.coordinate3.x
				+ mapState.cross.coordinate4.x) / 4;
		middleOfMap.y = (mapState.cross.coordinate1.y + mapState.cross.coordinate2.y + mapState.cross.coordinate3.y
				+ mapState.cross.coordinate4.y) / 4;
		// Before we can find quadrants, we gotta determine which wall is which.
		// TODO: If walls change, we gotta fix this part.
		Wall leftWall = new Wall();
		Wall rightWall = new Wall();
		// Figure out which wall is left wall.
		if (mapState.wallList.get(0).upper.x < mapState.wallList.get(1).upper.x) {
			leftWall = mapState.wallList.get(0);
			rightWall = mapState.wallList.get(1);
		} else {
			rightWall = mapState.wallList.get(0);
			leftWall = mapState.wallList.get(1);
		}
		// Let's set the four quadrant points. First northwest point - the middle of the
		// northwest quadrant.
		northWest = new Coordinate(0, 0);
		northWest.x = (leftWall.upper.x + middleOfMap.x) / 2;
		northWest.y = (leftWall.upper.y + middleOfMap.y) / 2;
		// Then the others.
		northEast = new Coordinate(0, 0);
		northEast.x = (rightWall.upper.x + middleOfMap.x) / 2;
		northEast.y = (rightWall.upper.y + middleOfMap.y) / 2;
		southWest = new Coordinate(0, 0);
		southWest.x = (leftWall.lower.x + middleOfMap.x) / 2;
		southWest.y = (leftWall.lower.y + middleOfMap.y) / 2;
		southEast = new Coordinate(0, 0);
		southEast.x = (rightWall.lower.x + middleOfMap.x) / 2;
		southEast.y = (rightWall.lower.y + middleOfMap.y) / 2;
	}

	// We find out which quadrant is closest to the requested ball.
	private Coordinate findNearestQuadrant(Coordinate coordinate) {
		Coordinate output = new Coordinate(0, 0);
		int compare;
		int minimum = calculateDistances(coordinate, northWest);
		output = northWest;
		// Now we compare against northEast.
		compare = calculateDistances(coordinate, northEast);
		if (compare < minimum) {
			minimum = compare;
			output = northEast;
		}
		// Now we compare against southWest.
		compare = calculateDistances(coordinate, southWest);
		if (compare < minimum) {
			minimum = compare;
			output = southWest;
		}
		// Now we compare against southEast.
		compare = calculateDistances(coordinate, southEast);
		if (compare < minimum) {
			minimum = compare;
			output = southEast;
		}
		return output;
	}

	// Play a sound.
	public void playSound(String sound) {
		switch (sound) {
		case "victory":
			MainClient.sendSound(2);
			break;
		case "ball":
			MainClient.sendSound(1);
			break;
		case "goal":
			break;
		default:
			break;
		}
	}

	public void pickUpMode(boolean swallow) {
		MainClient.pickUpBalls(swallow);
	}

	// We want the robot to drive a whole route.
	public void driveRoute(Route route, MapState mapState) {
		for (Coordinate coordinate : route.coordinates) {
			MainClient.sendCoordinate(coordinate, speedFast);
		}
	}

	public void swallowAndReverse(MapState mapState, Ball bestBall) {
		Coordinate tempCoordinate = mapState.robotLocation.coordinate;
		// Now we gotta calculate a coordinate about half the robot breadth away from the ball.
		Coordinate goToCoordinate = getPartwayCoordinate(mapState.robotLocation.coordinate, new Coordinate(bestBall.x, bestBall.y), robotDiameter/4);
		MainClient.sendCoordinate(goToCoordinate, speedSlow);
		
		MainClient.sendTravelDistanceBackwards(robotDiameter, speedSlow);
		MainClient.sendMotorSpeed(speedFast);
	}

	// Get's a new coordinate that is 'distance' away from coordinate 2, coming from coordinate 1.
	private Coordinate getPartwayCoordinate(Coordinate coordinate1, Coordinate coordinate2, int distance) {
		Coordinate output = new Coordinate(0, 0);
		output.x = coordinate2.x - ((robotDiameter/4)/calculateDistances(coordinate1, coordinate2))*(coordinate2.x-coordinate1.x);
		output.y = coordinate2.y - ((robotDiameter/4)/calculateDistances(coordinate1, coordinate2))*(coordinate2.y-coordinate1.y);
		return output;
	}

}
