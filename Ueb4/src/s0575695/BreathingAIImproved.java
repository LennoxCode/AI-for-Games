package s0575695;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

public class BreathingAIImproved extends AI {

	int currPearlIndex;
	int currScore;
	//TODO: reflexPoints can be outside of level bounds. calculate first if a point is bigger than the height or is lowere than zero;
	//TODO: Refractor seekPearl method to not override currPearl but just calculate way
	Point currPearl;
	ArrayList<Point> remainingPearls;
	ArrayList<Point> unreachablePearls;
	Vector currDirection;
	List<Point2D> currPath;
	ArrayList<Point2D> reflexCorners;
	ArrayList<Point2D> airPoints;
	Point2D currTarget;
	GraphNode currNode;
	Graph graphy;
	List<Point2D> aStarPath;
	Point2D startPos;
	private State currState;
	public BreathingAIImproved(Info info) {
		super(info);
		currState = State.seekingPearl;
		currPearlIndex = 0;
		currScore = 0;
		constructGraph();
		remainingPearls = new ArrayList<>();
		unreachablePearls = new ArrayList<>();
		airPoints = new ArrayList<>();
		for(Point point : info.getScene().getPearl()) {
			if(info.getMaxVelocity() / info.getMaxAir() <= point.y * 2)remainingPearls.add(point);
			else unreachablePearls.add(point);
		}
		info.getScene().getRecyclingProducts();
		
		if(remainingPearls.size() == 0) {
			remainingPearls = unreachablePearls;
			unreachablePearls = null;
		}
		currDirection = new Vector(0, 0); 
		//System.out.println(info.getScene().getHeight());
		enlistForTournament(575695);
		GraphNode testa = new GraphNode(new Point2D.Float(info.getX(), info.getY()), reflexCorners);
		currNode = testa;
		//System.out.println(info.getMaxAir());
		Point2D currPos = new Point2D.Float(info.getX(), info.getY());
		for(int i = 0; i < reflexCorners.size(); i++) {
			for(int f = i; f < reflexCorners.size(); f++) {
				if(reflexCorners.get(i).distance(reflexCorners.get(f)) < 15) reflexCorners.remove(f);
			}
		}
		
		int width = info.getScene().getWidth();
		for(int i = 0; i < width ; i+=40 ) {
			Point2D airPoint = new Point(i, 0);
			reflexCorners.add(airPoint);
			airPoints.add(airPoint);
		}
	
		//System.out.println(info.getMaxVelocity());
		//System.out.println("prev calc: " + info.getMaxVelocity() > info.getAir());
		//System.out.println("amount of unreachable pearls new : " + unreachablePearls.size());
		currPearl = getClosestPoint(new Point(0, info.getScene().getHeight() /2));
		for(Point2D point1 : info.getScene().getPearl())reflexCorners.add(point1);
		Graph graph = new Graph(reflexCorners);
		GraphNode node = new GraphNode(new Point.Double(currPearl.getX(), 0), reflexCorners);
		Area obstacleArea = new Area();
		for(Path2D path : info.getScene().getObstacles()) 
			obstacleArea.add(new Area(path.createTransformedShape(new AffineTransform())));
	
		graphy = graph;
		node.addOneWayTransitions(graphy.nodes, obstacleArea);
		List<Point2D> path = graph.constructPathAStar(node, currPearl);
		
		startPos = currPos;
		if(path != null) {
			//System.out.println(path.size());
			aStarPath = path;
			currTarget = path.get(path.size() - 1);
			
		}else {
			//System.out.println("did not find path");
			ArrayList<Point> removedPearls = new ArrayList<>();
			
			while(path == null && remainingPearls.size() > 1) {
				removedPearls.add(currPearl);
				remainingPearls.remove(currPearl);
				currPearl = getClosestPoint(new Point((int)currPos.getX(),(int) currPos.getY()));
				path = graphy.constructPathAStar(node, currPearl);
				
			}
			remainingPearls.addAll(removedPearls);
			if(path != null) {
				//System.out.println(path.size());
				aStarPath = path;
				currTarget = path.get(path.size() - 1);
			}
		}
	
		
	}
	
