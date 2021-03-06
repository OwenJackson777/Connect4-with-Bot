package engine;

/**
 * Version 3
 */

import java.util.Stack;

public class GameBoard {
	/**
	 * more friendly way to say a position is empty
	 */
	private static final int EMPTY_POS = 0;
	private static GameBoard gameBoard = null;

	/**
	 * integer representation of the board. Similar to the one in GUI, but this only
	 * holds 1,2, or 0 based on if player 1 controls a position or player 2 or
	 * neither (0)
	 */
	private int[][] board;

	/**
	 * number of columns and rows for the board
	 */
	private static final int ROWS = 6;
	private static final int COLUMNS = 7;

	/**
	 * private constructor. use getInstance instead.
	 */
	private GameBoard() {
		clearBoard();
	}

	/**
	 * Creates a game board singleton
	 *
	 * @return an instance of the board or creates a new one if there isn't one
	 */
	public static synchronized GameBoard getInstance() {
		if (gameBoard == null) {
			gameBoard = new GameBoard();
		}
		return gameBoard;
	}

	/**
	 * Check if the specific place(row,column) is full.
	 *
	 * @param row    - index of the row (zero based).
	 * @param column - index of the column (zero based).
	 * @return true if board position is already filled
	 */
	private boolean fullAtPos(int row, int column) throws OutsideBoardException {
		if (isValidColumn(column) && isValidRow(row)) {
			return board[row][column] != EMPTY_POS;
		} else {
			throw new OutsideBoardException();
		}
	}

	/**
	 * is the board full?
	 *
	 * @return true if full
	 */
	protected boolean isBoardFull() {
		boolean b = true;
		for (int i = 0; i < COLUMNS; i++) {
			try {
				b &= fullAtPos(0, i);
			} catch (OutsideBoardException ignored) {
			}
		}
		return b;
	}

	/**
	 * Puts the player's number in the bottom most empty spot in the column
	 *
	 * @param playerNumber - the Player's Number (non-zero)
	 * @param columnNumber - the column index to put the player/disc in it.
	 * @return true if player disc was added, false if column is full
	 *
	 * @throws OutsideBoardException if the position is invalid (out of bounds).
	 */
	protected boolean putDisc(int playerNumber, int columnNumber) throws OutsideBoardException {
		if (isValidColumn(columnNumber)) {
			for (int i = ROWS - 1; i >= 0; i--) {
				if (board[i][columnNumber] == EMPTY_POS) {
					board[i][columnNumber] = playerNumber;
					return true;
				}
			}
			return false;
		}
		throw new OutsideBoardException();
	}

	/**
	 * Get a position on the board
	 *
	 * @param rowIndex    - the row position
	 * @param columnIndex - the column position
	 * @return the player number (disc) for the position in question
	 *
	 * @throws OutsideBoardException on invalid position (out of bounds)
	 */
	protected int getBoardPos(int rowIndex, int columnIndex) throws OutsideBoardException {
		if (isValidColumn(columnIndex) && isValidRow(rowIndex)) {
			return board[rowIndex][columnIndex];
		}
		throw new OutsideBoardException();
	}

	/**
	 * Get the number of filled positions with discs in a column
	 *
	 * @param columnIndex - the column in question
	 * @return the number of filled positions
	 *
	 * @throws OutsideBoardException on invalid position (out of bounds)
	 */
	protected int getColumnHeight(int columnIndex) throws OutsideBoardException {
		if (!isValidColumn(columnIndex)) {
			throw new OutsideBoardException();
		}
		int i = 0;
		while (i < ROWS && board[i][columnIndex] == EMPTY_POS) {
			++i;
		}
		return ROWS - i;
	}

	/**
	 * Check the validity of the given column number (index).
	 *
	 * @param columnNumber - the column index to check
	 * @return true if the column is within the game board
	 */
	private boolean isValidColumn(int columnNumber) {
		return columnNumber >= 0 && columnNumber <= COLUMNS - 1;
	}

	/**
	 * Check the validity of the given row number (index).
	 *
	 * @param rowNumber - the row index to check
	 * @return true if the row is within the game board
	 */
	private boolean isValidRow(int rowNumber) {
		return rowNumber >= 0 && rowNumber <= ROWS - 1;
	}

	/**
	 * Get an array representation of the board
	 *
	 * @return the board as a 2d array
	 */
	protected int[][] getBoardArray() {
		return board;
	}

	protected final void clearBoard() {
		board = new int[ROWS][COLUMNS];
	}

	/**
	 * get the number of rows on the board
	 *
	 * @return number of rows
	 */
	protected int getRowNumber() {
		return ROWS;
	}

	/**
	 * get the number of columns on the board
	 *
	 * @return number of columns
	 */
	protected int getColumnNumber() {
		return COLUMNS;
	}

	protected void setWinningRow(Stack<Integer[]> s) {
		while (!s.isEmpty() && s.size() > 5) {
			s.pop();
		}
		for (int i = 0; i < 4; i++) {
			if (!s.isEmpty()) {
				Integer[] coord = s.pop();

				if (board[coord[0].intValue()][coord[1].intValue()] == 1) {
					board[coord[0].intValue()][coord[1].intValue()] = 3;
				} else if (board[coord[0].intValue()][coord[1].intValue()] == 2) {
					board[coord[0].intValue()][coord[1].intValue()] = 4;
				}
			}
		}
	}
}