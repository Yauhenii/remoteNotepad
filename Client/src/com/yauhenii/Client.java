package com.yauhenii;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Client {

    private final static int FILE_SIZE = 8192;//6022386;
    private final static String endMessage = "exit";
    private final static String requestMessage = "request";
    private final static String saveMessage = "save";
    private final static String echoMessage = "echo";
    private final static String acceptMessage = "accept";
    private final static String denyMessage = "deny";
    private final static String storageFolderDestination = "/Users/zhenyamordan/Desktop/Учеба/4 курс 1 сем/КБРС/Task2/Client/storage/";

    private Socket clientSocket;
    private BufferedReader consoleReader;
    private InputStream inputStream;
    private OutputStream outputStream;

    int port;
    int remotePort;
    InetAddress address;
    InetAddress remoteAddress;

    KeyPair keyPair;
    Cipher decryptCipher;

    private static Logger log = Logger.getLogger(Client.class.getName());

    public Client(InetAddress remoteAddress, int remotePort, InetAddress address, int port)
        throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        this.port = port;
        this.remotePort = remotePort;
        this.address = address;
        this.remoteAddress = remoteAddress;
    }

    public void start() throws Exception{
            clientSocket = new Socket(remoteAddress, remotePort, address, port);
            consoleReader = new BufferedReader(new InputStreamReader(System.in));
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            log.info("CLIENT IS RUN");

            //Send public key
            keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            System.out.println("PUBLIC KEY IS SENT");
            writeBytes(keyPair.getPublic().getEncoded());

            System.out.println(new String(decryptCipher.doFinal(readBytes())));

    }

    public void sendEndMessage() throws IOException {
        writeBytes(endMessage.getBytes());
        System.out.println("CONNECTION ABORTED");
        stop();
    }

    public byte[] sendRequestForFileMessage(String fileName) throws IOException {
        byte[] bytes = null;
        writeBytes((requestMessage+" "+fileName).getBytes());
        log.info("REQUEST FILE BY NAME: " + fileName);
        String message = new String(readBytes());
        if (message.equals(acceptMessage)) {
            log.info("RECEIVING FILE...");
            bytes = readBytes();
//            writeBytesToFile(bytes, newFileName);
            log.info("FILE IS SUCCESSFULLY RECEIVED AND WROTE");
        } else if (message.equals(denyMessage)) {
            log.info("FILE IS NOT FOUND");
            throw new NoSuchFileException(fileName);
        }
        return bytes;
    }

    public void sendSaveAsMessage(String fileName, byte[] bytes) throws IOException{
        writeBytes((saveMessage+" "+fileName).getBytes());
        log.info("SAVE FILE AS: " + fileName);
        String message = new String(readBytes());
        if (message.equals(acceptMessage)) {
            log.info("SENDING FILE...");
            writeBytes(bytes);
            log.info("FILE IS SENT");
        } else if (message.equals(denyMessage)) {
            log.info("UNEXPECTED ERROR");
        }
    }

    private byte[] readBytes() throws IOException {
        int count;
        byte[] bytes = new byte[FILE_SIZE];
        while ((count = inputStream.read(bytes)) > 0) {
            bytes = Arrays.copyOfRange(bytes, 0, count);
            break;
        }
        return bytes;
    }

    private void writeBytes(byte[] bytes) throws IOException {
        outputStream.write(bytes);
        outputStream.flush();
    }

    public void stop() {
        try {
            clientSocket.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
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

    private void writeBytesToFile(byte[] bytes, String fileName) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(
            storageFolderDestination + fileName);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        bufferedOutputStream.write(bytes, 0, bytes.length);
        bufferedOutputStream.flush();
    }

    private void writeBytesToConsole(byte[] bytes) throws IOException {
        System.out.write(bytes, 0, bytes.length);
        System.out.flush();
    }
}
