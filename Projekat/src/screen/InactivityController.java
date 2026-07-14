package screen;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;

//Coordinates everything related to user-inactivity handling
public class InactivityController implements InactivityMonitor.InactivityListener {

	private final Frame owner;
	private final Label statusLabel;
	private final Runnable shutdownAction;

	private final InactivityMonitor monitor;
	private InactivityWarningDialog warningDialog;
	private AWTEventListener activityListener;

	public InactivityController(Frame owner, Label statusLabel, Runnable shutdownAction) {
		this.owner = owner;
		this.statusLabel = statusLabel;
		this.shutdownAction = shutdownAction;
		this.monitor = new InactivityMonitor(this);
	}

	//Starts the countdown and begins listening application-wide for user activity.
	public void start() {
		monitor.start();

		activityListener = event -> {
			if (warningDialog == null || !warningDialog.isVisible()) {
				resetTimer();
			}
		};

		Toolkit.getDefaultToolkit().addAWTEventListener(activityListener, AWTEvent.MOUSE_EVENT_MASK
				| AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK | AWTEvent.ACTION_EVENT_MASK);
	}

	// Stops the countdown, dismisses any open warning dialog, and stops listening.
	public void stop() {
		monitor.stop();

		if (warningDialog != null && warningDialog.isVisible()) {
			warningDialog.dispose();
		}

		if (activityListener != null) {
			Toolkit.getDefaultToolkit().removeAWTEventListener(activityListener);
		}
	}

	// Pauses or resumes the countdown, e.g. while a simulation is running.
	public void setPaused(boolean paused) {
		monitor.setPaused(paused);
	}

	private void resetTimer() {
		monitor.reset();

		if (warningDialog != null && warningDialog.isVisible()) {
			warningDialog.dispose();
		}

		updateLabel(monitor.getSecondsRemaining());
	}

	private void updateLabel(int secondsRemaining) {
		if (statusLabel != null) {
			statusLabel.setText("Inactivity: " + secondsRemaining + "s");
		}
	}


	@Override
	public void onTick(int secondsRemaining) {
		updateLabel(secondsRemaining);
	}

	@Override
	public void onWarningThreshold(int secondsRemaining) {
		if (warningDialog == null || !warningDialog.isVisible()) {
			warningDialog = new InactivityWarningDialog(owner, secondsRemaining, this::resetTimer);
			warningDialog.setVisible(true);
		} else {
			warningDialog.updateCountdown(secondsRemaining);
		}
	}

	@Override
	public void onTimeout() {
		shutdownAction.run();
	}
}
