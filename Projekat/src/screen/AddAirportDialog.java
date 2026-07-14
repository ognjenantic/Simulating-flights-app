package screen;

import java.awt.*;
import java.awt.event.*;

import data.ParsedData;
import exceptions.InvalidParametars;
import exceptions.LoadDataSimulationOnException;
import parsing.CsvParser;
import exceptions.AirportAlreadyExists;

//Dialog that pops up when you select add Airport in menu
public class AddAirportDialog extends Dialog {

	private TextField tfCode, tfName, tfX, tfY;
	private Button btnAdd, btnCancel;
	private MainWindow mainWindow;

	public AddAirportDialog(MainWindow parent) {
		super(parent, "Add New Airport", true);
		this.mainWindow = parent;

		this.setSize(400, 200);
		this.setLocationRelativeTo(parent);
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
		this.add(new Label(" Code (3 letters):"));
		tfCode = new TextField();
		this.add(tfCode);

		this.add(new Label(" Name:"));
		tfName = new TextField();
		this.add(tfName);

		this.add(new Label(" X Coordinate (-180 to 180):"));
		tfX = new TextField();
		this.add(tfX);

		this.add(new Label(" Y Coordinate (-90 to 90):"));
		tfY = new TextField();
		this.add(tfY);

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
					String[] parts = { tfCode.getText(), tfName.getText(), tfX.getText(), tfY.getText() };

					if(mainWindow.getSimulationStatus() == true)
						throw new LoadDataSimulationOnException();
					ParsedData data = mainWindow.getParsedData();
					if (data == null)
						data = new ParsedData();
					data.addAirport(parts);

					mainWindow.updateData(data);
					dispose();

				} catch (InvalidParametars ex) {
					showErrorDialog("Invalid Parameter: " + ex.getMessage());
				} catch (AirportAlreadyExists ex) {
					showErrorDialog("Duplicate Error: " + ex.getMessage());
				} catch (LoadDataSimulationOnException ex) {
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