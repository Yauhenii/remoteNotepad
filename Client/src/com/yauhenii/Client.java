package com.yauhenii;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Client {

    private final static int FILE_SIZE = 8192;//6022386;

    private final String endMessage = "exit";
    private final String requestMessage = "request";
    private final String echoMessage = "echo";
    private final static String storageFolderDestination = "/Users/zhenyamordan/Desktop/Учеба/4 курс 1 сем/КБРС/Task2/Client/storage/";


    private Socket clientSocket;
    private BufferedReader consoleReader;
    private BufferedReader bufferedReader;
    private InputStream inputStream; //Read bytes
    private OutputStream outputStream; //Write bytes

    int port;
    int remotePort;
    InetAddress address;
    InetAddress remoteAddress;

    private static Logger log = Logger.getLogger(Client.class.getName());

    public Client(InetAddress remoteAddress, int remotePort, InetAddress address, int port)
        throws IOException {
        this.port = port;
        this.remotePort = remotePort;
        this.address = address;
        this.remoteAddress = remoteAddress;
    }

    public void start() {
        try {
            clientSocket = new Socket(remoteAddress, remotePort, address, port);
            log.info("CLIENT IS RUN");

            consoleReader = new BufferedReader(new InputStreamReader(System.in));
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();

            while (true) {
                String command = consoleReader.readLine();
                String[] commandSplit = command.split(" ");

                writeBytes(command.getBytes());

                byte[] bytes;

                if (commandSplit[0].equals(endMessage)) {
                    System.out.println("CONNECTION ABORTED");
                    break;
//                } else if (commandSplit[0].equals(requestMessage)){
//                    String fileName=commandSplit[1];
//                    System.out.println("REQUEST FILE BY NAME: "+fileName);
//                    System.out.println("SAVE AS:");
//                    String newFileName = consoleReader.readLine();
//                    bufferedWriter.write(command + "\n");
//                    bufferedWriter.flush();
//                    System.out.println("RECEIVING AND WRITING FILE...");
////                    writeConsole(inputStream);
//                    writeFile(newFileName,inputStream);
//                    System.out.println("FILE IS SUCCESSFULLY RECEIVED AND WROTE");
                } else if (commandSplit[0].equals(echoMessage)) {
                    bytes = readBytes();
                    System.out.println(new String(bytes));
                }

            }

            stop();
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

    private void writeBytes(byte[] bytes) throws IOException{
        outputStream.write(bytes);
        outputStream.flush();
    }

//    https://stackoverflow.com/questions/9520911/java-sending-and-receiving-file-byte-over-sockets

//    private void writeFile(String fileName, InputStream inputStream) throws IOException {
//        FileOutputStream fileOutputStream = new FileOutputStream(
//            storageFolderDestination + fileName);
//        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
//        int count;
//        byte[] bytes = new byte[FILE_SIZE];
//        if () {
//            count = inputStream.read(bytes);
//        }
//        bufferedOutputStream.write(bytes, 0, count);
//        bufferedOutputStream.flush();
//    }

    public void stop() {
        try {
            clientSocket.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
