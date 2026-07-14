package screen;

import java.awt.*;
import java.awt.event.*;

import parsing.*;
import data.ParsedData;
import exceptions.LoadDataSimulationOnException;
import exceptions.ParseException;
//Menu add the top of the Main Window
public class MainMenu extends MenuBar {

	private CsvParser csvParser;
	private JsonParser jsonParser;
	private MainWindow mainWindow;

	public MainMenu(MainWindow mainWindow) {
		this.csvParser = CsvParser.getInstance();
		this.jsonParser = JsonParser.getInstance();
		this.mainWindow = mainWindow;

		Menu fileMenu = new Menu("File");
		MenuItem loadCsv = new MenuItem("Load csv file");
		MenuItem loadJson = new MenuItem("Load json file");

		MenuItem saveItemCsv = new MenuItem("Save as csv");
		MenuItem saveItemJson = new MenuItem("Save as json");

		Menu addMenu = new Menu("Add");
		MenuItem addAirportItem = new MenuItem("Add airport");
		MenuItem addFlightItem = new MenuItem("Add flight");

		loadCsv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleLoadFile(mainWindow, csvParser, "Choose a CSV file");
			}
		});

		loadJson.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleLoadFile(mainWindow, jsonParser, "Choose a JSON file");
			}
		});

		saveItemCsv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleSaveFile(mainWindow, csvParser, "Choose a CSV file");
			}
		});

		saveItemJson.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleSaveFile(mainWindow, jsonParser, "Choose a JSON file");

			}
		});

		addAirportItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AddAirportDialog dialog = new AddAirportDialog(mainWindow);
				dialog.setVisible(true);

			}
		});

		addFlightItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AddFlightDialog dialog = new AddFlightDialog(mainWindow);
				dialog.setVisible(true);
			}
		});

		fileMenu.add(loadCsv);
		fileMenu.add(loadJson);
		fileMenu.addSeparator();
		fileMenu.add(saveItemCsv);
		fileMenu.add(saveItemJson);

		this.add(fileMenu);

		addMenu.add(addAirportItem);
		addMenu.add(addFlightItem);

		this.add(addMenu);
	}

	//Handles when load file is selected
	private void handleLoadFile(MainWindow parent, Parser parser, String dialogTitle) {
		FileDialog fd = new FileDialog(parent, dialogTitle, FileDialog.LOAD);
		fd.setVisible(true);

		String directory = fd.getDirectory();
		String file = fd.getFile();

		if (directory != null && file != null) {
			String fullPath = directory + file;

			try {
				if(mainWindow.getSimulationStatus() == true)
					throw new LoadDataSimulationOnException();
				ParsedData data = parser.parse(fullPath);

				parent.updateData(data);

			} catch (ParseException ex) {
				parent.showErrorMessage("Parsing error: " + ex.getMessage());
			} catch (LoadDataSimulationOnException e) {
				parent.showErrorMessage("Loading error: : " + e.getMessage());
			}
		}
	}

	//Handles when save file is selected
	private void handleSaveFile(MainWindow parent, Parser parser, String dialogTitle) {
		FileDialog fd = new FileDialog(parent, dialogTitle, FileDialog.SAVE);
		fd.setVisible(true);

		String directory = fd.getDirectory();
		String file = fd.getFile();

		if (directory != null && file != null) {
			String fullPath = directory + file;

			try {
				ParsedData data = parent.getParsedData();
				parser.stringify(fullPath, data);

			} catch (ParseException ex) {
				parent.showErrorMessage("Parsing error: " + ex.getMessage());
			}
		}
	}
}