	@Override
	public String getName() {
		return "Leonard";
	}

	@Override
	public Color getPrimaryColor() {
		return Color.RED;
	}

	@Override
	public Color getSecondaryColor() {
		return Color.BLACK;
	}
	@Override
	public void drawDebugStuff(Graphics2D gfx) {
//		if(aStarPath != null) {
//			gfx.setColor(Color.red);
//			for(Point2D pointy : aStarPath) {
//				gfx.drawOval((int)pointy.getX(), (int)pointy.getY(), 5, 5);
//			}
//		}else {
//			//System.out.println("no path right now");
//		}
//		gfx.setColor(Color.green);
//		for(Point2D point : reflexCorners) {
//			gfx.drawOval((int)point.getX(), (int)point.getY(), 5, 5);
//		}
//		gfx.setColor(Color.red);
//		GraphNode base = graphy.nodes.get(33);
//		if(aStarPath != null) {
//			for(int i = 0; i < aStarPath.size() - 1; i++) {
//				Point2D point1 = aStarPath.get(i);
//				Point2D point2 = aStarPath.get(i+1);
//				gfx.drawLine((int) point1.getX(),(int) point1.getY(), (int) point2.getX(),(int) point2.getY());
//			}
//		}
//		//gfx.drawOval((int)base.point.getX(), (int)base.point.getY(), 5, 5);
//		gfx.setColor(Color.red);
//		for(GraphNode to: base.transitions) {
//			//gfx.drawLine((int) base.point.getX(), (int) base.point.getY(),(int)  to.point.getX(),(int)  to.point.getY());
//		}
//		Point2D postion = currNode.point;
//		GraphNode test = graphy.getGraphNode(currPearl);
//		Point2D testPos = test.point;
//		for(Point2D edge : test.edges) {
//			gfx.drawLine((int)testPos.getX(), (int)testPos.getY(), (int)edge.getX(), (int)edge.getY());
//		}
		
	}
	@Override
	public PlayerAction update() {
		
		//Point[] pearls = info.getScene().getPearl();
		Point position = new Point((int)info.getX(),(int) info.getY());
		if(info.getScore() > currScore) {
			currScore = info.getScore();
			removePearl(position);
			if(remainingPearls.size() == 0 && unreachablePearls != null) {
				remainingPearls = unreachablePearls;
				unreachablePearls = null;
				currPearl = getBestPearlPocket(position);
			}else currPearl = getClosestPoint(position);
			
			
			
			//seekNextPearl(position);
			Point2D nearestAir = getNearestAirPoint(currPearl);
			Point2D currPos = new Point2D.Float(info.getX(), info.getY());
			GraphNode node = new GraphNode(currPos, reflexCorners);
			Area obstacleArea = new Area();
			for(Path2D path : info.getScene().getObstacles()) 
				obstacleArea.add(new Area(path.createTransformedShape(new AffineTransform())));

			
			node.addOneWayTransitions(graphy.nodes, obstacleArea);
			List<Point2D> path = graphy.constructPathAStar(node, currPearl);
			int yFactor = currPearl.y;
			if(currScore == 9)yFactor = 0;
			if(calcPathLenght(path) + yFactor >  info.getAir() / info.getMaxVelocity() ){
				path = graphy.constructPathAStar(node, nearestAir);
				if(calcPathLenght(path) >info.getAir() / info.getMaxVelocity()  ) {
					System.out.println("path too long");
					nearestAir = getNearestAirPoint(position);
					path = graphy.constructPathAStar(node, nearestAir);
				}
				if(currState == State.SuicideCharge) path = graphy.constructPathAStar(node, currPearl);
				else currState = State.SeekingAir;
				aStarPath = path;
				currTarget = path.get(path.size() - 1);
				//currState = State.SeekingAir;
			}else {
				aStarPath = path;
				currTarget = path.get(path.size() - 1);
			}
		
		}
		if(info.getAir() == info.getMaxAir() && currState == State.SeekingAir || (position.distance(currTarget) < 2 && aStarPath.size() == 1  && currState == State.movingAlongSurface)) {
		
			//currState = State.seekingPearl;
			seekNextPearl(position);
			if(calcPathLenght(aStarPath) > 0.6 * info.getScene().getHeight() && currState != State.movingAlongSurface ) {
				Point2D nearestAir = getNearestAirPoint(new Point(currPearl.x, 0));
				Point2D currPos = new Point2D.Float(info.getX(), info.getY());
				GraphNode node = new GraphNode(currPos, reflexCorners);
				Area obstacleArea = new Area();
				for(Path2D path : info.getScene().getObstacles()) 
					obstacleArea.add(new Area(path.createTransformedShape(new AffineTransform())));

				
				node.addOneWayTransitions(graphy.nodes, obstacleArea);
				List<Point2D> path = graphy.constructPathAStar(node, getNearestAirPoint2(new Point(currPearl.x, 0)));
				
				aStarPath = path;
				currTarget = path.get(path.size() - 1);
				currState = State.movingAlongSurface;
			}else if(unreachablePearls != null)currState = State.seekingPearl;
			else currState = State.SuicideCharge;
		}
		if(position.distance(currTarget) < 2) {
			if(aStarPath == null) {
				
			}
			else if(aStarPath.size() != 1) {
				aStarPath.remove(aStarPath.size() - 1);
				currTarget = aStarPath.get(aStarPath.size()-1);
			}else {
				currTarget = currPearl;
			}
		}
		//if(rayCast2(position, seek(currPearl).normalize(), currPearl)) {
			//Vector direction = seek(currPearl);
			//float angle = (float) Math.atan2(direction.y, direction.x);
			//return new DivingAction(info.getMaxAcceleration(), -angle);
		//}
		Vector direction = seek(currTarget);
		direction.normalize();
		
		float angle = (float) Math.atan2(direction.y, direction.x);
		return new DivingAction(info.getMaxAcceleration(), -angle);
		
		
	}
	private void seekNextPearl(Point position) {
		//removePearl(position);
		currScore = info.getScore();
		
			if(unreachablePearls != null) currPearl = getClosestPoint(position);
			else currPearl = getBestPearlPocket(position);
			Point2D currPos = new Point2D.Float(info.getX(), info.getY());
			GraphNode node = new GraphNode(currPos, reflexCorners);
			Area obstacleArea = new Area();
			for(Path2D path : info.getScene().getObstacles()) 
				obstacleArea.add(new Area(path.createTransformedShape(new AffineTransform())));

			node.addOneWayTransitions(graphy.nodes, obstacleArea);
			List<Point2D> path = graphy.constructPathAStar(node, currPearl);
			if(path != null) {
				//System.out.println(path.size());
				aStarPath = path;
				currTarget = path.get(path.size() - 1);

			}else {
				System.out.println("did not find path");
				ArrayList<Point> removedPearls = new ArrayList<>();
				
				while(path == null && remainingPearls.size() > 1) {
					removedPearls.add(currPearl);
					remainingPearls.remove(currPearl);
					currPearl = getClosestPoint(position);
					path = graphy.constructPathAStar(node, currPearl);
					
				}
				remainingPearls.addAll(removedPearls);
				if(path != null) {
					//System.out.println(path.size());
					aStarPath = path;
					currTarget = path.get(path.size() - 1);
				}
			}
		
	}
	private Point2D getNearestAirPoint(Point2D target) {
		double lowestDistance = 999999999;
		Point2D currPos = new Point2D.Float(info.getX(), info.getY());
		Point2D compareTo = new Point2D.Double((info.getX() + target.getX()) / 2, 0.f);
		Point2D reti = airPoints.get(0);
		for(Point2D airPoint : airPoints) 
			if(airPoint.distance(compareTo)  < lowestDistance) {
				reti = airPoint;
				lowestDistance = airPoint.distance(compareTo);
			}
		return reti;
		
	}
	private Point2D getNearestAirPoint2(Point2D target) {
		double lowestDistance = 999999999;
		Point2D reti = airPoints.get(0);
		for(Point2D airPoint : airPoints) 
			if(airPoint.distance(target)  < lowestDistance) {
				reti = airPoint;
				lowestDistance = airPoint.distance(target);
			}
		return reti;
	}
	private double calcPathLenght(List<Point2D> path) {
		double length = 0;
		for(int i = 0; i < path.size() - 1; i++)length += path.get(i).distance(path.get(i+1)); 
		return length;
	}
	private Vector seek(Point2D target) {
		return new Vector(target.getX() - info.getX(), target.getY() - info.getY());
	}
	private Point flee(Point target) {
		return new Point((int) -(target.x - info.getX()), (int) (target.y - info.getY()));
	}
	private float getLen(Point point) {
		return (float) Math.sqrt(Math.pow(point.getX(), 2) + Math.pow(point.getY(), 2));
	}
	private void removePearl(Point playerPos) {
		Point ClosestPearl = getClosestPoint(playerPos);
		remainingPearls.remove(ClosestPearl);
	}
	private Point getClosestPoint(Point playerPos) {
		Point ClosestPearl = remainingPearls.get(0);
		for(Point pearl: remainingPearls) {
			if(playerPos.distance(pearl) < playerPos.distance(ClosestPearl))ClosestPearl = pearl;
		}
		return ClosestPearl;
	}
	private Point getBestPearlPocket(Point2D playerPos){
		Point bestPoint = null;
		double lowestDistance = 999999999;
		for(int i = 0; i < remainingPearls.size(); i++) {
			Point point = remainingPearls.get(i);
			double currDistance = 0;
			for(int f = 0; f < remainingPearls.size(); f++) {
				currDistance += Math.pow(point.distance(remainingPearls.get(f)), 2);
				
			}
			if(currDistance < lowestDistance) {
				lowestDistance = currDistance;
				bestPoint = point;
			}
		}
			
		return bestPoint;
		
	}
	private Point normalize(Point toNormalize) {
		float len = getLen(toNormalize);
		return new Point((int)( toNormalize.getX() / len), (int) (toNormalize.getY() / len));
	}

