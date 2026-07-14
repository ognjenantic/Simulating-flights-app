package exceptions;

public class FileDoesntExist extends ParseException {
	private String filePath;

	public FileDoesntExist(String filepath) {
		filePath = filepath;
	}

	@Override
	public String getMessage() {
		return "File " + filePath + " doesnt exist.";
	}
}
