package galactic.server.modules.commands;


import java.util.Set;


public abstract class Commands {

    protected String client = "", command = "", selfMessage = "";
    protected Set<String> receiver;



    public abstract String CommandHandler();

    public abstract Set<String> GetReceivingParty();


    public String getClientName() { return client; }

    public String ServerResponse() { return selfMessage; }
}
