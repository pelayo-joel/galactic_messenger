package galactic.client;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private ObjectOutputStream writer;

    public Client(String serverAddress, int port) {
        try {
            System.out.println("Creating socket...");
            socket = new Socket(serverAddress, port);
            System.out.println("Socket created.");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Reader initialized.");
            writer = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Streams initialized.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            List<String> messageSplit = List.of(message.split(" "));
            writer.writeObject(messageSplit);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Client start...");
        Client client = new Client("10.10.1.119", 4269);
        System.out.println("Client connected to server");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter a message: ");
            String message = scanner.nextLine();
            if (message.equals("/disconnect")) {
                client.sendMessage(message);
                client.receiveMessage();
                System.out.println("Successfully disconnected");
                break;
            }
            else {
                client.sendMessage(message);
                String response = client.receiveMessage();
                System.out.println("Server response: " + response);
            }
        }
    }
}
