package galactic.client;

import java.io.BufferedReader;

public class MessageHandler {
    private BufferedReader reader;

    // Constructor to initialize the reader for receiving messages
    public MessageHandler(BufferedReader reader) {
        this.reader = reader;
    }

    // Method to process and display incoming chat messages
    public void processChatMessage(String message) {
        // Implement logic to display chat messages to the user
    }

    // Method to handle notifications of new users joining the system
    public void handleNewUserNotification(String username) {
        // Implement logic to display notifications about new users
    }

    // Add methods for handling other types of messages (group messages, file notifications, etc.)
}
