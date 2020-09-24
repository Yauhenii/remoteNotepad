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

public class ServerThread extends Thread {

    private final String endMessage = "exit";
    private final String requestMessage = "request";
    private final String echoMessage = "echo";

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
                String[] commandSplit= command.split(" ");
                if (commandSplit[0].equals(endMessage)) {
                    log.info(clientAddress + ": CONNECTION ABORTED");
                    stopClient();
                    break;
                } else if (commandSplit[0].equals(requestMessage)){
                    String fileName=commandSplit[1];
                    log.info(clientAddress + ": GOT REQUEST FOR FILE " + fileName);
                    byte[] bytes = getBytes(fileName);
                    log.info(clientAddress + ": SENDING FILE... " + fileName);
                    outputStream.write(bytes,0,bytes.length);
                    outputStream.flush();
                    log.info(clientAddress + ": FILE SENT " + fileName);
                } else if (commandSplit[0].equals(echoMessage)){
                    String message=commandSplit[1];
                    log.info(clientAddress + ": GOT ECHO MESSAGE ");
                    bufferedWriter.write(message + "\n");
                    bufferedWriter.flush();
                    log.info(clientAddress + ": SENT ECHO MESSAGE BACK");
                }
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private byte[] getBytes(String fileName) throws IOException{
        File file = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(file);
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

