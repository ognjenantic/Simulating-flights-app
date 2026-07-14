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
import exceptions.ParseException;
import exceptions.ReadingFromFileException;

public class JsonParser implements Parser {
	private JsonParser() {
	}

	static private JsonParser instance = null;

	public static JsonParser getInstance() {
		if (instance == null) {
			instance = new JsonParser();
		}
		return instance;
	}

	@Override
	public ParsedData parse(String filePath) throws ParseException {

		if (!filePath.toLowerCase().endsWith(".json")) {
			throw new InvalidFileType(".json");
		}

		ParsedData data = new ParsedData();
		String section = "None";

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;

			while ((line = br.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty() || line.equals("{") || line.equals("}") || line.equals("],") || line.equals("]")) {
					continue;
				}

				if (line.contains("\"airports\":")) {
					section = "airports";
					continue;
				} else if (line.contains("\"flights\":")) {
					section = "flights";
					continue;
				}

				if (line.startsWith("{")) {
					String cleanLine = line.replace("{", "").replace("}", "").replace("\"", "");
					if (cleanLine.endsWith(",")) {
						cleanLine = cleanLine.substring(0, cleanLine.length() - 1);
					}
					String[] pairs = cleanLine.split(",");
					parseAndAddJsonElement(pairs, section, data);
				}
			}

		} catch (FileNotFoundException e) {
			throw new FileDoesntExist(filePath);
		} catch (IOException e) {
			throw new ParseException() {
				@Override
				public String getMessage() {
					return "Error while reading data from file.";
				}
			};
		}

		return data;
	}

	// Helper function that makes an array of strings from JSON input
	private void parseAndAddJsonElement(String[] pairs, String section, ParsedData data) throws ParseException {
		if (section.equals("airports")) {
			String code = "", name = "", x = "", y = "";
			for (String pair : pairs) {
				String[] kv = pair.split(":", 2);
				if (kv.length != 2)
					continue;
				String k = kv[0].trim();
				String v = kv[1].trim();
				switch (k) {
				case "code":
					code = v;
					break;
				case "name":
					name = v;
					break;
				case "x":
					x = v;
					break;
				case "y":
					y = v;
					break;
				}
			}
			
			data.addAirport(new String[] { code, name, x, y });

		} else if (section.equals("flights")) {
			String from = "", to = "", dep = "", dur = "";
			for (String pair : pairs) {
				String[] kv = pair.split(":", 2);
				if (kv.length != 2)
					continue;
				String k = kv[0].trim();
				String v = kv[1].trim();
				switch (k) {
				case "from":
					from = v;
					break;
				case "to":
					to = v;
					break;
				case "departure":
					dep = v;
					break;
				case "duration":
					dur = v;
					break;
				}
			}
			data.addFlight(new String[] { from, to, dep, dur });
		}
	}

	@Override
	public void stringify(String filePath, ParsedData data) throws ParseException {
		List<Flight> flights = data.getFlightArr();
		List<Airport> airports = data.getAirportArr();

		try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
			pw.println("{");
			stringifyAirports(pw, airports);
			pw.println(",");
			stringifyFlights(pw, flights);
			pw.println();
			pw.println("}");

		} catch (IOException e) {
			throw new ReadingFromFileException();
		}
	}

	private void stringifyAirports(PrintWriter pw, List<Airport> airports) {
		pw.println("\"airports\":[");
		for (int i = 0; i < airports.size(); i++) {
			Airport a = airports.get(i);
			pw.print("{\"code\":\"" + a.getCode() + "\",\"name\":\"" + a.getName() + "\",\"x\":" + a.getX() + ",\"y\":"
					+ a.getY() + "}");
			if (i < airports.size() - 1)
				pw.println(",");
			else
				pw.println();
		}
		pw.print("]");
	}

	private void stringifyFlights(PrintWriter pw, List<Flight> flights) {
		pw.println("\"flights\":[");
		for (int i = 0; i < flights.size(); i++) {
			Flight f = flights.get(i);
			pw.print("{\"from\":\"" + f.getFrom().getCode() + "\",\"to\":\"" + f.getTo().getCode()
					+ "\",\"departure\":\"" + f.getDepartureFormated() + "\",\"duration\":" + f.getDuration() + "}");
			if (i < flights.size() - 1)
				pw.println(",");
			else
				pw.println();
		}
		pw.print("]");
	}
}