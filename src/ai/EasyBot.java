package ai;

import engine.Move;

import java.util.ArrayList;
import java.util.Random;

public class EasyBot extends Player implements AI {

    public EasyBot(int playerNum) {
        super(playerNum, "Easy", "Computer");
    }

    public EasyBot(int playerNum, String name, String bot) {
        super(playerNum, name, bot);
    }

    /*
     * Returns a list of available columns to go
     */
    public ArrayList<Integer> getFreeMoves(int[][] board) {
        ArrayList<Integer> list = new ArrayList<>();

        for(int i = 0; i < board.length; i++) {
            if(board[0][i] == 0)
                list.add(i);
        }

        return list;
    }

    /**
     * Returns a random move to pick on the board
     *
     * @param board int[][]
     * @param lastMove Move
     * @return int
     */
    public int move(int[][] board, Move lastMove) {

        if(lastMove == null)
            return 3;

        ArrayList<Integer> list = getFreeMoves(board);

        Random rand = new Random();

        return list.get(rand.nextInt(list.size()));
    }
}
