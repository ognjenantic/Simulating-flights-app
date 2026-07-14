package screen;

import java.awt.*;
import java.awt.event.*;

import data.Airport;
import data.Flight;
import data.ParsedData;

//The main application window for the Flight Traffic Simulation.
public class MainWindow extends Frame {

	private MapCanvas mapCanvas;
	private TableFrame dataPanel;
	private SimulationPanel simulationPanel;
	private Panel panelBottom;
	private Label inactivityLabel;

	private InactivityController inactivityController;

	private ParsedData data;
	private boolean simulationIsOn = false;

	public MainWindow() {
		super("Flight Traffic Simulation");
		this.setSize(1024, 768);
		this.setLayout(new BorderLayout());
		this.setMenuBar(new MainMenu(this));

		initComponents();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				shutdownApplication();
			}
		});

		inactivityController = new InactivityController(this, inactivityLabel, this::shutdownApplication);
		inactivityController.start();

		data = null;
	}

	// Builds and arranges the main window's child components.
	private void initComponents() {
		mapCanvas = new MapCanvas(this);
		this.add(mapCanvas);

		dataPanel = new TableFrame(this);
		this.add(dataPanel, BorderLayout.EAST);

		simulationPanel = new SimulationPanel(this);
		this.add(simulationPanel, BorderLayout.NORTH);

		panelBottom = new Panel(new BorderLayout());
		inactivityLabel = new Label("Inactivity: 60s");
		panelBottom.add(inactivityLabel, BorderLayout.EAST);
		this.add(panelBottom, BorderLayout.SOUTH);
	}

	// Tears down the inactivity controller, closes the window, and exits.
	private void shutdownApplication() {
		inactivityController.stop();
		this.dispose();
		System.exit(0);
	}

	// Replaces the currently loaded data set and refreshes dependent views.
	public void updateData(ParsedData data) {
		this.data = data;

		if (dataPanel != null) {
			dataPanel.updateData(data);
		}
		if (mapCanvas != null) {
			mapCanvas.updateData(data);
		}

		dataPanel.validate();
		repaint();
	}

	public ParsedData getParsedData() {
		return data;
	}

	// Pauses or resumes the inactivity countdown, e.g. while a simulation runs.
	public void setInactivityPaused(boolean paused) {
		inactivityController.setPaused(paused);
	}

	// Notifies the map that an active filter changed and it needs to redraw.
	public void notifyFilterChanged() {
		if (mapCanvas != null) {
			mapCanvas.repaint();
		}
	}

	// Advances the simulation to the given simulated time and refreshes the map.
	public void simulateTickMap(int currSimulatedTime) {
		if (mapCanvas != null) {
			mapCanvas.setSimulationTime(currSimulatedTime);
			mapCanvas.repaint();
		}
	}

	// Displays a modal error dialog with the given message.
	public void showErrorMessage(String message) {
		EventQueue.invokeLater(() -> {
			ErrorDialog errorDialog = new ErrorDialog(this, message);
			errorDialog.setVisible(true);
		});
	}

	public void setSimulation(boolean isOn) {
		simulationIsOn = isOn;
	}

	public boolean getSimulationStatus() {
		return simulationIsOn;
	}

	public static void main(String[] args) {
		MainWindow window = new MainWindow();
		window.setVisible(true);
	}
}
