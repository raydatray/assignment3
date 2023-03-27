package assignment3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
public class Block {
	private int xCoord;
	private int yCoord;
	private int size; // height/width of the square
	private int level; // the root (outer most block) is at level 0
	private int maxDepth; 
	private Color color;
	private Block[] children; // {UR, UL, LL, LR}
	public static Random gen = new Random(2);
 
	/*
	 * These two constructors are here for testing purposes. 
	 */
	public Block() {}
	public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
		this.xCoord=x;
		this.yCoord=y;
		this.size=size;
		this.level=lvl;
		this.maxDepth = maxD;	
		this.color=c;
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

		if(lvl == maxDepth) {
			this.color = GameColors.BLOCK_COLORS[gen.nextInt(4)];
			this.children = new Block[0];
		} else {
			if (gen.nextFloat(1) < Math.exp((-.25)*this.level)) {
				Block[] subBlocks = new Block[4];
				for(int i = 0; i < 4; i++){
					subBlocks[i] = new Block(lvl + 1, maxDepth);
				}
				this.children = subBlocks;
				this.color = null;
			} else {
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
		if (this.level == 0 ) {
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
		/*
		 * ADD YOUR CODE HERE
		 */
		return null;
	}

	/*
	 * Swaps the child Blocks of this Block. 
	 * If input is 1, swap vertically. If 0, swap horizontally. 
	 * If this Block has no children, do nothing. The swap 
	 * should be propagate, effectively implementing a reflection
	 * over the x-axis or over the y-axis.
	 * 
	 */
	public void reflect(int direction) {
		/*
		 * ADD YOUR CODE HERE
		 */
	}
 

 
	/*
	 * Rotate this Block and all its descendants. 
	 * If the input is 1, rotate clockwise. If 0, rotate 
	 * counterclockwise. If this Block has no children, do nothing.
	 */
	public void rotate(int direction) {
		/*
		 * ADD YOUR CODE HERE
		 */
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
		/*
		 * ADD YOUR CODE HERE
		 */
		return false;
	}
 
 
	/*
	 * Return a two-dimensional array representing this Block as rows and columns of unit cells.
	 * 
	 * Return and array arr where, arr[i] represents the unit cells in row i, 
	 * arr[i][j] is the color of unit cell in row i and column j.
	 * 
	 * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
	 */
	public Color[][] flatten() {
		/*
		 * ADD YOUR CODE HERE
		 */
		return null;
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
		Block blockDepth2 = new Block(0,2);
		blockDepth2.updateSizeAndPosition(16,0,0);
		blockDepth2.getBlocksToDraw();
	}
}