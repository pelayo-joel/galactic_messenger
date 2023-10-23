package galactic.server.modules.commands;


import java.util.List;

import galactic.server.modules.commands.interfaces.Command;


public class FileTransmission implements Command {
    private String command, room, file;


    public FileTransmission(List<String> clientInput) {
        this.command = clientInput.get(0);
        this.room = clientInput.size() < 2 ? null : clientInput.get(1);
        this.file = clientInput.size() < 3 ? null : clientInput.get(2);
    }



    @Override
    public String CommandHandler() {
        switch (this.command) {
            case "/upload" -> { return FileUpload(); }
            case "/list_files" -> { return ListFiles(); }
            case "/download" -> { return FileDownload(); }
            default -> { return "Invalid file command"; }
        }
    }



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
