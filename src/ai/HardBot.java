package ai;

import engine.Move;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class HardBot extends Player implements AI{

	/*
	 * Used for a recursive algorithm to determine the best move
	 */
	private class GameTree {

		/*
		 * Encapsulates all important values for each board configuration
		 */
		private class Node {

			int[][] board;
			Map<Node, Integer> children = new LinkedHashMap<>(); //Holds all child nodes as keys and the previous move as the value
			int win = 0; // If the current board has a winner, this is their value
			int score = 0; // The higher the value, the more importance the board has
			int depth; // How far down the node is in the tree


			public Node(int[][] map, int depth) {
				this.board = map;
				this.depth = depth;
			}
		}

		Node root;
		private int player; //The AI's player value
		private int oppPlayer; //The opponent player's value

		final int ROWS = 6, COLS = 7; //Dimensions

		public GameTree(int[][] board, int player) {
			root = new Node(board, 0);
			this.player = player;

			//Setting the correct player values
			if(player == 1)
				oppPlayer = 2;
			else
				oppPlayer = 1;
		}

		/*
		 * Creates all children and leaf nodes with winning Nodes being leaves.
		 * Also creates the score of leaf nodes giving higher priority to not
		 * picking losing branches
		 */
		public void makeTree(Node root, int times) {

			//if the depth is even, the board resulted in an opponents move
			if(root.depth % 2 == 0) {

				//if the opponent won
				if(detectPlayerWin(root.board, oppPlayer)) {
					root.win = oppPlayer;
					root.score = -11;
					return;
				}

			} else { //if the depth is odd, the board resulted in the AI's move

				//if the AI won
				if(detectPlayerWin(root.board, player)) {
					root.win = player;
					root.score = 10;
					return;
				}

			}

			if(times <= 0) return; //Base case

			ArrayList<Integer> moves = getFreePositions(root.board); //Available moves

			if(root.depth % 2 == 0) {

				//Recursively calling method on all created child nodes
				for(int col : moves) {
					Node child = new Node(placePiece(root.board, col, player), root.depth + 1);
					root.children.put(child, col);
					makeTree(child, times - 1);
				}
			} else {

				//Recursively calling method on all created child nodes
				for(int col : moves) {
					Node child = new Node(placePiece(root.board, col, oppPlayer), root.depth + 1);
					root.children.put(child, col);
					makeTree(child, times - 1);
				}
			}
		}

		/*
		 * Recursive algorithm to set each node to the sum of the children's scores.
		 * Then the score gets divided by the depth so that the more recent wins get higher
		 * priority
		 */
		public void setNodeScores(Node root) {

			int scoreSum = 0;

			//Going down to the leaf nodes
			if(!root.children.isEmpty()) {
				for(Node child : root.children.keySet()) {
					setNodeScores(child);
				}
			} else {
				return;
			}

			//Getting the sum of the children scores
			for(Node child : root.children.keySet()) {
				scoreSum += child.score;
			}

			//Giving higher priority to the nodes closer to the root
			root.score = scoreSum / (root.depth + 1);
		}

		/*
		 * Returns an array of the first level of node's scores
		 */
		public int[] getNodeScores(Node root) {

			int[] arr = new int[7]; //Index corresponds to column

			for(Node child : root.children.keySet()) {
				arr[root.children.get(child)] = child.score;
			}

			return arr;
		}

		/*
		 * Returns true if it detects that a player has a win starting at
		 * that specific location
		 */
		private boolean detectPlayerWin(int[][] board, int player) {
			for(int row = 0; row < ROWS; row++) {
				for(int col = 0; col < COLS; col++) {
					if(board[row][col] == player) {
						if(detectPieceWin(board, row, col))
							return true;
					}
				}
			}
			return false;
		}

		/*
		 * Returns true if the piece at the row and col is the leftmost or topmost
		 * piece of a "4 in a row", win
		 */
		private boolean detectPieceWin(int[][] board, int row, int col) {

			//current piece
			int cur = board[row][col];

			//Go down
			boolean win = true;
			for(int i = 0; i < 4; i++) {
				if(!(inBounds(row + i, col) && cur == board[row + i][col])) {
					win = false;
					break;
				}
			}
			if(win) return true;


			//Go right
			win = true;
			for(int i = 0; i < 4; i++) {
				if(!(inBounds(row, col + i) && cur == board[row][col + i])) {
					win = false;
					break;
				}
			}
			if(win) return true;

			//Go down diagonal
			win = true;
			for(int i = 0; i < 4; i++) {
				if(!(inBounds(row + i, col + i) && cur == board[row + i][col + i])) {
					win = false;
					break;
				}

			}
			if(win) return true;

			//Go up diagonal
			win = true;
			for(int i = 0; i < 4; i++) {
				if(!(inBounds(row - i, col + i) && cur == board[row - i][col + i])) {
					win = false;
					break;
				}
			}

			return win;
		}

		/*
		 * Returns the win that is in the children nodes at depth 1. If there is
		 * none, return -1
		 */
		public int win() {
			for(Node child : root.children.keySet()) {
				if (child.win == player)
					return root.children.get(child);
			}

			return -1;
		}


		/*
		 * Returns a List of available possible columns to pick, meaning the ones that
		 * it does not return are currently full
		 */
		private ArrayList<Integer> getFreePositions(int[][] board) {
			ArrayList<Integer> moves = new ArrayList<>();

			for(int i = 0; i < board[0].length; i++) {

				//Checking top row for empty values
				if(board[0][i] == 0)
					moves.add(i);

			}

			return moves;
		}


		/*
		 * Returns a configuration of the passed board with the 'player's piece placed
		 * into the passed column
		 */
		private int[][] placePiece(int[][] board, int col, int player) {

			//Creating a copy of the passed board to return it
			int[][] result = new int[board.length][board[0].length];
			for(int i = 0; i < board.length; i++)
				result[i] = board[i].clone();

			//Goes down the board until it finds the piece it is supposed to go overtop of
			for(int row = 0; row < board.length; row++) {
				if(board[row][col] != 0) {
					result[row - 1][col] = player;
					return result;
				}
			}

			//Placing the piece into an empty column
			result[result.length - 1][col] = player;
			return result;
		}

		/*
		 * Returns true if the row and column are within the bounds of the board
		 */
		private boolean inBounds(int row, int col) {
			return row >= 0 && row < ROWS && col >= 0 && col < COLS;
		}

	}

	//Constructors
	public HardBot(int playerNum) {
		super(playerNum, "Hard", "Computer");
	}
	public HardBot(int playerNum, String name, String bot) {
		super(playerNum, name, bot);
	}

	/**
	 * Returns the optimal move for the AI to win by seeing into the
	 * future of the game
	 *
	 * @param board the current state of the game
	 * @param lastMove the last move that that has been played in the game, null if its the start
	 * @return int - the optimal move
	 */
	public int move(int[][] board, Move lastMove) {

		//If the game just started, of in the middle
		if(lastMove == null)
			return 3;

		//creating the game tree and initializing its node's values
		GameTree gt = new GameTree(board, PLAYER);
		gt.makeTree(gt.root, 7);
		gt.setNodeScores(gt.root);

		//If there is an immediate win option, pick it
		if(gt.win() != -1)
			return gt.win();

		//Getting an array in which the index corresponds to the columns and hold the optimal moves
		int[] arr = gt.getNodeScores(gt.root);

		//Return the optimal column
		return pickCol(board, arr);
	}

	/*
	 * Pick the highest value in the array and return its index as
	 * long as the column at the index is not full
	 */
	private int pickCol(int[][] board, int[] bestChoices) {
		int max = Integer.MIN_VALUE, index = 0;

		//Getting the max value
		for(int i = 0; i < bestChoices.length; i++) {
			if(!colOccupied(board, i) && bestChoices[i] > max) {
				index = i;
				max = bestChoices[i];
			}
		}

		//return the max values index
		return index;
	}

	/*
	 * Returns if the column in the board is currently occupied
	 */
	private boolean colOccupied(int[][] board, int col){
		return board[0][col] != 0;
	}
}