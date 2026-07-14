package exceptions;

public class LoadDataSimulationOnException extends Exception{
	public LoadDataSimulationOnException() {}
	
	@Override
	public String getMessage() {
		return "Cant add data while simulation is on";
	}
}
