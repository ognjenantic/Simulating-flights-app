package screen;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;

 //A dialog shown once the inactivity countdown enters its warning window.
public class InactivityWarningDialog extends Dialog {

	private final Label countdownLabel;

	public InactivityWarningDialog(Frame owner, int secondsRemaining, Runnable onContinue) {
		super(owner, "Inactivity Warning", false);
		this.setLayout(new FlowLayout());
		this.setSize(300, 150);
		this.setLocationRelativeTo(owner);

		Panel countdownPanel = new Panel(new BorderLayout());
		countdownLabel = new Label(formatCountdown(secondsRemaining));
		countdownPanel.add(countdownLabel);
		this.add(countdownPanel, BorderLayout.CENTER);

		Panel buttonPanel = new Panel(new BorderLayout());
		Button btnContinue = new Button("Continue");
		btnContinue.addActionListener(e -> {
			onContinue.run();
			this.dispose();
		});
		buttonPanel.add(btnContinue);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	// Updates the on-screen countdown text; call this once per tick.
	public void updateCountdown(int secondsRemaining) {
		countdownLabel.setText(formatCountdown(secondsRemaining));
	}

	private String formatCountdown(int secondsRemaining) {
		return "Closing in: " + secondsRemaining + "s";
	}
}
