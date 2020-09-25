package com.yauhenii;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Logger;

public class ServerThread extends Thread {

    private final static int FILE_SIZE = 8192;//6022386;
    private final static String endMessage = "exit";
    private final static String requestMessage = "request";
    private final static String echoMessage = "echo";
    private final static String storageFolderDestination = "/Users/zhenyamordan/Desktop/Учеба/4 курс 1 сем/КБРС/Task2/Server/storage/";

    private Socket clientSocket;
    private String clientAddress;
    private InputStream inputStream;
    private OutputStream outputStream;

    private static Logger log = Logger.getLogger(Server.class.getName());

    public ServerThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        clientAddress = clientSocket.getInetAddress().getHostAddress();
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
        start();
    }

    // TODO: 9/24/20 Rewrite if/else if to map

    @Override
    public void run() {
        log.info(clientAddress + ": CONNECTION ESTABLISHED");
        try {
            while (true) {
                byte[] bytes=null;
                bytes = readBytes();
                String command = new String(bytes);
                String[] commandSplit = command.split(" ");

                if (commandSplit[0].equals(endMessage)) {
                    log.info(clientAddress + ": CONNECTION ABORTED");
                    stopClient();
                    break;
                } else if (commandSplit[0].equals(requestMessage)) {
                    String fileName = commandSplit[1];
                    log.info(clientAddress + ": GOT REQUEST FOR FILE " + fileName);
                    bytes = readBytesFromFile(fileName);
                    log.info(clientAddress + ": SENDING FILE... " + fileName);
                    System.out.println(new String(bytes));
                    writeBytes(bytes);
                    log.info(clientAddress + ": FILE SENT " + fileName);
                } else if (commandSplit[0].equals(echoMessage)) {
                    String message = commandSplit[1];
                    log.info(clientAddress + ": GOT ECHO MESSAGE ");
                    writeBytes(message.getBytes());
                    log.info(clientAddress + ": SENT ECHO MESSAGE BACK");
                }
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private byte[] readBytes() throws IOException {
        int count;
        byte[] buffer = new byte[FILE_SIZE];
        while ((count = inputStream.read(buffer)) > 0) {
            return Arrays.copyOfRange(buffer, 0, count);
        }
        return null;
    }

    private void writeBytes(byte[] bytes) throws IOException {
        outputStream.write(bytes);
        outputStream.flush();
    }

    private byte[] readBytesFromFile(String filename) throws IOException {
        File file = new File(storageFolderDestination + filename);
        return Files.readAllBytes(file.toPath());
    }

    public void stopClient() {
        try {
            clientSocket.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}

