package exceptions;

public class ReadingFromFileException extends ParseException {
	public ReadingFromFileException() {
	}

	@Override
	public String getMessage() {
		return "Error while reading data from file.";
	}

}
