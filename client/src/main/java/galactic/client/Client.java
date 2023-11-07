package galactic.client;

import galactic.client.modules.miscellaneous.Colors;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;

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
            if(message.equals("/disconnect") || message.equals("/quit")) {
                System.out.println("Disconnecting...");
                client.close();
                break;
            }
            if (message.equals("/help")) {
                System.out.println(Colors.RED + "For 1-to-1 messages:\n" +
                        Colors.BLUE + "\n/private_chat [target user] :" + Colors.DEFAULT + " Cette commande permet de demarrer une conversation privee avec un autre utilisateur en specifiant leur nom.\n" +
                        Colors.BLUE + "\n/accept [target user] :" + Colors.DEFAULT + " Utilise pour accepter une invitation à une discussion privee de la part d'un utilisateur specifie.\n" +
                        Colors.BLUE + "\n/decline [target user] :" + Colors.DEFAULT + " Cette commande permet de refuser une invitation à une discussion privee de la part d'un utilisateur specifie.\n" +
                        Colors.BLUE + "\n/msg [message] :" + Colors.DEFAULT + " Utilise pour envoyer un message dans une conversation privee en cours.\n" +
                        Colors.BLUE + "\n/exit_private_chat :" + Colors.DEFAULT + " Cette commande permet de quitter une discussion privee actuelle.\n");

                System.out.println(Colors.RED + "For group command:\n" +
                        Colors.BLUE + "\n/create_group [name] :" + Colors.DEFAULT + "  Vous permet de creer un groupe de discussion en specifiant un nom pour le groupe.\n" +
                        Colors.BLUE + "\n/join_group [name] :" + Colors.DEFAULT + " Utilise pour rejoindre un groupe de discussion existant en specifiant son nom.\n" +
                        Colors.BLUE + "\n/msg_group :" + Colors.DEFAULT + " Cette commande permet d'envoyer un message dans un groupe de discussion en cours.\n" +
                        Colors.BLUE + "\n/exit_group :" + Colors.DEFAULT + " Utilise pour quitter le groupe de discussion actuel.\n" +
                        Colors.BLUE + "\n/create_secure_group [name] [password] :" + Colors.DEFAULT + " Vous permet de creer un groupe de discussion securise en specifiant un nom et un mot de passe.\n" +
                        Colors.BLUE + "\n/join_secure_group [name] :" + Colors.DEFAULT + " Cette commande permet de rejoindre un groupe de discussion securise en specifiant son nom.\n");

                System.out.println(Colors.RED + "For files command:\n" +
                        Colors.BLUE + "\n/upload [user/group] [filename] :" + Colors.DEFAULT + " Utilise pour telecharger un fichier vers un utilisateur ou un groupe specifie.\n" +
                        Colors.BLUE + "\n/list_files :" + Colors.DEFAULT + " Cette commande affiche la liste des fichiers disponibles dans la messagerie.\n" +
                        Colors.BLUE + "\n/download [user/group] [filename] :" + Colors.DEFAULT + " Vous permet de telecharger un fichier depuis un utilisateur ou un groupe specifie.\n");

                System.out.println(Colors.RED + "To print online users:\n" +
                        Colors.BLUE + "\n/online_users :" + Colors.DEFAULT + " Affiche la liste des utilisateurs en ligne.\n");

                System.out.println(Colors.RED + "To quit and log out from the program:\n" +
                        Colors.BLUE + "\n/disconnect :" + Colors.DEFAULT + " Utilise pour quitter completement le programme et se deconnecter.\n" +
                        Colors.BLUE + "\n/quit :" + Colors.DEFAULT + " Utilise pour quitter completement le programme et se deconnecter." + Colors.DEFAULT);
            }


        }
    }
}

