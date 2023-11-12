package galactic.server.modules.commands;


import java.util.Set;


/**
 * Holds common properties/methods for all implementations
 */
public abstract class Commands {

    protected String client = "", command = "", selfMessage = "";

    protected Set<String> receiver;


    /**
     * Parses the user inputs and creates the server response via child classes methods
     *
     * @return the String for the receivers
     */
    public abstract String CommandHandler();

    /**
     * Returns the server response to the client,
     * needs to be implemented in child classes to get the right value for 'selfMessage'
     *
     * @return the server response
     */
    public abstract String ServerResponse();

    /**
     * Returns the Set of usernames to whom the server needs to send its message,
     * needs to be implemented in child classes to get the right value for 'selfMessage'
     *
     * @return a Set of usernames
     */
    public abstract Set<String> GetReceivingParty();

    /**
     * [NOT USED] Should handle invalid arguments or usage for the command
     *
     * @return an error String for the client
     */
    protected abstract String InvalidArgumentsErrors();


    //Returns the client username
    public String getClientName() { return client; }
}
