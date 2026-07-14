package screen;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import data.ParsedData;
import data.Airport;
import data.Flight;

//Used for table for flights and checkboxes for Airports
public class TableFrame extends Panel {
	private List flightList;
	private Panel filterPanel;
	private MainWindow mainWindow;

	public TableFrame(MainWindow mWindow) {
		this.mainWindow = mWindow;
		this.setLayout(new GridLayout(2, 1));
		this.setPreferredSize(new Dimension(250, 0));

		flightList = new List();
		this.add(flightList);

		filterPanel = new Panel(new GridLayout(0, 1, 0, 2));

		Panel wrapperPanel = new Panel(new BorderLayout());
		wrapperPanel.add(filterPanel, BorderLayout.NORTH);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(wrapperPanel);

		this.add(scrollPane);
	}

	// Updates the flights and airports that are shown
	public void updateData(ParsedData data) {
		flightList.removeAll();
		filterPanel.removeAll();

		if (data == null)
			return;

		if (data.getFlightArr() != null) {
			for (Flight f : data.getFlightArr()) {
				String output = f.getFrom().getCode() + " -> " + f.getTo().getCode() + " " + f.getDepartureFormated()
						+ " (" + f.getDuration() + " min)";
				flightList.add(output);
			}
		}

		if (data.getAirportArr() != null) {
			for (Airport a : data.getAirportArr()) {
				makeCheckbox(a);
			}
		}

		this.getParent().validate();
		this.repaint();
	}

	// Makes a checkbox for one airport
	private void makeCheckbox(Airport a) {
		Checkbox cb = new Checkbox(a.getCode() + " - " + a.getName(), a.isVisible());
		cb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean isChecked = (e.getStateChange() == ItemEvent.SELECTED);
				a.setVisible(isChecked);

				if (mainWindow != null) {
					mainWindow.notifyFilterChanged();
				}

			}
		});
		filterPanel.add(cb);
	}
}