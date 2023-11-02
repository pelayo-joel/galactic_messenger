package galactic.server.modules.commands.implementations;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import galactic.server.modules.commands.Commands;
import galactic.server.modules.database.crud.Create;
import galactic.server.modules.database.crud.Read;


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
        byte[] fileData = Read.FileData(this.fileName, this.room);
        this.file = new DatagramPacket(fileData, fileData.length);

        if (Read.GroupUsers(this.room).contains(this.client)) {
           return null;
        }

        return this.file;
    }


    public void StoreFile(byte[] fileBytes) {
        Create.InsertFile(fileBytes, this.fileName, this.room);
    }




    private String FileUpload() {
        if (this.room == null || this.file == null) {
            return "\nInvalid usage: missing group/username or file path or both\n" +
                    "    Usage: <command> <group/username> <file path>";
        }

        this.selfMessage = "'" + this.fileName + "' has been uploaded to the server";
        return "'" + this.fileName + "' is available for you on the server";
    }


    private String ListFiles() {
        if (this.room == null || this.file == null) {
            return "\nInvalid usage: missing group/username\n" +
                    "    Usage: <command> <group/username>";
        }

        StringBuilder response = new StringBuilder("Files in [" + this.room + "]: ");
        List<String> fileList = Read.RoomFiles(this.room);
        for (String file : fileList) {
            response.append(file).append(", ");
        }

        response.substring(0, response.length() - 2);
        return response.toString();
    }


    private String FileDownload() {
        if (this.room == null || this.file == null) {
            return "\nInvalid usage: missing group/username or file path or both\n" +
                    "    Usage: <command> <group/username> <file name>";
        }

        return "'" + this.fileName + "' has been successfully downloaded" ;
    }
}
