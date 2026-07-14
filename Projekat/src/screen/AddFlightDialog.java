package screen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import data.ParsedData;
import exceptions.LoadDataSimulationOnException;
import exceptions.ParseException;
import parsing.CsvParser;
//Dialog that pops up when add Flight is selected in menu
public class AddFlightDialog extends Dialog {
	private TextField tfFrom, tfTo, tfDuration, tfDeparture;
	private Button btnAdd, btnCancel;
	private MainWindow mainWindow;

	public AddFlightDialog(MainWindow mainWindow) {
		super(mainWindow, "Add new flight", true);
		this.mainWindow = mainWindow;

		this.setSize(400, 200);
		this.setLocationRelativeTo(mainWindow);
		this.setLayout(new GridLayout(5, 2, 10, 10));

		initComponents();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

	}
	
	private void initComponents() {
		this.add(new Label("From (3 letters):"));
		tfFrom = new TextField();
		this.add(tfFrom);

		this.add(new Label("To (3 letters):"));
		tfTo = new TextField();
		this.add(tfTo);

		this.add(new Label("Departure (HH:MM):"));
		tfDeparture = new TextField();
		this.add(tfDeparture);

		this.add(new Label("Duration:"));
		tfDuration = new TextField();
		this.add(tfDuration);

		initButtons();
	}
	
	private void initButtons() {
		btnCancel = new Button("Cancel");
		btnAdd = new Button("Add");
		this.add(btnCancel);
		this.add(btnAdd);

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String[] parts = { tfFrom.getText(), tfTo.getText(), tfDeparture.getText(), tfDuration.getText() };

					if(mainWindow.getSimulationStatus() == true)
						throw new LoadDataSimulationOnException();
					ParsedData data = mainWindow.getParsedData();
					if(data == null)
						data = new ParsedData();
					data.addFlight(parts);
					mainWindow.updateData(data);

					dispose();

				} catch (ParseException ex) {
					showErrorDialog(ex.getMessage());
				}catch (LoadDataSimulationOnException ex) {
					showErrorDialog(ex.getMessage());
				}
			}
		});
	}
	// Dialog shown when there is a faulty parametar
	private void showErrorDialog(String message) {
		final Dialog errDialog = new Dialog(this, "Error", true);
		errDialog.setLayout(new FlowLayout());
		errDialog.setSize(350, 100);
		errDialog.setLocationRelativeTo(this);

		errDialog.add(new Label(message));
		Button okBtn = new Button("OK");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				errDialog.dispose();
			}
		});
		errDialog.add(okBtn);
		errDialog.setVisible(true);
	}
}
