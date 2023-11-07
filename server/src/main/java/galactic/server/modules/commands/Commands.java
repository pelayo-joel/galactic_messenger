package galactic.server.modules.commands;


import java.util.Set;


public abstract class Commands {

    protected String client = "", command = "", selfMessage = "";
    protected Set<String> receiver;



    public abstract String CommandHandler();

    public abstract String ServerResponse();

    public abstract Set<String> GetReceivingParty();

    protected abstract String InvalidArgumentsErrors();


    public String getClientName() { return client; }
}
