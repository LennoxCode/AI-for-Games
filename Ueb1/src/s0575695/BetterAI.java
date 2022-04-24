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
import java.util.Iterator;

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
	public BetterAI(Info info) {
		super(info);
		currPearlIndex = 0;
		currScore = 0;
		remainingPearls = new ArrayList<>();
		for(Point point : info.getScene().getPearl())remainingPearls.add(point);
		currPearl = remainingPearls.get(0);
		currDirection = new Vector(0, 0); 
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
		
		Vector direction = seek(currPearl);
		direction.normalize();
		currDirection.add(direction);
		currDirection.normalize();
		//Point seekForce = seek(currPearl);
		//direction = new Point((int) (direction.getX() + seekForce.getX() / getLen(seekForce)),(int)  (direction.getY() + seekForce.getY() / getLen(seekForce)));
		//System.out.println(rayCast(position, direction.getX() / getLen(direction), direction.getY() / getLen(direction)));
		if(position.distance(currPearl) > 40) {
			for(Path2D path : info.getScene().getObstacles()) {
				if(path.contains(info.getX() + currDirection.x * 40,
					info.getY() + currDirection.y * 40)) {
					
					Rectangle bounds = path.getBounds();
					
					Point center = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height /2);
					
					Vector avoidanceForce = new Vector(direction.x - center.x, direction.y - center.y);
					System.out.println(avoidanceForce.normalize());
					currDirection.add(avoidanceForce.normalize()).normalize();
				
				}
			}
		}
	

		
		float angle = (float) Math.atan2(currDirection.y, currDirection.x);
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
