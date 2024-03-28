package game;

import java.io.Serializable;

public class Player implements Serializable {
	String name;
	pair location;
	pair lastLocation;
	int point;
	String direction;

	public Player(String name, pair loc, String direction) {
		this.name = name;
		this.location = loc;
		this.direction = direction;
		this.point = 0;
		this.lastLocation = loc;
	};
	
	public pair getLocation() {
		return this.location;
	}

	public void setLocation(pair p) {
		this.location=p;
	}

	public int getXpos() {
		return location.x;
	}
	public void setXpos(int xpos) {
		this.location.x = xpos;
	}
	public int getYpos() {
		return location.y;
	}
	public void setYpos(int ypos) {
		this.location.y = ypos;
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
	public String toString() {
		return name+":   "+point;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getPair() {
		return location.x + " " + location.y;
	}

	public pair getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(pair lastLocation) {
		this.lastLocation = lastLocation;
	}
}