	private void constructGraph() {
		Path2D[] obstacles = info.getScene().getObstacles();
		Area[] areas = new Area[obstacles.length];
		ArrayList<Point2D> reflexCorners = new ArrayList<>();
		for(int i = 0; i < obstacles.length; i++)areas[i] = new Area(obstacles[i]);
		int amountOfPoints = 0;
		for(Path2D obstacle : obstacles) {
			PathIterator it = obstacle.getPathIterator(null);
			Point2D prevPoint = new Point2D.Double(0, 0);
			Point2D currPoint = new Point2D.Double(0,0);
			Point2D nextPoint = new Point2D.Double(0,0);
			for(; !it.isDone(); it.next()) {
				float[] coord = new float[2];

				it.currentSegment(coord);
				nextPoint = new Point2D.Float(coord[0], coord[1]);
				if(isReflexCorner(currPoint, prevPoint, nextPoint)) {
					Vector normal1  =new Vector(-(currPoint.getY() - prevPoint.getY()),currPoint.getX() - prevPoint.getX()).normalize();
					Vector normal2  =new Vector(-(nextPoint.getY() - currPoint.getY()),nextPoint.getX() - currPoint.getX()).normalize();
					Vector toAdd = normal1.add(normal2).scale(0.5f).normalize().scale(15);
					Point2D movedPoint = new Point2D.Double(currPoint.getX() - toAdd.x, currPoint.getY() - toAdd.y);
					reflexCorners.add(movedPoint);
				}
				prevPoint = currPoint;
				currPoint = nextPoint;
				amountOfPoints++;
			}
		}
		//System.out.println(amountOfPoints);
		//System.out.println(reflexCorners.size());
		this.reflexCorners = reflexCorners;
	}

