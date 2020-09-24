package com.yauhenii;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// TODO: 9/24/20 set java.util.logging.SimpleFormatter.format="%4$s: %5$s [%1$tc]%n"

public class ServerThread extends Thread {

    private final static String endMessage = "exit";
    private final static String requestMessage = "request";
    private final static String echoMessage = "echo";
    private final static String acceptMessage = "accepted";
    private final static String deniedMessage = "denied";
    private final static String storageFolderDestination = "/Users/zhenyamordan/Desktop/Учеба/4 курс 1 сем/КБРС/Task2/Server/storage/";

    private Socket clientSocket;
    private String clientAddress;
    private BufferedReader bufferedReader; //Read text
    private OutputStream outputStream; //Write bytes
    private BufferedWriter bufferedWriter; //Write text

    private static Logger log = Logger.getLogger(Server.class.getName());

    public ServerThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        clientAddress = clientSocket.getInetAddress().getHostAddress();
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outputStream = clientSocket.getOutputStream();
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        start();
    }

    // TODO: 9/24/20 Rewrite if/else if to map

    @Override
    public void run() {
        log.info(clientAddress + ": CONNECTION ESTABLISHED");
        try {
            while (true) {
                String command = bufferedReader.readLine();
                String[] commandSplit = command.split(" ");
                if (commandSplit[0].equals(endMessage)) {
                    log.info(clientAddress + ": CONNECTION ABORTED");
                    stopClient();
                    break;
                } else if (commandSplit[0].equals(requestMessage)) {
                    String fileName = commandSplit[1];
                    log.info(clientAddress + ": GOT REQUEST FOR FILE " + fileName);
                    try {
                        byte[] bytes = getBytes(fileName);

                        log.info(clientAddress + ": SENDING ACCEPT MESSAGE... ");
                        sendMessage(acceptMessage);
                        log.info(clientAddress + ": SENDING FILE... " + fileName);
                        sendMessage(bytes);
                        log.info(clientAddress + ": FILE SENT " + fileName);
                    } catch (IOException exception) {
                        log.info(clientAddress + ": CANNOT FIND FILE " + fileName);
                        sendMessage(deniedMessage);
                    }
                } else if (commandSplit[0].equals(echoMessage)) {
                    String message = commandSplit[1];
                    log.info(clientAddress + ": GOT ECHO MESSAGE ");
                    sendMessage(message);
                    log.info(clientAddress + ": SENT ECHO MESSAGE BACK");
                }
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void sendMessage(String message) throws IOException {
        bufferedWriter.write(message + "\n");
        bufferedWriter.flush();
    }

    private void sendMessage(byte[] bytes) throws IOException {
        outputStream.write(bytes, 0, bytes.length);
        outputStream.flush();
    }

    private byte[] getBytes(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(storageFolderDestination + file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        //Read bytes
        byte[] bytes = new byte[(int) file.length()];
        bufferedInputStream.read(bytes, 0, bytes.length);
        return bytes;
    }

    public void stopClient() {
        try {
            clientSocket.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}

