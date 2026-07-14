package screen;

import java.awt.*;
import data.Airport;
import data.Flight;
import data.ParsedData;
import exceptions.DataDoesntExistException;

// Used for starting, pausing and restarting the simulation
public class SimulationPanel extends Panel {
	MainWindow mainWindow;
	private int currentTime = 0;
	private Thread timeThread;
	private Label timeLabel;
	private volatile boolean isRunning = false;

	public SimulationPanel(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		this.setLayout(new BorderLayout());

		Panel buttonPanel = new Panel();
		Button btnStart = new Button("Start");
		Button btnPause = new Button("Pause");
		Button btnReset = new Button("Reset");
		buttonPanel.add(btnStart);
		buttonPanel.add(btnPause);
		buttonPanel.add(btnReset);

		btnStart.addActionListener(e -> {
			try {
				ParsedData data = mainWindow.getParsedData();
				if (data == null || data.getAirportArr() == null || data.getFlightArr() == null) {
					throw new DataDoesntExistException();
				}
				mainWindow.setSimulation(true);
				isRunning = true;
				mainWindow.setInactivityPaused(true);

			} catch (DataDoesntExistException ex) {
				mainWindow.showErrorMessage(ex.getMessage());
			}
		});
		btnPause.addActionListener(e -> {
			mainWindow.setSimulation(false);
			isRunning = false;
			mainWindow.setInactivityPaused(false);
		});
		btnReset.addActionListener(e -> resetSimulation());

		Panel timePanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
		timeLabel = new Label("Simulation Time: " + getStringFromTime());
		timePanel.add(timeLabel);

		this.add(buttonPanel, BorderLayout.CENTER);
		this.add(timePanel, BorderLayout.EAST);

		timeThread = setupTimeThread();
		timeThread.start();
	}

	private void simulateTick() {
		
		ParsedData data = mainWindow.getParsedData();
		for (Flight flight : data.getFlightArr()) {
			flight.updateStatus(currentTime);
		}

		for (Airport airport : data.getAirportArr()) {
			airport.processTakeoffs(currentTime);
		}

		mainWindow.simulateTickMap(currentTime);
	}
	
	// Called when the reset button is pressed to reset the simulation
	public void resetSimulation() {
		mainWindow.setSimulation(false);
		isRunning = false;
		mainWindow.setInactivityPaused(false);
		currentTime = 0;
		updateLabel();
		simulateTick();
		ParsedData data = mainWindow.getParsedData();
		if (data != null) {
			if (data.getAirportArr() != null) {
				for (Airport a : data.getAirportArr()) {
					a.resetSimulation();
				}
			}

			if (data.getFlightArr() != null) {
				for (Flight f : data.getFlightArr()) {
					f.resetSimulation();
				}
			}
		}
	}

	// Updates the label with new simulation time
	private void updateLabel() {
		EventQueue.invokeLater(() -> {
			if (timeLabel != null) {
				timeLabel.setText("Simulation Time: " + getStringFromTime());
			}
		});
	}

	// Makes a thread that simulates time passed in simulation
	private Thread setupTimeThread() {
		Thread t = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(200);
					if (isRunning) {
						currentTime += 2;
						updateLabel();
						simulateTick();
					}
				} catch (InterruptedException e) {
					break;
				}
			}
		});
		t.setDaemon(true);
		return t;
	}

	// Formats time so it can be printed as HH:MM
	private String getStringFromTime() {
		int hours = (currentTime / 60) % 24;
		int minutes = currentTime % 60;
		return String.format("%02d:%02d", hours, minutes);
	}

}