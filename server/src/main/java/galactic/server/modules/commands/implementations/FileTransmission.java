package galactic.server.modules.commands.implementations;


import java.net.DatagramPacket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import galactic.server.modules.commands.Commands;


public class FileTransmission extends Commands {

    private DatagramPacket file;

    private String room, fileName;

    private Set<String> receiver;



    public FileTransmission(List<String> clientInput, String clientName) {
        this.client = clientName;
        this.command = clientInput.get(0);
        this.room = clientInput.get(1).isEmpty() ? null : clientInput.get(1);
        this.fileName = clientInput.get(2).isEmpty() ? null : clientInput.get(2);

        if (this.fileName != null && this.fileName.contains("/")) {
            this.fileName = this.fileName.substring(this.fileName.lastIndexOf("/") + 1);
        }
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


    public DatagramPacket GetFile() {
        return this.file;
    }


    public void StoreFile(byte[] fileBytes) {

    }




    private String FileUpload() {
        if (this.room == null || this.file == null) {
            return "\nInvalid usage: missing group/username or file path or both\n" +
                    "    Usage: <command> <group/username> <file path>";
        }

        return "";
    }


    private String ListFiles() {
        if (this.room == null || this.file == null) {
            return "\nInvalid usage: missing group/username\n" +
                    "    Usage: <command> <group/username>";
        }

        return "";
    }


    private String FileDownload() {
        if (this.room == null || this.file == null) {
            return "\nInvalid usage: missing group/username or file path or both\n" +
                    "    Usage: <command> <group/username> <file name>";
        }

        return "";
    }
}
