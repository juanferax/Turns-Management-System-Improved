package model;

public class Turn {

	private String turnID;
	private User user;
	private boolean inUse;
	private TurnType type;

	public Turn(String turnID, User user, TurnType type) {
		this.turnID = turnID;
		this.user = user;
		this.type = type;
		inUse = true;
	}

	/**
	 * @return the turnID
	 */
	public String getTurnID() {
		return turnID;
	}

	/**
	 * @param turnID the turnID to set
	 */
	public void setTurnID(String turnID) {
		this.turnID = turnID;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the inUse
	 */
	public boolean isInUse() {
		return inUse;
	}

	/**
	 * @param inUse the inUse to set
	 */
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	/**
	 * @return the type
	 */
	public TurnType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TurnType type) {
		this.type = type;
	}
}
