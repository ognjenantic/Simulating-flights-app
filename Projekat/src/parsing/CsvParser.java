package parsing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import data.Flight;
import data.Airport;
import data.ParsedData;
import exceptions.FileDoesntExist;
import exceptions.InvalidFileType;
import exceptions.InvalidNumOfParametars;
import exceptions.ParseException;
import exceptions.ReadingFromFileException;

public class CsvParser implements Parser {
	private CsvParser() {
	}

	static private CsvParser instance = null;

	public static CsvParser getInstance() {
		if (instance == null) {
			instance = new CsvParser();
		}
		return instance;
	}

	@Override
	public ParsedData parse(String filePath) throws ParseException {

		if (!filePath.toLowerCase().endsWith(".csv")) {
			throw new InvalidFileType(".csv");
		}

		ParsedData data = new ParsedData();
		String section = "None";

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;

				if (line.startsWith("# AIRPORTS")) {
					section = "airports";
					continue;
				} else if (line.startsWith("# FLIGHTS")) {
					section = "flights";
					continue;
				}

				if (line.startsWith("FROM") || line.startsWith("CODE"))
					continue;

				String[] parts = line.split(",");

				if (parts.length != 4) {
					throw new InvalidNumOfParametars(parts.length, 4, filePath);
				}

				if (section.equals("flights")) {
					data.addFlight(parts);
				} else if (section.equals("airports")) {
					data.addAirport(parts);
				}
			}
		} catch (FileNotFoundException e) {
			throw new FileDoesntExist(filePath);
		} catch (IOException e) {
			throw new ReadingFromFileException();
		}
		return data;
	}

	@Override
	public void stringify(String filePath, ParsedData data) throws ParseException {
		List<Flight> flights = data.getFlightArr();
		List<Airport> airports = data.getAirportArr();

		try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
			stringifyAirports(pw, airports);
			pw.println();
			stringifyFlights(pw, flights);

		} catch (IOException e) {
			throw new ParseException() {
				public String getMessage() {
					return "Error while writing data to file.";
				}
			};
		}
	}

	private void stringifyAirports(PrintWriter pw, List<Airport> airports) {
		pw.println("# AIRPORTS");
		pw.println("CODE,NAME,X,Y");
		for (Airport a : airports) {
			pw.println(a.getCode() + "," + a.getName() + "," + a.getX() + "," + a.getY());
		}
	}

	private void stringifyFlights(PrintWriter pw, List<Flight> flights) {
		pw.println("# FLIGHTS");
		pw.println("FROM,TO,DEPARTURE,DURATION");
		for (Flight f : flights) {
			pw.println(f.getFrom().getCode() + "," + f.getTo().getCode() + "," + f.getDepartureFormated() + ","
					+ f.getDuration());
		}
	}
}