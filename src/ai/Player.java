package ai;

public class Player {
	protected final int PLAYER;
	protected final int OPPONENT;
	protected final int EMPTY = 0;
	public int wins = 0;
	private String name;
	private String botName;

	/**
	 * A player object to keep track of game players
	 *
	 * @param PLAYER - the id for the player
	 */
	public Player(int PLAYER) {
		this(PLAYER, "Player " + PLAYER, "");
	}

	public Player(int PLAYER, String name, String botName) {
		this.PLAYER = PLAYER;
		this.name = name;
		this.botName = botName;
		this.OPPONENT = PLAYER == 1 ? 2 : 1;
	}

	/**
	 * Get this player's current ID
	 *
	 * @return the player ID (i.e. player 1 or player 2)
	 */
	public int getInt() {
		return PLAYER;
	}

	public String getName() {
		return name;
	}

	public String getBot() {
		return botName;
	}

}
