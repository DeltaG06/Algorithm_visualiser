# Algorithm Visualiser

A high-fidelity, interactive, Java Swing application built with a "Sci-Fi/Hacker" aesthetic for visualizing sorting and searching algorithms.

## Features

- **Custom Graphics**: High-fidelity visualizer with curved arc connectors, operation-specific "COMPARING" and "SWAPPING" pill labels, and index tracking.
- **Neon Palette**: Dark theme (`#08080A`) with vibrant neon accents (Cyan, Pink, Green, Gold).
- **Divide & Conquer Mode**: Specialized 3-panel split view for algorithms like Merge Sort, allowing users to watch sub-arrays split and merge in real-time.
- **Step-by-Step History Replay**: Fully supports pausing the simulation and stepping backwards/forwards through the algorithm execution history (up to 5,000 recorded states).
- **Multi-Language Code Snippets**: Includes an integrated code panel with a language-switching dropdown for Java, Python, C++, and C with a one-click copy button.
- **Speed Control**: Dynamically adjustable simulation speed down to 2ms or up to 2000ms.
- **Algorithm Coverage**: Bubble, Selection, Insertion, Merge, Quick Sort, and Binary Search.
- **Performance Persistence**: Logs algorithm execution metrics (time, swaps, comparisons) to an SQLite database (`results.db`).

## Prerequisites

- **Java Development Kit (JDK) 8 or higher**
- A Windows environment is recommended to use the included `run.bat` script.

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/DeltaG06/Algorithm_visualiser.git
   cd Algorithm_visualiser
   ```

2. **Run the Application:**
   Double-click the `run.bat` file in your file explorer, or execute it from the command line:
   ```cmd
   .\run.bat
   ```

   *The `run.bat` script will automatically compile all `.java` files from the `src/` directory into the `out/` folder, include the necessary SQLite JDBC library, and launch the application.*

## Project Structure
- `src/Main.java`: The application entry point.
- `src/algorithms/`: Contains the logic and state management for each algorithm.
- `src/ui/`: Contains the custom Swing GUI components (Visualizer, Control Panel, Code Panel, etc.).
- `src/db/`: SQLite database initialization and data access objects.
- `lib/`: Includes the required `sqlite-jdbc.jar` dependency.
