package exceptions;

import data.Flight;

public class FlightUnselectedAirport extends Exception {
	private Flight flight;
	private int fromTo;

	public FlightUnselectedAirport(Flight f, int fromTo) {
		this.flight = f;
		this.fromTo = fromTo;
	}

	@Override
	public String getMessage() {
		if (fromTo == 0)
			return flight.getFrom().getCode() + " is unselected";
		else
			return flight.getTo().getCode() + " is unselected";

	}
}
