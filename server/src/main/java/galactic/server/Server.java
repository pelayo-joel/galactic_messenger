package galactic.server;

import galactic.server.modules.*;
// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Server {
    private static int port;
    public static void main(String[] args) {
        try {
            port = Integer.parseInt(args[0]);
            ServerConnection serverTest = new ServerConnection(port);
        }
        catch (NumberFormatException e) {
            System.out.println("Erreur : Veuillez fournir un port valide");
            System.out.println("Utilisation : java -jar galactic_messenger_server.jar [port]");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Utilisation : java -jar galactic_messenger_server.jar [port]");
        }
    }
}