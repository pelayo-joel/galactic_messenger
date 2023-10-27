package galactic.client;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {
    private static Socket socket;

    private List<String> user_demands;
    private BufferedReader reader;
    private ObjectOutputStream writer;

    public Client(String serverAddress, String port) {
        user_demands = new ArrayList<>();
        try {
            System.out.println("Creating socket...");
            socket = new Socket(serverAddress, Integer.parseInt(port));
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
            ;
            List<String> messageSplit = List.of(message.split(" ", 3));
            if (messageSplit.get(0).equals("/accept") || messageSplit.get(0).equals("/decline")) {
                if(user_demands.contains(messageSplit.get(1))) {
                    user_demands.remove(messageSplit.get(1));
                }else {
                    System.out.println("You don't have any demands from this user.");
                    return;
                }
            }
            writer.writeObject(messageSplit);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        try {
            String message = reader.readLine();
            if (message.startsWith("/dprivate")) {
                String userRequest = message.substring(message.lastIndexOf(" ") + 1);
                user_demands.add(userRequest);
                return "User " + userRequest + " wants to talk with you.";
            }else {
                return message;
            }
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

    public void startMessageReceiver() {
        Thread messageReceiverThread = new Thread(() -> {
            while (true) {
                // sleep 1s

                String message = receiveMessage();
                if (message != null) {
                    System.out.println(message);
                }

            }
        });
        messageReceiverThread.setDaemon(true);
        messageReceiverThread.start();
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Client start...");
        Client client = new Client(args[0], args[1]);
        System.out.println("Client connected to server");

        client.startMessageReceiver();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter a message: ");
            String message = scanner.nextLine();
            client.sendMessage(message);
            if(message.equals("/disconnect")) {
                System.out.println("Disconnecting...");
                client.close();
                break;
            }
        }
    }
}

