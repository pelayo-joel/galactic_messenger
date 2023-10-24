package galactic.server.modules.commands.interfaces;

import java.util.Set;

public interface Communication {
    public String ServerResponse();
    public Set<String> GetReceivingParty();
}
