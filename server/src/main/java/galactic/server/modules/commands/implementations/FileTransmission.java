package galactic.server.modules.commands.implementations;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import galactic.server.modules.commands.Commands;


public class FileTransmission extends Commands {

    private String room, file;

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




    private String FileUpload() {
        if (this.room == null || this.file == null) {
            return "Invalid usage: missing group/username or file path or both\n" +
                    "    Usage: <command> <group/username> <file path>";
        }

        return "";
    }


    private String ListFiles() {
        if (this.room == null || this.file == null) {
            return "Invalid usage: missing group/username\n" +
                    "    Usage: <command> <group/username>";
        }

        return "";
    }


    private String FileDownload() {
        if (this.room == null || this.file == null) {
            return "Invalid usage: missing group/username or file path or both\n" +
                    "    Usage: <command> <group/username> <file name>";
        }

        return "";
    }
}
