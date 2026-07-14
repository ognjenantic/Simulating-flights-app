package exceptions;

public class InvalidParametars extends ParseException {
	String typeOfParametar;

	public InvalidParametars(String typeOfParametar) {
		this.typeOfParametar = typeOfParametar;
	}

	@Override
	public String getMessage() {
		return typeOfParametar + " has an invalid value.";
	}
}
