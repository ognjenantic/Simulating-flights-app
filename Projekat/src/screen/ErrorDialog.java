package screen;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//A modal dialog for surfacing a single error message to the user, with an "OK"
//button (and window-close) to dismiss it.
public class ErrorDialog extends Dialog {

	public ErrorDialog(Frame owner, String message) {
		super(owner, "Interaction Error", true);
		this.setLayout(new FlowLayout());
		this.setSize(450, 150);
		this.setLocationRelativeTo(owner);

		Label lblMessage = new Label(message);
		lblMessage.setForeground(Color.RED);
		this.add(lblMessage);

		Button btnOk = new Button("OK");
		btnOk.addActionListener(e -> this.dispose());
		this.add(btnOk);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}
}
