package galactic.server;


import galactic.server.modules.*;


/**
 * Application's entry point
 */
public class Server {

    private static int port, databasePort;


    /**
     * Handles application's arguments
     *
     * @param args Array of arguments which takes the server (machine) and database port
     */
    public static void main(String[] args) {
        try {
            port = Integer.parseInt(args[0]);
            databasePort = Integer.parseInt(args[1]);

            //Throws an error if it's an invalid port
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