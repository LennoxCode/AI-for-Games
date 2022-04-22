package s0575695;

import java.awt.Color;
import java.awt.Point;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

public class MyAI extends AI {

	int currPearlIndex;
	int currScore;
	public MyAI(Info info) {
		super(info);
		currPearlIndex = 0;
		currScore = 0;
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
		if(info.getScore() > currScore) {
			currScore = info.getScore();
			currPearlIndex++;
		}
		Point[] pearls = info.getScene().getPearl();
		Point nextTarget = pearls[currPearlIndex];
		System.out.println(currPearlIndex);
		return new DivingAction(0, 0);
	}

}
