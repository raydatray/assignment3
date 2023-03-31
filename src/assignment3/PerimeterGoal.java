package assignment3;

import java.awt.Color;

public class PerimeterGoal extends Goal{

	public PerimeterGoal(Color c) {
		super(c);
	}

	//run around the board
	@Override
	public int score(Block board) {
		Color[][] unitBoard = board.flatten();
		int arrayLength = unitBoard.length;
		int perimeter = 0;

		for(int i = 0; i < arrayLength; i++) {
			if(unitBoard[i][0] == this.targetGoal) perimeter++; //left edge
			if(unitBoard[i][arrayLength - 1] == this.targetGoal) perimeter++; //right edge
			if(unitBoard[0][i] == this.targetGoal) perimeter++; //top edge
			if(unitBoard[arrayLength - 1][i] == this.targetGoal) perimeter++; //bottom edge
		}
		return perimeter;
	}

	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal) 
		+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
