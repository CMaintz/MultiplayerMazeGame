package game2024;

public class Player {
	String name;
	int xpos;
	int ypos;
	int point;
	String direction;
	private boolean isFrozen = false;

	public Player(String name, int xpos, int ypos, String direction) {
		this.name = name;
		this.xpos = xpos;
		this.ypos = ypos;
		this.direction = direction;
		this.point = 0;
	}

	public boolean isFrozen() {
		return isFrozen;
	}

	public void setFrozen(boolean frozen) {
		isFrozen = frozen;
	}

	public int getXpos() {
		return xpos;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	public int getYpos() {
		return ypos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public void addPoints(int p) {
		point+=p;
	}

	public String getName() {
		return name;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String toString() {
		return name+":   "+point;
	}

	public String getState() {
		return this.getName() + " " + this.getXpos() + " " + this.getYpos() + " " + this.getDirection() + " " + this.getPoint();
	}
}
