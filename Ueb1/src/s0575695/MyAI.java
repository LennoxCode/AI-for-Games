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

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

public class MyAI extends AI {

	final int AVOID_TIME = 60;
	int currPearlIndex;
	int currScore;
	int avoidTime = 0;
	int offsetAngle = 0;
	Point center;
	Point currPearl;
	ArrayList<Point> remainingPearls;
	public MyAI(Info info) {
		super(info);
		currPearlIndex = 0;
		currScore = 0;
		remainingPearls = new ArrayList<>();
		for(Point point : info.getScene().getPearl())remainingPearls.add(point);
		currPearl = remainingPearls.get(0);
		
	}
	
	@Override
	public String getName() {
		return "Leonard Valentin";
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
		Point direction = seek(currPearl);
		float angle = (float) Math.atan2(direction.y, direction.x);
		//Rectangle2D test = new Rectangle((int)info.getX(),(int) info.getY(), 5, 5);
		//System.out.println(info.getScene().getObstacles()[0].intersects(test));
		//System.out.println(info.getScene().getObstacles().length);
		if(avoidTime > 0) {
			direction = flee(center);
			angle = (float) Math.atan2(direction.y, direction.x);
			 
			avoidTime--;
			return new DivingAction(info.getMaxAcceleration(), angle);
			
		}
		for(Path2D path : info.getScene().getObstacles()) {
			if(path.contains(info.getX() + direction.getX() * 20/ getLen(direction) ,
					info.getY() - direction.y * 20 / getLen(direction))) {
				avoidTime = AVOID_TIME;
				Rectangle bounds = path.getBounds();
				Point center = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height /2);
				System.out.println(center);
				this.center = new Point((position.x + center.x) / 2, (position.y + center.y) / 2);
			
			}
		}
		
		//if(info.getScene().getObstacles()[0].contains(info.getX() , info.getY()+5))System.out.println("obstacle right");
		//if(info.getScene().getObstacles()[0].contains(info.getX() -5 , info.getY()))System.out.println("obstacle up");
		//if(info.getScene().getObstacles()[0].contains(info.getX() +5 , info.getY()))System.out.println("obstacle below");
		//if(info.getScene().getObstacles()[0].contains(info.getX() , info.getY()-5 ))System.out.println("obstacle left");
		return new DivingAction(info.getMaxAcceleration(), angle);
		
		
	}
	private Point seek(Point target) {
		return new Point((int) (target.x - info.getX()), (int) -(target.y - info.getY()));
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
}
