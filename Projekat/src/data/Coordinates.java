package data;

import exceptions.InvalidParametars;

class Coordinates {
	private int x, y;

	Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	Coordinates(String x, String y) throws InvalidParametars {
		this.x = setX(x);
		this.y = setY(y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int setY(String y) throws InvalidParametars {
		int y1;
		try {
			y1 = Integer.parseInt(y.trim());
			if (y1 > 90 || y1 < -90) {
				throw new InvalidParametars("y");
			}
		} catch (NumberFormatException e) {
			throw new InvalidParametars("y");
		}
		return y1;
	}

	public int setX(String x) throws InvalidParametars {
		int x1;
		try {
			x1 = Integer.parseInt(x.trim());
			if (x1 > 180 || x1 < -180) {
				throw new InvalidParametars("x");
			}
		} catch (NumberFormatException e) {
			throw new InvalidParametars("x");
		}
		return x1;
	}

}
