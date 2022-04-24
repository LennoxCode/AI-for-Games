package s0575695;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
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
	Point[][] nodes;
	public BetterAI(Info info) {
		super(info);
		currPearlIndex = 0;
		currScore = 0;
		remainingPearls = new ArrayList<>();
		for(Point point : info.getScene().getPearl())remainingPearls.add(point);
		currPearl = remainingPearls.get(0);
		currDirection = new Vector(0, 0); 
		
		for (int x = 0; x < info.getScene().getWidth(); x +=20) {
			for (int y = 0; y < info.getScene().getHeight(); y+=20) {
				
			}
		}
		
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
		}
		passedTime++;
		if(passedTime % 5 != 0) {
			return new DivingAction(info.getMaxAcceleration(), -currentAngle);
		}
		
		Vector direction = seek(currPearl);
		direction.normalize();
		//Point seekForce = seek(currPearl);
		//direction = new Point((int) (direction.getX() + seekForce.getX() / getLen(seekForce)),(int)  (direction.getY() + seekForce.getY() / getLen(seekForce)));
		//System.out.println(rayCast(position, direction.getX() / getLen(direction), direction.getY() / getLen(direction)));
		if(position.distance(currPearl) > 40) {
			for(Path2D path : info.getScene().getObstacles()) {
				if(path.contains(info.getX() + direction.x * 40,
					info.getY() + direction.y * 40)) {
					
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
				if(path.contains(origin.x + i * directionX, origin.y - i * directionY )) return i;
			}
		}
		return 5;
	}
	
	private List<Point> constructPath(Point from, Point to) {
		Queue<Point> toLookat = new PriorityQueue<>();
		HashMap<Point, Point> lookedAt = new HashMap<>();
		toLookat.add(from);
		while(toLookat.size() > 0) {
			Point current = toLookat.remove();
			List<Point> neighbors = getNeighbors(current);
			for(Point neighbor : neighbors) {
				if(!lookedAt.containsKey(neighbor) && !toLookat.contains(neighbor)) toLookat.add(neighbor);
			
			}
			
		}
		
		
		
		return null;
	}
	private List<Point> getNeighbors(Point from){
		ArrayList<Point> reti = new ArrayList<>();
		for (int i = -1; i < 2; i+=2) {
			for (int f = -1; f < 2; f+=2) {
				Point point = new Point(from.x + 5 * i, from.y * f);
				if(!isPointInObstacles(point))reti.add(point);
			}
		}
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
