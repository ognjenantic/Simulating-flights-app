package exceptions;

public class InvalidFileType extends ParseException {
	private String extension;

	public InvalidFileType(String extension) {
		this.extension = extension;
	}

	@Override
	public String getMessage() {
		return "File type is not " + extension + ".";
	}
}
