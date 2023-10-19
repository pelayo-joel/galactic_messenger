package galactic.client;
import java.io.*;
import java.net.Socket;
import java.util.List;
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
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream())) ;
            System.out.println("Reader initialized.");
            writer = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Streams initialized.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    thread to receive messages
//    thread to send messages
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Client start...");
        Client client = new Client("10.10.10.214", 4269);
        System.out.println("Client connected to server");
        boolean send = false;

        String message = scanner.nextLine();
        client.send(message);
    }

    public void send(String message) {
        List<String> message_split = List.of(message.split(" "));
//        System.out.println(Arrays.toString(message_split));

        try{
            writer.writeObject(message_split);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void receive() {

        try {
            String message = reader.readLine();
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
