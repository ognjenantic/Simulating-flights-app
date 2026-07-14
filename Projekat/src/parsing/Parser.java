package parsing;

import data.ParsedData;
import exceptions.ParseException;

public interface Parser {
	ParsedData parse(String filePath) throws ParseException;

	void stringify(String filePath, ParsedData data) throws ParseException;
}