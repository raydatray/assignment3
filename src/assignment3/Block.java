package assignment3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
public class Block {
	private int xCoord;
	private int yCoord;
	private int size; // height/width of the square
	private int level; // the root (outermost block) is at level 0
	private int maxDepth; 
	private Color color;
	private Block[] children; // {UR, UL, LL, LR}
	public static Random gen = new Random();
 
	/*
	 * These two constructors are here for testing purposes. 
	 */
	public Block() {}
	public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
		this.xCoord = x;
		this.yCoord = y;
		this.size = size;
		this.level = lvl;
		this.maxDepth = maxD;	
		this.color = c;
		this.children = subBlocks;
	}

	/*
	 * Creates a random block given its level and a max depth. 
	 * 
	 * xCoord, yCoord, size, and highlighted should not be initialized
	 * (i.e. they will all be initialized by default)
	 */
	public Block(int lvl, int maxDepth) {
		this.level = lvl;
		this.maxDepth = maxDepth;

		if(lvl == maxDepth) { //At max depth you cannot generate more children
			this.color = GameColors.BLOCK_COLORS[gen.nextInt(4)];
			this.children = new Block[0];
		} else {
			if (gen.nextFloat(1) < Math.exp(-.25 * this.level)) { //Attempt to generate more children
				Block[] subBlocks = new Block[4];
				for(int i = 0; i < 4; i++){
					subBlocks[i] = new Block(lvl + 1, maxDepth);
				}
				this.children = subBlocks;
				this.color = null;
			} else { //No kids :(
				this.color = GameColors.BLOCK_COLORS[gen.nextInt(4)];
				this.children = new Block[0];
			}
		}
	}
	/*
	  * Updates size and position for the block and all of its sub-blocks, while
	  * ensuring consistency between the attributes and the relationship of the 
	  * blocks. 
	  * 
	  *  The size is the height and width of the block. (xCoord, yCoord) are the 
	  *  coordinates of the top left corner of the block. 
	 */
	public void updateSizeAndPosition (int size, int xCoord, int yCoord) {
		if (this.level == 0 ) { //this only works if it is the top block
			int level = 0;
			int tempSize = size;
			while (level <= this.maxDepth) {
				if (tempSize % 2 == 0) {
					level++;
					tempSize /= 2;
				} else {
					throw new IllegalArgumentException("x");
				}
			}
		}

		this.size = size;
		this.xCoord = xCoord;
		this.yCoord = yCoord;

		if (this.children.length != 0){
			for(int i = 0; i < 4; i++) {
				switch(i) {
					case 0 -> {
						this.children[i].updateSizeAndPosition(size/2, xCoord + size/2, yCoord);
					}
					case 1 -> {
						this.children[i].updateSizeAndPosition(size/2, xCoord, yCoord);
					}
					case 2 -> {
						this.children[i].updateSizeAndPosition(size/2, xCoord, yCoord + size/2);
					}
					case 3 -> {
						this.children[i].updateSizeAndPosition(size/2, xCoord + size/2, yCoord + size/2);
					}
				}
			}
		} else {
			return;
		}
	}

	/*
  	* Returns a List of blocks to be drawn to get a graphical representation of this block.
  	* 
  	* This includes, for each undivided Block:
  	* - one BlockToDraw in the color of the block
  	* - another one in the FRAME_COLOR and stroke thickness 3
  	* 
  	* Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
  	*  
  	* The order in which the blocks to draw appear in the list does NOT matter.
  	*/
	public ArrayList<BlockToDraw> getBlocksToDraw() {
		ArrayList<BlockToDraw> board = new ArrayList<BlockToDraw>();
		if(this.children.length != 0) {
			for(Block block : this.children){
				board.addAll(block.getBlocksToDraw());
			}
		} else {
			board.add(new BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0));
			board.add(new BlockToDraw(GameColors.FRAME_COLOR, this.xCoord, this.yCoord, this.size, 3));
		}
		return board;
	}

	/*
	 * This method is provided, and you should NOT modify it.
	 */
	public BlockToDraw getHighlightedFrame() {
		return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
	}
 
	/*
	 * Return the Block within this Block that includes the given location
	 * and is at the given level. If the level specified is lower than 
	 * the lowest block at the specified location, then return the block 
	 * at the location with the closest level value.
	 * 
	 * The location is specified by its (x, y) coordinates. The lvl indicates 
	 * the level of the desired Block. Note that if a Block includes the location
	 * (x, y), and that Block is subdivided, then one of its sub-Blocks will 
	 * contain the location (x, y) too. This is why we need lvl to identify 
	 * which Block should be returned. 
	 * 
	 * Input validation: 
	 * - this.level <= lvl <= maxDepth (if not throw exception)
	 * - if (x,y) is not within this Block, return null.
	 */
	public Block getSelectedBlock(int x, int y, int lvl) {
		if((this.level > lvl) || (lvl > this.maxDepth)){
			throw new IllegalArgumentException("Invalid depth level provided");
		}

		if (!containsCoord(this, x, y)){
			return null;
		} else {
			return searchTree(this, x, y, lvl);
		}
	}

	//Helper function to check if a block contains given coordinates x,y
	private static boolean containsCoord(Block block, int x, int y){
		return ((block.xCoord <= x && x <= block.xCoord + block.size) && (block.yCoord <= y && y <= block.yCoord + block.size ));
	}

	//Recursive Tree Search Algo
	private static Block searchTree(Block block, int x, int y, int lvl){
		if(block == null){ //return null if block doesnt exist
			return null;
		}

		if(containsCoord(block, x, y)){ //return null if block doesnt contain given coords
			if(lvl == block.level){
				return block; //if the block is at the correct level and contains the coords, immediately return this block
			} else if (block.children.length == 0 ){
				return null; //if the block is not at the right level and has no children, it is a leaf and can immediately be pruned
			} else {
				Block result = null;
				while (result == null) { //we are GUARANTEED a result past the input parsing, continuously search until a result is found
					for (Block subBlock : block.children) {
						Block tempResult = searchTree(subBlock, x, y, lvl); //recursively call for each child of a given block
						if (tempResult != null) {
							result = tempResult; //if a block is found write to the result
						}
					}
					if (result == null) { //if no result is found at a given level, go up one level to find the closest one
						lvl--;
					}
				}
				return result;
			}
		} else {
			return null;
		}
	}

	/*
	 * Swaps the child Blocks of this Block. 
	 * If input is 1, swap vertically. If 0, swap horizontally. 
	 * If this Block has no children, do nothing. The swap 
	 * should be propagated, effectively implementing a reflection
	 * over the x-axis or over the y-axis.
	 * 
	 */
	public void reflect(int direction) {
		if (!(direction == 0 || direction == 1)) {
			throw new IllegalArgumentException("Invalid direction provided");
		}

		if(this.children.length == 0) {
			return;
		} else {
			Block[] newSubBlocks = new Block[4];
			for(int i = 0; i < 4; i++) { // UR UL LL LR
				switch(i) {
					case(0) -> {
						if(direction == 0){ //LR LL UL UR
							newSubBlocks[3] = this.children[i];
						} else { // UL UR LR LL
							newSubBlocks[1] = this.children[i];
						}
					}
					case(1) -> {
						if(direction == 0){ //LR LL UL UR
							newSubBlocks[2] = this.children[i];
						} else { // UL UR LR LL
							newSubBlocks[0] = this.children[i];
						}
					}
					case(2) -> {
						if(direction == 0){ //LR LL UL UR
							newSubBlocks[1] = this.children[i];
						} else { // UL UR LR LL
							newSubBlocks[3] = this.children[i];
						}
					}
					case(3) -> {
						if(direction == 0){ //LR LL UL UR
							newSubBlocks[0] = this.children[i];
						} else { // UL UR LR LL
							newSubBlocks[2] = this.children[i];
						}
					}
				}
				this.children[i].reflect(direction);
			}
			this.children = newSubBlocks;
			this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
		}
	}

	/*
	 * Rotate this Block and all its descendants. 
	 * If the input is 1, rotate clockwise. If 0, rotate 
	 * counterclockwise. If this Block has no children, do nothing.
	 */
	public void rotate(int direction) {
		if (!(direction == 0 || direction == 1)) {
			throw new IllegalArgumentException("Invalid direction provided");
		}

		if (this.children.length == 0) {
			return;
		} else {
			//this is array rotation
			Block[] newSubBlocks = new Block[4];

			if(direction == 0){ //CCW  0123 -> 3012
				for(int i = 0; i < 4; i++) { //0,1 1,2 2,3 3,0
					newSubBlocks[(i+1) % 4] = this.children[i];
					this.children[i].rotate(direction);
				}
			} else { //CW 0123 -> 1230
				for(int i = 0; i < 4; i++){ //0,3 1,0 2,1 3,2
					newSubBlocks[(i+3) % 4] = this.children[i];
					this.children[i].rotate(direction);
				}
			}
			this.children = newSubBlocks;
			this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
		}
	}
 


	/*
	 * Smash this Block.
	 * 
	 * If this Block can be smashed,
	 * randomly generate four new children Blocks for it.  
	 * (If it already had children Blocks, discard them.)
	 * Ensure that the invariants of the Blocks remain satisfied.
	 * 
	 * A Block can be smashed iff it is not the top-level Block 
	 * and it is not already at the level of the maximum depth.
	 * 
	 * Return True if this Block was smashed and False otherwise.
	 * 
	 */
	public boolean smash() {
		if(this.level == 0 || this.level == this.maxDepth){
			return false;
		} else {
			Block[] newSubBlocks = new Block[4];
			for(int i = 0; i < 4; i++){
				newSubBlocks[i] = new Block(this.level + 1, this.maxDepth);
				newSubBlocks[i].updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
			}
			this.children = newSubBlocks;
			return true;
		}
	}
 
 
	/*
	 * Return a two-dimensional array representing this Block as rows and columns of unit cells.
	 * 
	 * Return and array arr where, arr[i] represents the unit cells in row i, 
	 * arr[i][j] is the color of unit cell in row i and column j.
	 * 
	 * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
	 */

	//Unit cell = smallest
	public Color[][] flatten() {
		int unitSize = this.findUnit();

		Color[][] returnArray = new Color[this.size/unitSize][this.size/unitSize];
		this.writeArray(returnArray,unitSize);

		return returnArray;
	}

	private int findUnit() {
		int smallestFound = Integer.MAX_VALUE;
		if(this.children.length == 0){
			return this.size;
		} else {
			for(int i = 0; i < 4; i++) {
				int foundValue = this.children[i].findUnit();
				if (foundValue < smallestFound) {
					smallestFound = foundValue;
				}
			}
		}
		return smallestFound;
	}

	private void writeArray(Color[][] array, int unitSize){
		if (this.children.length == 0){
			for(int i = 0; i < (this.size/unitSize); i++){
				for(int j = 0; j < (this.size/unitSize); j++){
					array[this.yCoord/unitSize + i][this.xCoord/unitSize + j] = this.color;
				}
			}
		} else {
			for(Block subBlock : this.children){
				subBlock.writeArray(array,unitSize);
			}
		}
	}
 
	// These two get methods have been provided. Do NOT modify them. 
	public int getMaxDepth() {
		return this.maxDepth;
	}
 
	public int getLevel() {
		return this.level;
	}


	/*
	 * The next 5 methods are needed to get a text representation of a block. 
	 * You can use them for debugging. You can modify these methods if you wish.
	 */
	public String toString() {
		return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
	}

	public void printBlock() {
		this.printBlockIndented(0);
	}

	private void printBlockIndented(int indentation) {
		String indent = "";
		for (int i=0; i<indentation; i++) {
			indent += "\t";
		}

		if (this.children.length == 0) {
			// it's a leaf. Print the color!
			String colorInfo = GameColors.colorToString(this.color) + ", ";
			System.out.println(indent + colorInfo + this);   
		} 
		else {
			System.out.println(indent + this);
			for (Block b : this.children)
				b.printBlockIndented(indentation + 1);
		}
	}
 
	private static void coloredPrint(String message, Color color) {
		System.out.print(GameColors.colorToANSIColor(color));
		System.out.print(message);
		System.out.print(GameColors.colorToANSIColor(Color.WHITE));
	}

	public void printColoredBlock(){
		Color[][] colorArray = this.flatten();
		for (Color[] colors : colorArray) {
			for (Color value : colors) {
				String colorName = GameColors.colorToString(value).toUpperCase();
				if(colorName.length() == 0){
					colorName = "\u2588";
				}
				else{
					colorName = colorName.substring(0, 1);
				}
				coloredPrint(colorName, value);
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {
		Block blockDepth3 = new Block(0,4);
		blockDepth3.updateSizeAndPosition(32 ,0,0);
		blockDepth3.printBlock();
		blockDepth3.printColoredBlock();
	}
}