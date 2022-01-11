package engine;

/**
 * Version 3
 */

import javax.swing.*;
import javax.swing.border.Border;
import ai.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class GUI extends MouseAdapter implements ActionListener {
	/**
	 * Game disc icons *
	 */
	private static final String EMPTY_ICON = "img/empty4.png";
	private static final String RED_ICON = "img/red.png";
	private static final String RED_WIN_ICON = "img/redWin.png";
	private static final String YELLOW_ICON = "img/yellow.png";
	private static final String YELLOW_WIN_ICON = "img/yellowWin.png";

	private static final int IMG_SIZE = 100;
	private static final int Y_OFFSET = 75;
	private static final int X_OFFSET = 75;
	private GameEngine engine = null;
	private JFrame frame;
	private JPanel panel;

	/**
	 * array representation of the board of the board's images. Each slot tells what
	 * kind of image is in the slot *
	 */
	private JLabel[][] board = null;
	private JLabel score;
	private JLabel currentTurn = null;
	private JMenuBar menuBar;
	JMenuItem rematchItem;

	private Border off = BorderFactory.createLineBorder(Color.gray, 5);
	private Border on = BorderFactory.createLineBorder(Color.green, 5);

	JPanel scores;
	JLabel p1, p2, p1Score, p2Score, scoreDiv, scoreDiv2, drawScore;
	private int[] points = { 0, 0, 0 };

	private int speed = 500;
	Timer timer = new Timer(speed, this);

	private boolean waiting = false;
	private boolean gameOn = false;
	final Color yellow = new Color(245, 245, 0);
	final Color red = new Color(220, 0, 0);

	private String p1Name = "Human Player";
	private String p2Name = "Human Player";
	private int turn = 1;

	/************************************************
	 * 
	 * Set the AI players available in the ai package
	 * 
	 ************************************************/

	Object[] choices = { "Human Player", "Easy Bot", "Hard Bot" };

	private Player initPlayer(String p, int n) {
		if (p.equals("Easy Bot"))
			return new EasyBot(n);
		if (p.equals("Hard Bot"))
			return new HardBot(n);
		return new Player(n);
	}

	/**
	 * Possible values for a disc dropped into a board column Default value is None
	 * (empty)
	 */
	private enum Disc {
		None, Player1, Player2, Player1Win, Player2Win
	}

	/**
	 * Create a new graphical representation of the game. In other words, Create the
	 * graphical interface for playing the game.
	 *
	 * @param p1 - player 1
	 * @param p2 - player 2
	 */
	public GUI() {
		frame = new JFrame();
		frame.setTitle("Connect 4");
		score = new JLabel();
		menuBar = new JMenuBar();
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//frame.setUndecorated(true);
		readSettings();
		timer.setDelay(speed);
		engine = new GameEngine(initPlayer(p1Name, 1), initPlayer(p2Name, 2));
		createMenu();
		timer.start();
	}

	/**
	 * Start up the game by setting the board boundary, loading the images and
	 * adding the proper input listeners.
	 */
	public void startGame() {
		initBoard();
		frame.addMouseListener(this);
	}

	public void readSettings() {
		File f = new File("settings.txt");
		if (f.exists()) {
			try {
				FileReader r = new FileReader("settings.txt");
				BufferedReader b = new BufferedReader(r);
				p1Name = b.readLine();
				p2Name = b.readLine();
				speed = Integer.parseInt(b.readLine());
				b.close();
				r.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			writeSettings();
		}
	}

	public void writeSettings() {
		try {
			FileWriter r = new FileWriter("settings.txt");
			BufferedWriter b = new BufferedWriter(r);
			b.write(p1Name);
			b.newLine();
			b.write(p2Name);
			b.newLine();
			b.write("" + speed);
			b.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Create the top menu for the game.
	 */
	public final void createMenu() {
		JMenu file = new JMenu("File");
		JMenuItem newItem = new JMenuItem("New Game");
		newItem.addActionListener(new NewGame(frame));
		newItem.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		rematchItem = new JMenuItem("Rematch");
		rematchItem.addActionListener(new Rematch());
		rematchItem.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		rematchItem.setEnabled(false);
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ExitGame(frame));
		exitItem.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		file.add(newItem);
		file.add(rematchItem);
		file.add(exitItem);
		menuBar.add(file);

		JMenu options = new JMenu("Options");
		JMenuItem settingsItem = new JMenuItem("Delay");
		settingsItem.addActionListener(new Settings(frame));
		settingsItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		settingsItem.setSize(300, 200);
		options.add(settingsItem);

		JMenuItem player1Item = new JMenuItem("Player 1");
		player1Item.addActionListener(new SetPlayer(frame, 1));
		player1Item.setAccelerator(KeyStroke.getKeyStroke('1', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		player1Item.setSize(300, 200);
		options.add(player1Item);

		JMenuItem player2Item = new JMenuItem("Player 2");
		player2Item.addActionListener(new SetPlayer(frame, 2));
		player2Item.setAccelerator(KeyStroke.getKeyStroke('2', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		player2Item.setSize(300, 200);
		options.add(player2Item);

		menuBar.add(options);

		JMenu help = new JMenu("Help");
		JMenuItem helpItem = new JMenuItem("How to Play");
		helpItem.addActionListener(new HelpMenu(frame));
		helpItem.setAccelerator(KeyStroke.getKeyStroke('H', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		helpItem.setSize(300, 200);
		help.add(helpItem);
		menuBar.add(help);
		frame.setJMenuBar(menuBar);
	}

	/**
	 * Update the board locations with the proper images after a new disc is added
	 * to the board.
	 */
	public void updateBoard() {
		int[][] a = engine.getBoard().getBoardArray();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				Disc pos = Disc.values()[a[i][j]];
				switch (pos) {
				case None:
					board[i][j].setIcon(new ImageIcon(EMPTY_ICON));
					break;
				case Player1:
					board[i][j].setIcon(new ImageIcon(RED_ICON));
					break;
				case Player2:
					board[i][j].setIcon(new ImageIcon(YELLOW_ICON));
					break;
				case Player1Win:
					board[i][j].setIcon(new ImageIcon(RED_WIN_ICON));
					break;
				case Player2Win:
					board[i][j].setIcon(new ImageIcon(YELLOW_WIN_ICON));
					break;
				}
			}
		}
	}

	/**
	 * Called when the game is over to determine what to do next.
	 *
	 * @param winner - the game winner (if there is one)
	 */
	public void gameOver(Player winner) {
		rematchItem.setEnabled(true);
		displayGameOver();
		gameOn = false;
		points[winner.getInt()]++;
		p1Score.setText("" + points[1]);
		p2Score.setText("" + points[2]);
		drawScore.setText("" + points[0]);
	}

	public void updateScoreText() {
		int[] playerScores = engine.getScore();
		score.setText(String.format("Score: %s - %s", playerScores[0], playerScores[1]));
	}

	public void updateTurnText(Player currentPlayer) {
		currentPlayer = currentPlayer == null ? new Player(1) : currentPlayer;
		String color = currentPlayer.getInt() == 1 ? "red" : "yellow";
		currentTurn.setText(String.format("Current turn: Player %s (%s)", currentPlayer.getInt(), color));
	}

	/**
	 * Add the player's disc to the game board.
	 *
	 * @param columnNumber - the column the disc should be added to
	 * @return true if the disc was added (column was not full)
	 *
	 * @throws OutsideBoardException if the column is full or out of bounds
	 */
	public boolean putDisc(int columnNumber) throws OutsideBoardException {
		boolean putIsDone = engine.putDisc(new Move(columnNumber));

		if (putIsDone) {
			Player p = engine.isGameOver();
			updateBoard();
			updateScoreText();
			if (p != null) {
				gameOver(p);
			}
		} else {
			System.out.println("Invalid Move");
		}
		return putIsDone;
	}

	/**
	 * Determine where the mouse was just clicked and put a disc in that column
	 *
	 * @param mouseEvent the mouse event
	 */
	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		if (!(engine.getCurrentPlayer() instanceof AI) && waiting && gameOn) {
			waiting = false;
			try {
				// get the column that was clicked and putDisc down the correct image
				if (mouseEvent.getY() < Y_OFFSET + 20 + IMG_SIZE * engine.getRowNumber()
						&& mouseEvent.getY() > Y_OFFSET + 20
						&& mouseEvent.getX() < X_OFFSET + 0 + IMG_SIZE * engine.getColumnNumber()
						&& mouseEvent.getX() > X_OFFSET + 0) {
					putDisc((mouseEvent.getX() - (X_OFFSET + 0)) / IMG_SIZE);
				}
			} catch (OutsideBoardException ignored) {
			}
			if (gameOn)
				displayCurrent();
			waiting = true;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (engine.getCurrentPlayer() instanceof AI && waiting && gameOn) {
			waiting = false;
			try {
				int[][] board = engine.getBoard().getBoardArray();
				int[][] newBoard = new int[board.length][board[0].length];
				for (int r = 0; r < board.length; r++) {
					for (int c = 0; c < board[0].length; c++) {
						newBoard[r][c] = board[r][c];
					}
				}
				putDisc(((AI) (engine.getCurrentPlayer())).move(newBoard, engine.getLastMove()));
			} catch (OutsideBoardException ignored) {
			}
			if (gameOn)
				displayCurrent();
			waiting = true;
		}
	}

	/**
	 * Initialize the board. Set all board positions to empty and load the empty
	 * image icon for each position.
	 */
	private void initBoard() {
		rematchItem.setEnabled(false);
		engine.clearBoard();
		frame.getContentPane().removeAll();

		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		panel = new JPanel();
		panel.setBackground(Color.black);
		panel.setLayout(null);

		board = new JLabel[engine.getRowNumber()][engine.getColumnNumber()];

		// Player 1 Panel
		JPanel panel1 = new JPanel();
		panel1.setBackground(red);
		panel1.setBounds(X_OFFSET + (board[0].length + 1) * IMG_SIZE, Y_OFFSET, 325, 250);
		panel1.setLayout(new FlowLayout());
		panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		p1 = new JLabel();
		p1.setPreferredSize(new Dimension(270, 200));
		p1.setBackground(Color.white);
		p1.setFont(new Font("Serif", Font.BOLD, 40));
		p1.setHorizontalAlignment(SwingConstants.CENTER);
		p1.setText("<html><body style='text-align: center'>" + engine.getP1().getName() + "<br>"
				+ engine.getP1().getBot());
		p1.setOpaque(true);
		p1.setBorder(off);
		panel1.add(p1);

		panel.add(panel1);

		scores = new JPanel();
		scores.setBackground(Color.black);
		scores.setBounds(250, 725, 800, 150);
		scores.setLayout(null);
		scores.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		p1Score = new JLabel();
		p1Score = setLabel(0, 0, 200, 150, Color.red, Color.white, "" + points[1]);
		scores.add(p1Score);

		scoreDiv = new JLabel();
		scoreDiv = setLabel(200, 0, 100, 150, Color.white, Color.white, "-");
		scores.add(scoreDiv);

		p2Score = new JLabel();
		p2Score = setLabel(300, 0, 200, 150, yellow, Color.white, "" + points[2]);
		scores.add(p2Score);

		scoreDiv2 = new JLabel();
		scoreDiv2 = setLabel(500, 0, 100, 150, Color.white, Color.white, "-");
		scores.add(scoreDiv2);

		drawScore = new JLabel();
		drawScore = setLabel(600, 0, 200, 150, Color.white, Color.white, "" + points[0]);
		scores.add(drawScore);

		panel.add(scores);

		// Player 2 Panel
		JPanel panel2 = new JPanel();
		panel2.setBackground(yellow);
		panel2.setBounds(X_OFFSET + (board[0].length + 1) * IMG_SIZE, Y_OFFSET + 350, 325, 250);
		panel2.setLayout(new FlowLayout());
		panel2.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		p2 = new JLabel();
		p2.setPreferredSize(new Dimension(270, 200));
		p2.setBackground(Color.white);
		p2.setFont(new Font("Serif", Font.BOLD, 40));
		p2.setHorizontalAlignment(SwingConstants.CENTER);
		p2.setText("<html><body style='text-align: center'>" + engine.getP2().getName() + "<br>"
				+ engine.getP2().getBot());
		p2.setOpaque(true);
		p2.setBorder(off);
		panel2.add(p2);

		panel.add(panel2);

		// Gameboard
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				board[i][j] = new JLabel();
				board[i][j].setBounds(X_OFFSET + j * IMG_SIZE, Y_OFFSET + i * IMG_SIZE, IMG_SIZE, IMG_SIZE);
				board[i][j].setIcon(new ImageIcon(EMPTY_ICON));
				panel.add(board[i][j]);
			}
		}

		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setVisible(true);
	}

	private JLabel setLabel(int x, int y, int w, int h, Color fg, Color bg, String text) {
		JLabel label = new JLabel();

		label.setBounds(x, y, w, h);
		label.setBackground(bg);
		label.setForeground(fg);
		label.setFont(new Font("Serif", Font.BOLD, 150));
		label.setText(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);

		return label;
	}

	private void displayCurrent() {
		if (engine.getCurrentPlayer().getInt() == 1) {
			p1.setBorder(on);
			p2.setBorder(off);
		} else {
			p1.setBorder(off);
			p2.setBorder(on);
		}
	}

	private void displayGameOver() {
		p1.setBorder(off);
		p2.setBorder(off);
	}

	private void initGame() {
		engine = new GameEngine(initPlayer(p1Name, 1), initPlayer(p2Name, 2));
		initBoard();
		updateBoard();
		displayCurrent();
		waiting = true;
		gameOn = true;
	}

	private class NewGame implements ActionListener {
		private JFrame frame;

		NewGame(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int playAgain = JOptionPane.showConfirmDialog(frame,
					"Are you sure you want to start a new game?\nWarning: Scores will be reset.", "",
					JOptionPane.YES_NO_OPTION);

			if (playAgain == JOptionPane.YES_OPTION) {
				gameOn = false;
				points = new int[3];
				turn = 1;
				initGame();
			}
		}
	}

	private class Rematch implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			engine = new GameEngine(initPlayer(p1Name, 1), initPlayer(p2Name, 2));
			turn = turn == 1 ? 2 : 1;
			if (turn == 1)
				engine.p1Turn();
			else
				engine.p2Turn();
			initBoard();
			updateBoard();
			displayCurrent();
			waiting = true;
			gameOn = true;
		}
	}

	private class SetPlayer implements ActionListener {
		private JFrame frame;
		private int player;

		SetPlayer(JFrame frame, int player) {
			this.frame = frame;
			this.player = player;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String ans = (String) JOptionPane.showInputDialog(frame, "Choose Player " + player + ": ",
					"Player " + player, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

			if (ans != null) {
				gameOn = false;
				if (player == 1)
					p1Name = ans;
				else
					p2Name = ans;
				engine = new GameEngine(initPlayer(p1Name, 1), initPlayer(p2Name, 2));
				points = new int[3];
				initBoard();
				updateBoard();
			}

			writeSettings();
		}
	}

	private class Settings implements ActionListener {
		private JFrame frame;

		Settings(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object[] choices = { 0, 50, 100, 250, 500, 1000, 1500, 2000 };
			Integer ans = (Integer) JOptionPane.showInputDialog(frame, "Set delay in ms: ", "Delay",
					JOptionPane.QUESTION_MESSAGE, null, choices, speed);

			if (ans != null) {
				speed = ans.intValue();
				timer.setDelay(speed);
			}
			writeSettings();
		}
	}

	/**
	 * Context Menu for quitting the game
	 */
	private class ExitGame implements ActionListener {
		private JFrame frame;

		ExitGame(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int playAgain = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?", "",
					JOptionPane.YES_NO_OPTION);

			if (playAgain == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}

	/**
	 * Context Menu for bringing up the help menu
	 */
	private static class HelpMenu implements ActionListener {
		private JFrame frame;

		HelpMenu(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String msg = "Take turns dropping discs into the columns of the board. \n"
					+ "Objective is get of your colored discs in a row (up/down, left/right or diagonally). \n"
					+ "First player to get 4 in a row wins.\n";

			JOptionPane.showMessageDialog(frame, msg, "How to play", JOptionPane.OK_OPTION);
		}
	}
}