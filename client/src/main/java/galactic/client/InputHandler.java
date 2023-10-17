package galactic.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class InputHandler {
    private BufferedReader userInputReader;
    private BufferedWriter writer;

    // Constructor to initialize the user input reader and message writer
    public InputHandler(BufferedReader userInputReader, BufferedWriter writer) {
        this.userInputReader = userInputReader;
        this.writer = writer;
    }

    // Method to read user input and process commands
    public void processUserInput() {
        // Implement logic to read user input and process commands
    }

    // Add methods to handle specific user commands (e.g., sending messages, joining groups, etc.)
}
