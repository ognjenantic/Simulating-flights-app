package data;

import java.util.LinkedList;
import java.util.List;

import exceptions.InvalidParametars;

public class Airport {
	private Coordinates place;
	private String code, name;
	private boolean selected = false;
	private boolean blinkState = false;
	private boolean visible = true;

	private List<Flight> departureList = new LinkedList<>();
	private List<Flight> allFlights = new LinkedList<>();
	private int lastTakeoffTime = -10;

	public Airport(String c, String n, int x, int y) {
		place = new Coordinates(x, y);
		this.code = c;
		this.name = n;
	}

	public Airport(String c, String n, String x, String y) throws InvalidParametars {
		place = new Coordinates(x, y);
		this.code = setCode(c);
		this.name = setName(n);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isBlinkState() {
		return blinkState;
	}

	public void setBlinkState(boolean blinkState) {
		this.blinkState = blinkState;
	}

	public String setCode(String c) throws InvalidParametars {
		String trimmed = c.trim();
		if (!trimmed.matches("^[A-Z]{3}$")) {
			throw new InvalidParametars("code");
		}
		return trimmed;
	}

	public String setName(String n) throws InvalidParametars {
		if (n == null || n.trim().isEmpty()) {
			throw new InvalidParametars("name");
		}
		return n;
	}

	public int getX() {
		return place.getX();
	}

	public int getY() {
		return place.getY();
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + ", " + code + ", " + place.getX() + "," + place.getY() + "\n";
	}

	public List<Flight> getDepartureQueue() {
		return departureList;
	}

	public void addFlight(Flight f) {
		allFlights.add(f);
		allFlights.sort((f1, f2) -> Integer.compare(f1.getDeparture(), f2.getDeparture()));

		departureList = new LinkedList<>(allFlights);
	}

	public void processTakeoffs(int currentSimTime) {
		if (departureList.isEmpty())
			return;

		Flight nextFlight = departureList.get(0);

		if (nextFlight.getDeparture() <= currentSimTime && (currentSimTime - lastTakeoffTime >= 10)) {
			departureList.remove(0);
			nextFlight.takeOff(currentSimTime);
			lastTakeoffTime = currentSimTime;
		}
	}

	public void resetSimulation() {
		lastTakeoffTime = -10;

		departureList = new LinkedList<>(allFlights);
	}
}
