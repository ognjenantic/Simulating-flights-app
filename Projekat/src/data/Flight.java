package data;

import exceptions.InvalidParametars;
import exceptions.SameAirportException;

public class Flight {
	private Airport from, to;
	private int duration, departure;

	private Coordinates curr;
	private Coordinates fromPoint;
	private Coordinates toPoint;

	private boolean isFlying = false;
	private boolean hasLanded = false;
	private int actualDepartureTime = -1;
	

	public Flight(Airport from, Airport to, int duration, int departure) {
		this.from = from;
		this.duration = duration;
		this.departure = departure;
		this.to = to;

		this.curr = new Coordinates(from.getX(), from.getY());
		this.fromPoint = new Coordinates(from.getX(), from.getY());
		this.toPoint = new Coordinates(to.getX(), to.getY());

	}

	public Flight(Airport from, Airport to, String departure, String duration)
			throws InvalidParametars, SameAirportException {

		try {
			String time = departure.trim();
			this.departure = checkDeparture(time);

			int d = Integer.parseInt(duration.trim());
			this.duration = d;

		} catch (NumberFormatException e) {
			throw new InvalidParametars("time duration");
		}

		if (from.getCode() == to.getCode())
			throw new SameAirportException(from);

		this.from = from;
		from.addFlight(this);
		this.to = to;

		this.curr = new Coordinates(from.getX(), from.getY());
		this.fromPoint = new Coordinates(from.getX(), from.getY());
		this.toPoint = new Coordinates(to.getX(), to.getY());
	}

	private void setFromParams(int x, int y) {
		fromPoint.setX(x);
		fromPoint.setY(y);
	}

	private void setToParams(int x, int y) {
		toPoint.setX(x);
		toPoint.setY(y);
	}

	//Checks validity of departure format
	private int checkDeparture(String time) throws InvalidParametars {
		String[] timeParts = time.split(":");
		int hours = Integer.parseInt(timeParts[0]);
		if (hours >= 24 || hours < 0) {
			throw new InvalidParametars("departure time");
		}
		int minutes = Integer.parseInt(timeParts[1]);
		if (minutes >= 60 || minutes < 0) {
			throw new InvalidParametars("departure time");
		}
		int departureInMinutes = hours * 60 + minutes;
		return departureInMinutes;
	}

	public Airport getFrom() {
		return from;
	}

	public Airport getTo() {
		return to;
	}

	public int getDuration() {
		return duration;
	}

	public int getDeparture() {
		return departure;
	}

	@Override
	public String toString() {
		return from.getCode() + ", " + to.getCode() + ", " + departure + ", " + duration + "\n";
	}

	public String getDepartureFormated() {
		int min = departure;
		int hours = min / 60;
		int minutes = min % 60;
		String formatted = String.format("%02d:%02d", hours, minutes);
		return formatted;
	}

	public void takeOff(int simTime) {
		this.isFlying = true;
		this.hasLanded = false;
		this.actualDepartureTime = simTime;
	}

	public boolean isFlying() {
		return isFlying;
	}

	public double getProgress(int simTime) {
		if (!isFlying)
			return 0.0;
		int elapsed = simTime - actualDepartureTime;
		return Math.min(1.0, (double) elapsed / getDuration());
	}

	public void resetSimulation() {
		curr.setX(from.getX());
		curr.setY(from.getY());
		isFlying = false;
		hasLanded = false;
		actualDepartureTime = -1;
	}

	public int getCurrentX() {
		return curr.getX();
	}

	public int getCurrentY() {
		return curr.getY();
	}

	public void updateStatus(int simTime) {
		if (isFlying) {
			if (simTime >= actualDepartureTime + getDuration()) {
				isFlying = false;
				hasLanded = true;
				curr.setX(to.getX());
				curr.setY(to.getY());
			} else {
				double progress = getProgress(simTime);
				curr.setX((int) Math.floor(fromPoint.getX() + progress * (toPoint.getX() - fromPoint.getX())));
				curr.setY((int) Math.floor(fromPoint.getY() + progress * (toPoint.getY() - fromPoint.getY())));
			}
		}
	}

}
