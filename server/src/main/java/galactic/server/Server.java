package galactic.server;

import galactic.server.modules.*;
// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Server {

    private static int port, databasePort;



    public static void main(String[] args) {
        try {
            port = Integer.parseInt(args[0]);
            databasePort = Integer.parseInt(args[1]);

            if ((args[0] != null && port >= 0 && port <= 65535) || (args[1] != null && databasePort >= 0 && databasePort <= 65535)) {
                ServerConnection.GetInstance(port, databasePort);
            }
            else { throw new NumberFormatException(); }
        }
        catch (NumberFormatException e) {
            System.out.println("Erreur : Veuillez fournir des ports valide");
            System.out.println("Utilisation : java -jar galactic_messenger_server.jar [port] [database_port]");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Utilisation : java -jar galactic_messenger_server.jar [port] [database_port]");
        }
    }
}