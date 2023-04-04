package assignment3;

import java.awt.Color;

public class BlobGoal extends Goal{

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] unitBoard = board.flatten();
		int arrayLength = unitBoard.length;
		boolean[][] visited = new boolean[arrayLength][arrayLength];
		int biggestBlob = 0;

		for(int i = 0; i < arrayLength; i++) {
			for(int j = 0; j < arrayLength; j++){
				int blob = undiscoveredBlobSize(i,j,unitBoard,visited);
				if (blob > biggestBlob) biggestBlob = blob;
			}
		}
		return biggestBlob;
	}

	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal) 
		+ " blocks, anywhere within the block";
	}


	//{{adasd},
	// {safdf}
	//}

	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
		if (unitCells[i][j] != this.targetGoal) { //Cell does not contain color we want
			visited[i][j] = true;
			return 0;
		} else if (visited[i][j]) { //Cell already visited
			return 0;
		} else { //Cell not visited
			int upperBound = unitCells.length - 1;
			int size = 1;
			visited[i][j] = true;

			if(boundsCheck(i-1, j, upperBound)) { // up
				size += undiscoveredBlobSize(i-1, j, unitCells, visited);
			}
			if(boundsCheck(i+1, j, upperBound)) { // down
				size += undiscoveredBlobSize(i+1, j, unitCells, visited);
			}
			if(boundsCheck(i, j+1, upperBound)) { //right
				size += undiscoveredBlobSize(i, j+1, unitCells, visited);
			}
			if(boundsCheck(i, j-1, upperBound)) { //left
				size += undiscoveredBlobSize(i, j-1, unitCells, visited);
			}
			return size;
		}
	}

	private static boolean boundsCheck(int i, int j, int bounds) {
		return (0 <= i) && (i <= bounds) && (0 <= j) && (j <= bounds);
	}
	public static void main(String[] args) {
		Block b = new Block(0,6);
		b.updateSizeAndPosition(128, 0,0);
		b.printBlock();
		b.printColoredBlock();

		BlobGoal g1 = new BlobGoal(GameColors.BLUE);
		System.out.println(g1.score(b));
	}
}
