package screen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import data.*;
import exceptions.*;

// Custom Canvas component responsible for rendering the simulation map.
// It draws airports and flights, handles mouse interactions (selection), 
// and implements double buffering to prevent screen flickering.
public class MapCanvas extends Canvas {
	private ParsedData data;

	private final int minX = -180, maxX = 180;
	private final int minY = -90, maxY = 90;

	private final int airportSize = 10;

	private Airport selectedAirport = null;

	private MainWindow mainWindow;

	private int currSimulationTime = 0;

	private Image offscreenImage;
	private Graphics offscreenGraphics;

	public MapCanvas(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		this.setBackground(Color.WHITE); // Set map background color

		// Add mouse listener to detect clicks on the canvas
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleMouseClick(e.getX(), e.getY());
			}
		});

		// Background daemon thread handling the blinking effect of the selected airport
		Thread blinkThread = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(200);
					if (selectedAirport != null) {
						// Only blink if the airport is currently visible on the map
						if (selectedAirport.isVisible()) {
							selectedAirport.setBlinkState(!selectedAirport.isBlinkState());
							repaint();
						} else {
							selectedAirport.setSelected(false);
							selectedAirport = null;
							mainWindow.setInactivityPaused(false);
						}
					}
				} catch (InterruptedException ex) {
					break;
				}
			}
		});
		blinkThread.setDaemon(true);
		blinkThread.start();
	}

	// Handles mouse click events, identifies if an airport was clicked,
	// and triggers the appropriate interaction logic.
	private void handleMouseClick(int clickX, int clickY) {
		try {
			int width = this.getWidth();
			int height = this.getHeight();
			Airport clickedNow = null;
			boolean clickedOnHidden = false;

			// Iterate through all airports to check if the click falls within their visual
			// bounds
			for (Airport a : data.getAirportArr()) {
				Point p = mapCoordsToScreen(a.getX(), a.getY(), width, height);

				if (clickX >= p.x - airportSize / 2 && clickX <= p.x + airportSize / 2
						&& clickY >= p.y - airportSize / 2 && clickY <= p.y + airportSize / 2) {

					if (!a.isVisible()) {
						clickedOnHidden = true;
						break;
					} else {
						clickedNow = a;
						break;
					}
				}
			}

			if (clickedOnHidden) {
				throw new HiddenAirportInteractionException();
			}

			checkClickedNow(clickedNow);
			repaint();

		} catch (HiddenAirportInteractionException ex) {
			if (mainWindow != null) {
				mainWindow.showErrorMessage(ex.getMessage());
			}
		}
	}

	// Toggles the selection state based on the clicked target.
	private void checkClickedNow(Airport clickedNow) {
		if (clickedNow != null) {
			if (selectedAirport == clickedNow) {
				selectedAirport.setSelected(false);
				selectedAirport = null;
				mainWindow.setInactivityPaused(false);
			} else {
				if (selectedAirport != null) {
					selectedAirport.setSelected(false);
				}
				selectedAirport = clickedNow;
				selectedAirport.setSelected(true);
				mainWindow.setInactivityPaused(true);
			}
		} else {
			if (selectedAirport != null) {
				selectedAirport.setSelected(false);
				selectedAirport = null;
				if (mainWindow != null)
					mainWindow.setInactivityPaused(false);
				repaint();
			}
		}
	}

	// Updates the internal data model and triggers a visual refresh.
	public void updateData(ParsedData d) {
		this.data = d;
		this.repaint();
	}

// Main rendering method. Draws all visible airports and active flights.
	@Override
	public void paint(Graphics g) {
		if (data == null || data.getAirportArr() == null)
			return;

		List<Airport> airports = data.getAirportArr();
		int width = this.getWidth();
		int height = this.getHeight();

		// 1. Draw all visible airports
		for (Airport a : airports) {
			if (a.isVisible()) {
				drawAirport(a, g, width, height);
			}
		}

		// 2. Draw all active flying planes
		if (data.getFlightArr() != null) {
			for (Flight f : data.getFlightArr()) {
				if (f.isFlying()) {
					Airport from = f.getFrom();
					Airport to = f.getTo();

					// Skip drawing the flight if either its origin or destination airport is hidden
					if (!from.isVisible() || !to.isVisible()) {
						continue;
					}

					// Calculate flight's current position on the screen
					Point screenP = mapCoordsToScreen(f.getCurrentX(), f.getCurrentY(), width, height);

					g.setColor(Color.BLUE);
					g.fillOval(screenP.x - 4, screenP.y - 4, 8, 8);
				}
			}
		}
	}

	// Helper method to draw a single airport and its textual code.
	private void drawAirport(Airport a, Graphics g, int width, int height) {
		Point screenPoint = mapCoordsToScreen(a.getX(), a.getY(), width, height);

		if (a.isSelected()) {
			g.setColor(a.isBlinkState() ? Color.RED : Color.GRAY);
		} else {
			g.setColor(Color.GRAY);
		}

		g.fillRect(screenPoint.x - airportSize / 2, screenPoint.y - airportSize / 2, airportSize, airportSize);

		g.setColor(Color.BLACK);
		g.setFont(new Font("Monospaced", Font.BOLD, 12));
		g.drawString(a.getCode(), screenPoint.x + 8, screenPoint.y + 4);
	}

	// Maps geographical coordinates (X, Y) to screen pixel coordinates.
	private Point mapCoordsToScreen(int x, int y, int viewWidth, int viewHeight) {
		int padding = 10;

		int usableWidth = viewWidth - (2 * padding);
		int usableHeight = viewHeight - (2 * padding);

		int rangeX = maxX - minX;
		int rangeY = maxY - minY;

		// Calculate proportional position and add padding
		int screenX = padding + (int) (((double) (x - minX) / rangeX) * usableWidth);
		// Y-axis is inverted on the screen (0 is top), so we subtract from maxY
		int screenY = padding + (int) (((double) (maxY - y) / rangeY) * usableHeight);

		return new Point(screenX, screenY);
	}

	// Overrides the standard update method to implement Double Buffering.
	// This prevents screen flickering by drawing the entire frame in memory first,
	// and then blitting it to the screen all at once.
	@Override
	public void update(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();

		if (offscreenImage == null || offscreenImage.getWidth(this) != width
				|| offscreenImage.getHeight(this) != height) {
			offscreenImage = this.createImage(width, height);
			offscreenGraphics = offscreenImage.getGraphics();
		}

		offscreenGraphics.setColor(this.getBackground());
		offscreenGraphics.fillRect(0, 0, width, height);

		paint(offscreenGraphics);

		g.drawImage(offscreenImage, 0, 0, this);
	}

	public void setSimulationTime(int curr) {
		currSimulationTime = curr;
	}
}