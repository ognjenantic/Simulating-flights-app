package exceptions;

public class InvalidNumOfParametars extends ParseException {
	private int actualSize, requiredSize;
	private String filePath;

	public InvalidNumOfParametars(int actualSize, int requiredSize, String filePath) {
		this.actualSize = actualSize;
		this.requiredSize = requiredSize;
		this.filePath = filePath;
	}

	@Override
	public String getMessage() {
		return "There is " + actualSize + " instead of " + requiredSize
				+ " parametars";
	}
}
