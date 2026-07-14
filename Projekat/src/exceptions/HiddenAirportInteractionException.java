package exceptions;

public class HiddenAirportInteractionException extends Exception {
	public HiddenAirportInteractionException() {
	}

	@Override
	public String getMessage() {
		return "You clicked on a filtered airport. Enable it in the right panel to interact.";
	}
}