	private boolean isReflexCorner(Point2D toTest, Point2D prev, Point2D next) {
		Vector aTob = new Vector((float) (toTest.getX() - prev.getX()), (float) (toTest.getY() - prev.getY()));
		Vector aToC = new Vector((float) (next.getX() - toTest.getX()), (float) (next.getY() - toTest.getY()));
		//	Vector aToC = new Vector((float) (next.getX() - prev.getX()), (float) (next.getY() - prev.getY())); 
		aTob = new Vector(-aTob.y, aToC.x);
		return aToC.dotProdut(aTob) < 0;
	}
	
	private class Vector{
		public float x;
		public float y;
		@Override
		public String toString() {
			return "Vector [x=" + x + ", y=" + y + "]";
		}
		public Vector(float x, float y) {
			this.x = x;
			this.y = y;
		}
		public Vector(double x, double y) {
			this.x = (float)x;
			this.y = (float)y;
		}
		public Vector add(Vector toAdd) {
			this.x += toAdd.x;
			this.y += toAdd.y;
			return this;
		}
		public float getLen() {
			return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		}
		public double getX() {
			return x;
		}
		public double getY() {
			return y;
		}
		public Vector normalize() {
			float len = getLen();
			x /= len;
			y /= len;
			return this;
		}
		public Vector scale(float scalar) {
			return new Vector(x* scalar,y * scalar);
		}
		public float dotProdut(Vector toMulitply) {
			return this.x * toMulitply.x + this.y * toMulitply.y; 
		}
		
	}

