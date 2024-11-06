# Ecosystem Simulation

This project simulates an ecosystem with various animal species and interactions, using Java and Swing for the graphical user interface (GUI) or allowing for batch processing. Users can visualize the dynamics of the ecosystem, including animal behaviors, hunger states, and reproduction.
## Features

### General Features

- **Animal Simulation**: Simulate different species with different behaviors.
- **Dynamic Ecosystem**: Animals interact with each other and their environment, with different states depending on the animal itself.
- **Observer Pattern**: The system utilizes the Observer pattern to notify components of state changes, ensuring the UI updates in response to the simulation state.

### GUI Mode Features

- **Graphical Interface**: A user-friendly GUI allows for real-time updates and visual feedback of the ecosystem's state.
- **Data Display**: View the status of animals, including total counts, current time, and map dimensions, through a status bar.
- **Regions Table**: Displays information about each region, including the number of animals by diet type, and allows for easy tracking of ecosystem dynamics.
- **Interactive Controls**: Users can add animals and manipulate parameters through the interface, allowing for direct interaction with the simulation.
- **Real-time Feedback**: The GUI updates in real-time, reflecting changes in animal states and interactions.

### Batch Mode Features

- **Command-Line Interface**: Run the simulation without a graphical interface for automated testing or scripting.
- **Log Output**: Generate logs of the simulation's progress, including animal interactions, energy levels, and state changes, suitable for analysis.
- **Parameter Configuration**: Specify parameters through configuration files or command-line arguments for flexible simulations.
- **Simulation Speed Control**: Adjust the speed of the simulation through command-line options, allowing for faster or slower processing based on needs.
- **Headless Execution**: Ideal for running simulations on servers or in environments where a GUI is not available.

## Mode Options

When running the simulation, various command-line options can be specified to customize the simulation's behavior. Below are the available options:

1. **`-dt,--delta-time <arg>`**: A double representing actual time, in seconds,
                                  per simulation step. Default value: 0.03.

2. **`-h,--help`**: Print the help message.

3. **`-t,--time <arg>`**: An real number representing the total simulation
                          time in seconds. Default value: 10.0. (only for
                          BATCH mode).
4. **`-i,--input <arg>`**: A configuration file (optional in GUI mode) (.json).

5. **`-m,--mode <arg>`**: Execution Mode. Possible values: 'batch' (Batch
                          mode), 'gui' (Graphical User Interface mode).
                          Default value: 'gui'.

6. **`-o,--output <arg>`**: A file where output is written (only for BATCH
                          mode).

7. **`-sv,--simple-viewer`**: Show the viewer window in BATCH mode.
