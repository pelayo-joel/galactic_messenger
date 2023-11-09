package galactic.server.modules.commands.implementations;


import java.net.DatagramPacket;
import java.util.*;

import galactic.server.modules.commands.Commands;
import galactic.server.modules.database.crud.Create;
import galactic.server.modules.database.crud.Read;


/**
 * Inherits 'Commands', handles file transfer commands and creates a corresponding response from the server
 */
public class FileTransmission extends Commands {

    private DatagramPacket file;

    private final String CANAL_ARGS_ERROR = "Username or group name invalid or does not exist",
            FILE_ARGS_ERROR = "\nInvalid usage: missing group/username or file path or both\n" +
                    "    Usage: <command> <group/username> <file path>";

    private String canal, fileName;



    /**
     * Parse the received List,
     * number of String might change depending on the used command,
     * gets the properties to null if the command needs less arguments,
     * it also removes the path from the filename
     *
     * @param clientInput the user inputs
     * @param clientName the client username
     */
    public FileTransmission(List<String> clientInput, String clientName) {
        this.client = clientName;
        this.command = clientInput.get(0);
        this.canal = clientInput.size() >= 2 ? clientInput.get(1) : null;
        this.fileName = clientInput.size() == 3 ? clientInput.get(2) : null;

        //Removes the path from filename
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


    @Override
    public String ServerResponse() { return this.selfMessage; }


    @Override
    public Set<String> GetReceivingParty() { return this.receiver; }



    public DatagramPacket GetFile() {
        byte[] fileData = Read.FileData(this.fileName);
        this.file = new DatagramPacket(fileData, fileData.length);

        if (Read.GroupUsers(this.canal).contains(this.client) || !(Read.ChatId(this.client, this.canal) == 0)) {
           return this.file;
        }

        return null;
    }


    public void StoreFile(byte[] fileBytes) {
        if(Read.AllGroupNames().contains(this.canal)) {
            Create.FileInGroup(fileBytes, this.fileName, this.canal);
        }
        else if (Read.AllUsernames().contains(this.canal)) {
            Create.FileInChat(fileBytes, this.fileName, this.client, this.canal);
        }
        else {
            this.selfMessage = "User or group might not exist";
            return;
        }
        this.selfMessage = "'" + this.fileName + "' has been uploaded to the server";
    }




    private String FileUpload() {
        if (!InvalidArgumentsErrors().isEmpty()) {
            return InvalidArgumentsErrors();
        }

        return "'" + this.fileName + "' is available for you on the server";
    }


    private String ListFiles() {
        if (this.canal == null || this.file == null) {
            return "\nInvalid usage: missing group/username\n" +
                    "    Usage: <command> <group/username>";
        }

        StringBuilder response;
        List<String> fileList;
        if(Read.AllGroupNames().contains(this.canal)) {
            response = new StringBuilder("Files in [" + this.canal + "]: ");
            fileList = Read.GroupFiles(this.canal);
        }
        else if (Read.AllUsernames().contains(this.canal)) {
            response = new StringBuilder("Files available between you & " + this.canal + ": ");
            fileList = Read.ChatFiles(this.canal);
        }
        else {
            return CANAL_ARGS_ERROR;
        }

        for (String file : fileList) {
            response.append(file).append(", ");
        }
        response.substring(0, response.length() - 2);
        return response.toString();
    }


    private String FileDownload() {
        if (!InvalidArgumentsErrors().isEmpty()) {
            return InvalidArgumentsErrors();
        }

        return "'" + this.fileName + "' has been successfully downloaded" ;
    }


    protected String InvalidArgumentsErrors() {
        if (this.canal == null || this.file == null) {
            return FILE_ARGS_ERROR;
        }

        if(Read.AllGroupNames().contains(this.canal)) {
            this.receiver.addAll(Read.GroupUsers(this.canal));
        }
        else if (Read.AllUsernames().contains(this.canal)) {
            this.receiver.add(this.canal);
        }
        else {
            return CANAL_ARGS_ERROR;
        }

        return "";
    }
}
