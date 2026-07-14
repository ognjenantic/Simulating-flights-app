package screen;

import java.awt.EventQueue;

//Tracks user inactivity and drives a one-second countdown that can trigger a
//warning and, if ignored, an application shutdown.
public class InactivityMonitor {

	// Receives notifications about countdown progress and expiry.
	public interface InactivityListener {

		void onTick(int secondsRemaining);

		void onWarningThreshold(int secondsRemaining);

		void onTimeout();
	}

	private static final int DEFAULT_TIMEOUT_SECONDS = 60;
	private static final int WARNING_THRESHOLD_SECONDS = 5;

	private final InactivityListener listener;
	private final int timeoutSeconds;

	private volatile int secondsRemaining;
	private volatile boolean paused;
	private volatile boolean running;

	private Thread timerThread;

	public InactivityMonitor(InactivityListener listener) {
		this(listener, DEFAULT_TIMEOUT_SECONDS);
	}

	public InactivityMonitor(InactivityListener listener, int timeoutSeconds) {
		this.listener = listener;
		this.timeoutSeconds = timeoutSeconds;
		this.secondsRemaining = timeoutSeconds;
	}

	// Starts the background countdown thread
	public void start() {
		running = true;
		timerThread = new Thread(this::runCountdownLoop, "InactivityMonitor");
		timerThread.setDaemon(true);
		timerThread.start();
	}

	// Stops the countdown thread
	public void stop() {
		running = false;
		if (timerThread != null) {
			timerThread.interrupt();
		}
	}

	// Resets the countdown back to the full timeout
	public void reset() {
		secondsRemaining = timeoutSeconds;
	}

	// Pauses or resumes the countdown, e.g. while a simulation is running.
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public int getSecondsRemaining() {
		return secondsRemaining;
	}

	private void runCountdownLoop() {
		while (running) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Expected on stop()
				continue;
			}

			if (!paused) {
				secondsRemaining--;
			}
			
			EventQueue.invokeLater(() -> listener.onTick(secondsRemaining));

			if (secondsRemaining <= 0) {
				listener.onTimeout();
			} else if (secondsRemaining <= WARNING_THRESHOLD_SECONDS) {
				EventQueue.invokeLater(() -> listener.onWarningThreshold(secondsRemaining));
			}
		}
	}
}
