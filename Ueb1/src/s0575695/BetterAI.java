package s0575695;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.file.Path;
import java.util.ArrayList;
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

public class BetterAI extends AI {

	final int AVOID_TIME = 120;
	int currPearlIndex;
	int currScore;
	int avoidTime = 0;
	int offsetAngle = 0;
	Point center;
	Point currPearl;
	ArrayList<Point> remainingPearls;
	Vector currDirection;
	float currentAngle;
	int passedTime =0;
	List<Point> currPath;
	Point currTarget;
	public BetterAI(Info info) {
		super(info);
		currPearlIndex = 0;
		currScore = 0;
		remainingPearls = new ArrayList<>();
		for(Point point : info.getScene().getPearl())remainingPearls.add(point);
		currPearl = getClosestPoint(new Point(0, info.getScene().getHeight() /2));
		currDirection = new Vector(0, 0); 
		currPath = constructPath( new Point((int)info.getX(),(int) info.getY()), currPearl);
		currTarget = currPath.get(currPath.size()-1);
		
	}
	
	@Override
	public String getName() {
		return "better AI";
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
	public PlayerAction update() {
		
		//Point[] pearls = info.getScene().getPearl();
		Point position = new Point((int)info.getX(),(int) info.getY());
		if(info.getScore() > currScore) {
			removePearl(position);
			currScore = info.getScore();
			currPearl = getClosestPoint(position);
			currPath = constructPath(position, currPearl);
		}
		if(position.distance(currTarget) < 2) {
			if(currPath.size() != 1) {
				currPath.remove(currPath.size() - 1);
				currTarget = currPath.get(currPath.size()-1);
			}else {
				currTarget = currPearl;
			}
		}
		//if(rayCast2(position, seek(currPearl).normalize(), currPearl)) {
			//Vector direction = seek(currPearl);
			//float angle = (float) Math.atan2(direction.y, direction.x);
			//return new DivingAction(info.getMaxAcceleration(), -angle);
		//}
		///passedTime++;
		//if(passedTime % 5 != 0) {
		//	return new DivingAction(info.getMaxAcceleration(), -currentAngle);
		//}
		
		Vector direction = seek(currTarget);
		direction.normalize();
		
		//Point seekForce = seek(currPearl);
		//direction = new Point((int) (direction.getX() + seekForce.getX() / getLen(seekForce)),(int)  (direction.getY() + seekForce.getY() / getLen(seekForce)));
		//System.out.println(rayCast(position, direction.getX() / getLen(direction), direction.getY() / getLen(direction)));
		if(position.distance(currPearl) > 20) {
			for(Path2D path : info.getScene().getObstacles()) {
				//if(path.contains(info.getX() + direction.x * 20,
					//info.getY() + direction.y * 20)) {
					if(false) {
					Rectangle bounds = path.getBounds();
					
					Point center = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height /2);
					
					Vector avoidanceForce = new Vector(direction.x - center.x, direction.y - center.y);
					System.out.println(avoidanceForce.normalize());
					direction.add(avoidanceForce.normalize()).normalize();
				
				}
			}
		}
	
		
		
		float angle = (float) Math.atan2(direction.y, direction.x);
		currentAngle = angle;
		return new DivingAction(info.getMaxAcceleration(), -angle);
		
		
	}
	private Vector seek(Point target) {
		return new Vector(target.x - info.getX(), target.y - info.getY());
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
	private Point normalize(Point toNormalize) {
		float len = getLen(toNormalize);
		return new Point((int)( toNormalize.getX() / len), (int) (toNormalize.getY() / len));
	}
	private int rayCast(Point origin, double directionX, double directionY) {
		for (int i = 0; i < 500; i++) {
			for(Path2D path : info.getScene().getObstacles()) {
				if(path.contains(origin.x + i * directionX, origin.y + i * directionY )) return i;
			}
		}
		return 5;
	}
	private boolean rayCast2(Point origin, Vector direction, Point target) {
		Line2D pathToTarget = new Line2D.Double(origin.x, origin.y, target.x, target.y);
		
		
	
		for(Path2D path : info.getScene().getObstacles()) {
			Point2D lastPoint = null;
			for(PathIterator pi = path.getPathIterator(null); !pi.isDone(); pi.next()) {
				double[] coordinates = new double[6];
                pi.currentSegment(coordinates);
                if(lastPoint == null) {
                    lastPoint = new Point((int) coordinates[0], (int) coordinates[1]);
                }
                Line2D segment = new Line2D.Double(lastPoint.getX(), lastPoint. getY(), coordinates[0],coordinates[1]);
                lastPoint.setLocation((int) coordinates[0], (int) coordinates[1]);
                if(coordinates[0] != 0 && coordinates[1] != 0 && pathToTarget.intersectsLine(segment)) {
                	return false;
                }
		}
		}
		return true;
	}
	
	private List<Point> constructPath(Point from, Point to) {
		Queue<Point> toLookat = new LinkedList<>();
		HashMap<Point, Point> cameFrom = new HashMap<>();
		toLookat.offer(from);
		cameFrom.put(from, null);
		while(toLookat.size() > 0) {
			Point current = toLookat.poll();
			if(current.distance(to) < 15) { 
				List<Point> path = new ArrayList<>();
				Point point = current;
				path.add(point);
				while(cameFrom.get(point) != null) {
					point = cameFrom.get(point);
					path.add(point);
				}
				return path;
				}
			List<Point> neighbors = getNeighbors(current);
			for(Point neighbor : neighbors) {
				if(!toLookat.contains(neighbor) && !cameFrom.containsKey(neighbor)) {
					toLookat.add(neighbor);
					cameFrom.put(neighbor, current);
					
				}
			
			}
			
		}
		
		
		
		return null;
	}
	private List<Point> getNeighbors(Point from){
		final int GRID_SIZE = 20;
		ArrayList<Point> reti = new ArrayList<>();
		Point[] neighbors = {new Point(from.x + GRID_SIZE, from.y), new Point(from.x - GRID_SIZE, from.y),
				new Point(from.x , from.y + GRID_SIZE), new Point(from.x, from.y - GRID_SIZE)};
		for(Point point : neighbors)if(!isPointInObstacles(point))reti.add(point);
		return reti;
	}
	private boolean isPointInObstacles(Point point) {
		
		for(Path2D path : info.getScene().getObstacles()) {
			if(path.contains(point))return true;
		}
		return false;
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
		
		
	}
}
