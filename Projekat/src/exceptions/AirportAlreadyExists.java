package exceptions;

public class AirportAlreadyExists extends ParseException {
	private String name, code;

	public AirportAlreadyExists(String name, String code) {
		this.name = name;
		this.code = code;
	}

	@Override
	public String getMessage() {
		return "Airport " + name + " with code " + code + " already exists";
	}
}
