package mycontroller;

import controller.CarController;
import tiles.LavaTrap;
import tiles.TrapTile;
import world.Car;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.*;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class MyAutoController extends CarController{		
		// How many minimum units the wall is away from the player.
		private int wallSensitivity = 1;
		
		private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.
		
		// Car Speed to move at
		private final int CAR_MAX_SPEED = 1;

		public MyAutoController(Car car) {
			super(car);
			ParcelLocation = new ArrayList<Coordinate>();

		}


		private final int FIRST_STEP = 0;

		public ArrayList<Coordinate> ParcelLocation;

		// Coordinate initialGuess;
		// boolean notSouth = true;
		@Override
		public void update() {
			// Gets what the car can see
			HashMap<Coordinate, MapTile> currentView = getView();


			//update ParcelLocation with currentView
			findParcels(getView());
			ExploreMap.getInstance().updateMap(getView());

			RoutingStrategy routingStrategy = new RoutingStrategy(new Coordinate(getPosition()), getOrientation());
			//find the path using the chosen Strategy
			ArrayList<Coordinate> path = routingStrategy.AstarPathFinding();


			//drive 1 step
			drive(new Coordinate(getPosition()), path.get(FIRST_STEP));


		}


	//drive from start coordinate to goal coordinate
	private void drive(Coordinate start, Coordinate goal) {
		int startX =  start.x;
		int startY = start.y;
		int goalX = goal.x;
		int goalY = goal.y;
		if (goalX == startX + 1) {
			goEast();
		}
		else if (goalX == startX - 1) {
			goWest();
		}
		else if(goalY == startY+ 1) {
			goNorth();
		}else if (goalY == startY- 1) {
			goSouth();
		}else {
			applyBrake();
		}

	}


	private void goSouth() {
		Direction currentOrientation = getOrientation();
		switch(currentOrientation){
			case EAST:
				turnRight();
				break;
			case NORTH:
				applyReverseAcceleration();
				break;
			case SOUTH:
				applyForwardAcceleration();
				break;
			case WEST:
				turnLeft();
				break;
			default:
				applyBrake();
				break;
		}
	}

	private void goNorth() {
		Direction currentOrientation = getOrientation();
		switch(currentOrientation){
			case EAST:
				turnLeft();
				break;
			case NORTH:
				applyForwardAcceleration();
				break;
			case SOUTH:
				applyReverseAcceleration();
				break;
			case WEST:
				turnRight();
				break;
			default:
				applyBrake();
				break;
		}

	}

	private void goEast() {
		Direction currentOrientation = getOrientation();
		switch(currentOrientation){
			case EAST:
				applyForwardAcceleration();
				break;
			case NORTH:
				turnRight();
				break;
			case SOUTH:
				turnLeft();
				break;
			case WEST:
				applyReverseAcceleration();
				break;
			default:
				applyBrake();
				break;
		}

	}

	private void goWest() {
		Direction currentOrientation = getOrientation();
		switch(currentOrientation){
			case EAST:
				applyReverseAcceleration();
				break;
			case NORTH:
				turnLeft();
				break;
			case SOUTH:
				turnRight();
				break;
			case WEST:
				applyForwardAcceleration();
				break;
			default:
				applyBrake();
				break;
		}

	}



	//find new key in currentView(if any) and record its coordinate
	private void findParcels(HashMap<Coordinate, MapTile> currentView) {
		Iterator iter = currentView.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Coordinate coordinate = (Coordinate) entry.getKey();
			MapTile mapTile = (MapTile) entry.getValue();
			if (mapTile instanceof TrapTile) {
				TrapTile Parcel = (TrapTile) mapTile;
				String trap = Parcel.getTrap();
				if((trap != null) && (trap.contains("parcel"))){
					ParcelLocation.add(coordinate);
				}
			}
		}
	}
		
	}