	private class Graph {
		public ArrayList<GraphNode> nodes;
		public Graph(ArrayList<Point2D> points){
			Area obstacleArea = new Area();
			for(Path2D pathing : info.getScene().getObstacles()) 
				obstacleArea.add(new Area(pathing.createTransformedShape(new AffineTransform())));
			nodes = new ArrayList<>();
			
			for (int i = 0; i < points.size() ; i++) {
				Point2D curr = points.get(i);
				GraphNode toAdd = new GraphNode(curr, points);
				//for(GraphNode node : nodes) {
					//if(node.edges.contains(toAdd.point))toAdd.edges.add(node.point);
				//}
				nodes.add(toAdd);
				
			}
			for(GraphNode node : nodes) {
				//clone.remove(node);
				node.addTransitions(nodes, obstacleArea);
				//System.out.println(node.transitions.size());
				
			}
			
	
		}
		public List<Point2D> constructPathAStar(GraphNode start, Point2D target) {
			PointComperator comp = new PointComperator(target);
			ArrayList<GraphNode> frontier = new ArrayList<>();
			HashMap<GraphNode, GraphNode> cameFrom = new HashMap<>();
			HashMap<Point2D, Integer> HighestCost = new HashMap<>();
		
			
			//start = nearestNode;
			frontier.add(start);
			cameFrom.put(start, null);
			start.lowestCost = 1;
			HighestCost.put(start.point, 0);
			while(frontier.size() != 0) {
				GraphNode curr = frontier.remove(0);
				if(curr.point.distance(target) < 15) {
				
					for(GraphNode node : nodes) {
						node.lowestCost = 0;
					}
					List<Point2D> path = new ArrayList<>();
					GraphNode point = curr;
					path.add(point.point);
					while(cameFrom.get(point) != null) {
						point = cameFrom.get(point);
						path.add(point.point);
					}
					return path;
					
				}
				for(GraphNode next : curr.transitions) {
					//GraphNode nuxt = getGraphNode(next);
					if(next != null) {
						int cost =(int) next.point.distance(curr.point) + curr.lowestCost;
						if(!frontier.contains(next) && !cameFrom.containsKey(next))frontier.add(next);
						if(cost < next.lowestCost || next.lowestCost == 0) {
							next.lowestCost = cost;
							cameFrom.put(next, curr );
						}
						
						
					}else {
						System.out.println("could not find point");
					}
					
				}
				frontier.sort(comp);
				
			}
			System.out.println("did not find path");
			for(GraphNode node : nodes) {
				node.lowestCost = 0;
			}
		
			return null;
			
		}
		public GraphNode getGraphNode(Point2D point) {
			for(GraphNode node : nodes) if(node.point.equals(point))return node;
			return null;
		}
		private void addPoint(Point2D point) {
			Area obstacleArea = new Area();// new Area[info.getScene().getObstacles().length];
			for(Path2D path : info.getScene().getObstacles()) 
				obstacleArea.add(new Area(path.createTransformedShape(new AffineTransform())));
			
			for(GraphNode node : nodes) {
				
			}
		}
	}
	private class GraphNode {
		public Point2D point;
		public ArrayList<Point2D> edges;
		public ArrayList<GraphNode> transitions;
		int lowestCost;
		public GraphNode(Point2D point, ArrayList<Point2D> points) {
			edges = new ArrayList<>();
			transitions = new ArrayList<>();
			this.point = point;
//			Area obstacleArea = new Area();// new Area[info.getScene().getObstacles().length];
//			for(Path2D path : info.getScene().getObstacles()) 
//				obstacleArea.add(new Area(path.createTransformedShape(new AffineTransform())));
//			
//			for(Point2D currPoint : points) {	
//				Vector normal = new Vector(-(currPoint.getY() - point.getY()),currPoint.getX() - point.getX()).normalize();
//				Path2D path = new Path2D.Double();
//				path.moveTo(point.getX(), point.getY());
//				path.lineTo(currPoint.getX(), currPoint.getY());
//				path.lineTo(currPoint.getX() + normal.getX(), currPoint.getY() + normal.getY());
//				path.lineTo(point.getX() + normal.getX(), point.getY() + normal.getY());
//				path.closePath();
//				Area test = new Area(path);
//				test.intersect(obstacleArea);
//				if(test.isEmpty()) edges.add(currPoint);
//				
//				
//				
//			}
		}
		public void addOneWayTransitions(ArrayList<GraphNode> nodes, Area obstacleArea) {
			for(GraphNode node : nodes) {
				Point2D currPoint = node.point;
				Vector normal = new Vector(-(currPoint.getY() - point.getY()),currPoint.getX() - point.getX()).normalize().scale(2f);
				Path2D path = new Path2D.Double();
				path.moveTo(point.getX() - normal.getX(), point.getY() - normal.getY());
				path.lineTo(currPoint.getX() - normal.getX(), currPoint.getY() - normal.getY());
				path.lineTo(currPoint.getX() + normal.getX(), currPoint.getY() + normal.getY());
				path.lineTo(point.getX() + normal.getX(), point.getY() + normal.getY());
				path.closePath();
				Area test = new Area(path);
				test.intersect(obstacleArea);
			
				if(test.isEmpty()) {
					if(transitions.add(node));
				
				}
			}
		}
		public void addTransitions(ArrayList<GraphNode> nodes, Area obstacleArea) {
			for(GraphNode node : nodes) {
				if(node.point.getX() <  point.getX())continue;
				Point2D currPoint = node.point;
				Vector normal = new Vector(-(currPoint.getY() - point.getY()),currPoint.getX() - point.getX()).normalize().scale(2f);
				Path2D path = new Path2D.Double();
				path.moveTo(point.getX() - normal.getX(), point.getY() - normal.getY());
				path.lineTo(currPoint.getX() - normal.getX(), currPoint.getY() - normal.getY());
				path.lineTo(currPoint.getX() + normal.getX(), currPoint.getY() + normal.getY());
				path.lineTo(point.getX() + normal.getX(), point.getY() + normal.getY());
				path.closePath();
				Area test = new Area(path);
				test.intersect(obstacleArea);
			
				if(test.isEmpty()) {
					transitions.add(node);
					node.transitions.add(this);
				}
			}
		}
	}
	private class PointComperator implements Comparator<GraphNode>{
		
		private Point2D target;
		public PointComperator(Point2D target) {
			super();
			this.target = target;
			
		}
		@Override
		public int compare(GraphNode o1, GraphNode o2) {
			return  ((int)o1.point.distance(target) + o1.lowestCost)  - ((int)o2.point.distance(target) + o2.lowestCost);
		}
		
	}
	private enum State{
		SeekingAir, 
		seekingPearl,
		movingAlongSurface,
		SuicideCharge
	}
}
