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

public class ServerThread extends Thread {

    private final String endMessage = "exit";

    private Socket clientSocket;
    private String clientAddress;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private OutputStream outputStream;

    public ServerThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        clientAddress = clientSocket.getInetAddress().getHostAddress();
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        outputStream = clientSocket.getOutputStream();
        start();
    }

    @Override
    public void run() {
        System.out.println(clientAddress + ": " + "connection established");
        try {
            while (true) {
                //Read file name
                String text = bufferedReader.readLine();
                System.out.println(clientAddress + ": " + text);
                //Read file
                File file = new File(text);
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                //Read bytes
                byte[] bytes = new byte[(int) file.length()];
                bufferedInputStream.read(bytes, 0, bytes.length);
                //Send file
                System.out.println(clientAddress + ": sending " + text);
                outputStream.write(bytes,0,bytes.length);
                outputStream.flush();

//                bufferedWriter.write(text + "\n");
//                bufferedWriter.flush();

                if (text.equals(endMessage)) {
                    System.out.println(clientAddress + ": " + "connection aborted");
                    stopClient();
                    break;
                }
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void stopClient() {
        try {
            clientSocket.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}

