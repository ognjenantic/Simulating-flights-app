package data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import exceptions.AirportAlreadyExists;
import exceptions.AirportNotExists;
import exceptions.InvalidParametars;
import exceptions.SameAirportException;

public class ParsedData {
	private List<Airport> airportArr;
	private List<Flight> flightArr;

	public ParsedData(List<Airport> airportArr, List<Flight> flightArr) {
		this.airportArr = airportArr;
		this.flightArr = flightArr;
	}

	public ParsedData() {
		this.airportArr = new ArrayList<>();
		this.flightArr = new ArrayList<>();
	}

	public List<Airport> getAirportArr() {
		return airportArr;
	}

	public List<Flight> getFlightArr() {
		return flightArr;
	}

	public Airport findAirport(String code) {
		for (Airport airport : airportArr) {
			if (airport.getCode().equals(code)) {
				return airport;
			}
		}
		return null;
	}

	public void addAirport(String[] parts) throws AirportAlreadyExists, InvalidParametars {
		String code = parts[0].trim();
		String name = parts[1].trim();
		String x = parts[2];
		String y = parts[3];

		Airport a1 = findAirport(code);
		if (a1 != null) {
			throw new AirportAlreadyExists(a1.getName(), a1.getCode());
		}
		this.airportArr.add(new Airport(code, name, x, y));
	}

	public void addFlight(String[] parts) throws AirportNotExists, InvalidParametars, SameAirportException {
		String from = parts[0].trim();
		Airport a1 = findAirport(from);
		if (a1 == null) {
			throw new AirportNotExists(from, 0);
		}

		String to = parts[1].trim();
		Airport a2 = findAirport(to);
		if (a2 == null) {
			throw new AirportNotExists(to, 0);
		}

		flightArr.add(new Flight(a1, a2, parts[2], parts[3]));
		sortFlights();
	}

	private void sortFlights() {
		if (flightArr == null || flightArr.isEmpty())
			return;

		flightArr.sort(Comparator.comparingInt(Flight::getDeparture).thenComparingInt(Flight::getDuration));
	}

	@Override
	public String toString() {
		return "" + airportArr + ", " + flightArr;
	}
}