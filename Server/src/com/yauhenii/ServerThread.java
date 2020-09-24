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
    private BufferedReader bufferedReader; //Read text
    private OutputStream outputStream; //Write bytes
//    private BufferedWriter bufferedWriter;


    public ServerThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        clientAddress = clientSocket.getInetAddress().getHostAddress();
        bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outputStream = clientSocket.getOutputStream();
//        bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        start();
    }

    @Override
    public void run() {
        System.out.println("LOG: "+ clientAddress + ": CONNECTION ESTABLISHED");
        try {
            while (true) {
                String fileName = bufferedReader.readLine();
                if (fileName.equals(endMessage)) {
                    System.out.println("LOG: "+clientAddress + ": CONNECTION ABORTED");
                    stopClient();
                    break;
                }
                System.out.println("LOG: "+clientAddress + ": GOT REQUEST FOR FILE " + fileName);
                byte[] bytes = getBytes(fileName);
                System.out.println("LOG: "+clientAddress + ": SENDING FILE... " + fileName);
                outputStream.write(bytes,0,bytes.length);
                outputStream.flush();
                System.out.println("LOG: "+clientAddress + ": FILE SENT " + fileName);
//                bufferedWriter.write("DONE" + "\n");
//                bufferedWriter.flush();


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

