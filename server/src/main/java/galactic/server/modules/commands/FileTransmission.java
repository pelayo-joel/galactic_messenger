package galactic.server.modules.commands;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import galactic.server.modules.commands.interfaces.Command;
import galactic.server.modules.commands.interfaces.Communication;


public class FileTransmission implements Command, Communication {
    private final String client;
    private String command, room, file, selfMessage;
    private Set<String> receiver;


    public FileTransmission(List<String> clientInput, String clientName) {
        this.client = clientName;
        this.command = clientInput.get(0);
        this.room = clientInput.size() < 2 ? null : clientInput.get(1);
        this.file = clientInput.size() < 3 ? null : clientInput.get(2);
    }




    @Override
    public String CommandHandler() {
        this.receiver = new HashSet<>();
        switch (this.command) {
            case "/upload" -> { return FileUpload(); }
            case "/list_files" -> { return ListFiles(); }
            case "/download" -> { return FileDownload(); }
            default -> { return null; }
        }
    }


    @Override
    public String ServerResponse() { return this.selfMessage; }

    @Override
    public Set<String> GetReceivingParty() { return this.receiver; }



    private String FileUpload() {
        return "";
    }

    private String ListFiles() {
        return "";
    }

    private String FileDownload() {
        return "";
    }
}
