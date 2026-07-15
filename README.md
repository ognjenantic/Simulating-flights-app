# Flight Traffic Simulation ✈️

A flight traffic simulation application developed in Java using the AWT graphics library. This project is the official assignment for the **Object-Oriented Programming 2 (OOP2)** course at the School of Electrical Engineering, University of Belgrade. 

The system provides functionalities for loading, visualizing, and simulating flights on a global map, strictly adhering to object-oriented design principles, multithreading, and robust exception handling.

## 📌 Core Features

The project is divided into three main phases covering data processing, visualization, and real-time simulation.

### 1. Data Input and Processing
* **I/O Operations:** Ability to load and save the system's state using `CSV` and `JSON` file formats.
* **Parsing and Validation:** Implemented custom parsers (`CsvParser`, `JsonParser`) that extensively validate user input.
* **Error Handling:** All errors (e.g., invalid coordinates, duplicate codes, wrong file formats) are caught and presented to the user via modal `ErrorDialog` windows.
* **Inactivity Timer:** The system monitors user inactivity. After 60 seconds of inactivity (no mouse movement or clicks), the application automatically shuts down, displaying a modal warning during the final 5 seconds.

### 2. Map Visualization and Filtering
* **Cartesian System:** Airports are mapped onto the screen using geographical coordinates (X-axis: -180 to 180, Y-axis: -90 to 90).
* **Interaction:** Airports are represented as gray squares with a three-letter code. Clicking on an airport selects it and makes it blink (red/green) using a background daemon thread.
* **Filtering System:** A dynamic list (Checkboxes) on the right side allows users to toggle the visibility of specific airports on the map in real-time.

### 3. Flight Simulation
* **Time Logic:** The simulation runs in a dedicated background thread. One second of real time corresponds to 10 minutes of simulated time.
* **Takeoff Rules:** A maximum of one plane can take off from a single airport within a 10-minute (simulated) interval. Other flights are placed in a waiting queue.
* **Graphics:** Planes are rendered as blue circles that move linearly between their origin and destination.
* **Controls:** Users can start, pause, and completely reset the simulation using the provided UI controls.

## 🛠️ Architecture and Design Patterns

The project is built entirely from scratch without using external GUI frameworks, relying exclusively on the core **Java AWT** library.

* **Model-View-Controller (MVC):** Clear separation of concerns between data models (`ParsedData`), the graphical user interface (`MapCanvas`, `MainWindow`), and the control logic.
* **Double Buffering:** Implemented within the `MapCanvas` component to prevent screen flickering during the 200ms flight animation updates.
* **Singleton Pattern:** Used for the instantiation of the `CsvParser` and `JsonParser` classes.
* **Thread-Safety:** All communication between background threads (like the simulation timer and inactivity monitor) and the graphical components is safely executed using `EventQueue.invokeLater()`.
