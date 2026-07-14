package exceptions;

import data.Airport;

public class SameAirportException extends ParseException {
	private Airport air;

	public SameAirportException(Airport a) {
		air = a;
	}

	@Override
	public String getMessage() {
		return "Flight cant start and land at the same airport: " + air.getCode();
	}
}
