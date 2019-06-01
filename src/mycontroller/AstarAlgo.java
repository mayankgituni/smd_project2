/**
 * The University of Melbourne
 * SWEN30006 Software Modelling and Design
 * FileName: MyAIController.java
 *
 *
 * This class is used handle the control of car
 * 
 * @author  Xiaoyu Sun
 * @StudentID 897097
 * @Username xsun4
 * @Date  18/10/2018 
 */

package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class AstarAlgo {
	private Node startNode;  // Node is the inner class, the code is located at the end
	private final static int ACTION_COST = 1; // make all the cost of each action equal to 1

	public AstarAlgo(Coordinate coordinate, Direction currentOrientation) {
		startNode = new Node(coordinate, currentOrientation);
	}
	
	/*
	 * This is the A* algorithm which used to find path
	 * 
	 * The algorithm finds a list of coordinate from start state to goal state
	 * 
	 * @return an arrayList<Coordinate> which contains a list of Coordinate to goal state
	 * 
	 */
	public ArrayList<Coordinate> AstarPathFinding() {
		PriorityQueue<Node> openList = new PriorityQueue<>(); // The data structure for openlist is priority queue
		ArrayList<Coordinate> closedList = new ArrayList<Coordinate>(); // data structure for open-list
		openList.add(startNode);
		if (isGoal(startNode.getCoordinate())) {
			ArrayList<Coordinate> stop = new ArrayList<Coordinate>();
			stop.add(startNode.getCoordinate());
			return stop;
		}
		while (!openList.isEmpty()) {
			Node currentNode = openList.remove();
			int currentCost = currentNode.getG();
			ArrayList<Coordinate> currentPathToTheNode = currentNode.getpathTotheNode();
			if (!closedList.contains(currentNode.getCoordinate())) {
				closedList.add(currentNode.getCoordinate());
				if (isGoal(currentNode.getCoordinate())) {
					return currentPathToTheNode;
				}
				ArrayList<Node> successors = getSuccessor(currentNode);
				if (successors.size() > 0) {
					for (Node successorNode : successors) {
						int g = currentNode.getG() + ACTION_COST;
						int h = heuristic(successorNode.getCoordinate());
						if (!closedList.contains(successorNode.getCoordinate())) {
							currentPathToTheNode = currentNode.getpathTotheNode(); // get an new deep copy of array
							currentPathToTheNode.add(successorNode.getCoordinate());
							int f = g + h;
							successorNode.update(g, f, currentPathToTheNode);
							openList.add(successorNode);
						}
					}

				}
			}

		}
		return new ArrayList<Coordinate>();
	}
	
	
	/*
	 * This is helper method for A* algorithm to find successor of certain node
	 * 
	 * For each Legal actions, only if next coordinate is not wall or mud, it would
	 * be defined as successor
	 * 
	 * @param node: an object of inner class Node which contains current Direction and coordinate
	 * @return arralist<Node> that contains all the successor of the node
	 */
	
	private ArrayList<Node> getSuccessor(Node node) {
		Coordinate currentCoordinate = node.coordinate;
		int x = currentCoordinate.x;
		int y = currentCoordinate.y;
		Direction currentOrientation = node.currentOrientation;
		MapTile currentTile = ExploreMap.getInstance().getExploredMap().get(currentCoordinate).getMapTile();
		ArrayList<Node> successor = new ArrayList<Node>();
		ArrayList<Direction> legalActions = getLegalDirections(node);

		for (Direction direction : legalActions) {
			Coordinate successorCoordinate;
			switch (direction) {
			case NORTH:
				successorCoordinate = new Coordinate(x, y + 1);
				break;
			case SOUTH:
				successorCoordinate = new Coordinate(x, y - 1);
				break;
			case EAST:
				successorCoordinate = new Coordinate(x + 1, y);
				break;
			case WEST:
				successorCoordinate = new Coordinate(x - 1, y);
				break;
			default:
				successorCoordinate = new Coordinate(x, y); // should never happen
			}
			if (successorCoordinate.x < 0 || successorCoordinate.y < 0) {
				continue;
			}
			MapTile successorTile = ExploreMap.getInstance().getExploredMap().get(successorCoordinate).getMapTile();
			if (!successorTile.isType(MapTile.Type.WALL)) {
				if (successorTile.isType(MapTile.Type.TRAP) && ((TrapTile) successorTile).getTrap().equals("mud")) {
					continue; // if MapTile type is mud, then it can not be added into successor
				}
				Node successorNode = new Node(successorCoordinate, direction);
				successor.add(successorNode);
			}
		}

		return successor;

	}

	/*
	 * This is helper method for get successor method
	 * 
	 * If the current maptile is road, the arraylist contains four direction -w,n,s,e
	 * If the current maptile is grass/start/healthTrap, the legal actions are 
	 * current-direction & reverse-direction
	 * 
	 * @param  node  an object of inner class Node which contains current Direction and coordinate
	 * @return arrayList<Directiom> that contains for legal actions
	 */
	private ArrayList<Direction> getLegalDirections(Node node) {
		ArrayList<Direction> legalActions = new ArrayList<Direction>();
		Coordinate currentCoordinate = node.coordinate;
		Direction currentOrientation = node.currentOrientation;
		MapTile currentTile = ExploreMap.getInstance().getExploredMap().get(currentCoordinate).getMapTile();
		// if the MapTile type is grass, then the legal action would only be current
		// action/ reverse action
		if (currentTile.isType(MapTile.Type.TRAP) && ((TrapTile) currentTile).getTrap().equals("grass") ||
				currentTile.isType(MapTile.Type.START) ||
				currentTile.isType(MapTile.Type.TRAP) && ((TrapTile) currentTile).getTrap().equals("health")
				) {
			legalActions.add(currentOrientation);
			legalActions.add(WorldSpatial.reverseDirection(currentOrientation));
			return legalActions;

		}
		legalActions.add(Direction.SOUTH);
		legalActions.add(Direction.NORTH);
		legalActions.add(Direction.EAST);
		legalActions.add(Direction.WEST);
		return legalActions;
	}

	/*
	 * This is the heuristic function
	 * 
	 * to set the heuristic, the a* algorithm would try to avoid dangerous trap 
	 * as much as possible
	 * 
	 * @param  coordinate
	 * @return if the map tile of current coordinate is 100, then 
	 */
	public int heuristic(Coordinate coordinate) {
		MapTile currentTile = ExploreMap.getInstance().getExploredMap().get(coordinate).getMapTile();
		if (currentTile.isType(MapTile.Type.TRAP) && ((TrapTile) currentTile).getTrap().equals("lava"))
			return 100; // if that's Lava
		return 0;
	}

	/*
	 * This the abstract method that is supposed to be override by descender class
	 * 
	 * @param  coordinate of a node
	 * 
	 * @return true if the coordinate is the goal
	 */
	public boolean isGoal(Coordinate coordinate){
		MapTile currentTile = ExploreMap.getInstance().getExploredMap().get(coordinate).getMapTile();
		if(currentTile.isType(MapTile.Type.FINISH))
			return true;
		else
			return false;
	}

	
	/*
	 * This the inner class which defines the node, which mainly used to ease 
	 * the implementation of A* algorithm
	 * 
	 */
	class Node implements Comparable<Node> {
		private Coordinate coordinate; // the coordinate of the node
		private Direction currentOrientation; // the direction that reaches the node
		private ArrayList<Coordinate> pathTotheNode = new ArrayList<Coordinate>();
		private int g = 0;// the total action cost
		private int f = 0;// the total f value where f= g + h

		Node(Coordinate coordinate, Direction currentOrientation) {
			this.coordinate = coordinate;
			this.currentOrientation = currentOrientation;
		}
		Node(Coordinate coordinate, Direction currentOrientation, int g, int f) {
			this.coordinate = coordinate;
			this.currentOrientation = currentOrientation;
			this.g = g;
			this.f = f;
		}
		// return a deep copy of arrayList of coordinate
		ArrayList<Coordinate> getpathTotheNode() {
			ArrayList<Coordinate> temp = new ArrayList<Coordinate>();
			if (pathTotheNode.size() > 0) {
				for (Coordinate coordinate : pathTotheNode) {
					temp.add(coordinate);
				}
			}
			return temp;
		}

		void update(int g, int f, ArrayList<Coordinate> pathTotheNode) {
			this.g = g;
			this.f = f;
			this.pathTotheNode = pathTotheNode;

		}

		int getF() {
			return f;
		}

		int getG() {
			return g;
		}

		Coordinate getCoordinate() {
			return coordinate;
		}

		Direction getCurrentDirection() {
			return currentOrientation;
		}

		// The compareTo method is used to help the implement of PriorityQueue<Node>
		@Override
		public int compareTo(Node node) {
			if (this.getF() > node.getF()) {
				return 1;
			} else if (this.getF() < node.getF()) {
				return -1;
			} else {
				return 0;
			}
		}

	}

}
