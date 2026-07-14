package exceptions;

public class AirportNotExists extends ParseException {
	private String code;

	public AirportNotExists(String code, int numOfLine) {
		this.code = code;
	}

	@Override
	public String getMessage() {
		return "Airport with code " + code + " doesnt exist";
	}
}
