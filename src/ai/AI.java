package ai;

import engine.Move;

public interface AI {

	/**
	 * The move method is called by the game engine when it is the AI's turn
	 * 
	 * @param board
	 * @return random column in the board
	 */
	public abstract int move(int[][] board, Move lastMove);
}
