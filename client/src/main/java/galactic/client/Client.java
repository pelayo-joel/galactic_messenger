package galactic.client;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    public Client(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            reader = new ObjectInputStream(socket.getInputStream());
            writer = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client("10.10.6.78", 4269);
        Scanner scanner = new Scanner(System.in);
        boolean send = false;
        while (true) {
            if (!send) {
                String message = scanner.nextLine();
                client.send(message);
                send = true;
            }
            client.receive();
        }

    }

    public void send(String message) {
        String[] message_split = message.split(" ", -1);


        try{
            writer.writeObject(message_split);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void receive() {

        try {
            String message = (String) reader.readObject();
            System.out.println(message);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
