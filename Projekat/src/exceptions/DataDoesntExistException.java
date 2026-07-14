package exceptions;

public class DataDoesntExistException extends Exception {
	public DataDoesntExistException() {
	}

	@Override
	public String getMessage() {
		return "There is no data loaded. Please first load data then start the simulation.";
	}
}
