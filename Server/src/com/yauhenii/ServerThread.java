package com.yauhenii;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class ServerThread extends Thread {

    private final static int FILE_SIZE = 8192;//6022386;
    private final static String endMessage = "exit";
    private final static String requestMessage = "request";
    private final static String saveMessage = "save";
    private final static String echoMessage = "echo";
    private final static String acceptMessage = "accept";
    private final static String denyMessage = "deny";

    private final static String storageFolderDestination = "/Users/zhenyamordan/Desktop/Учеба/4 курс 1 сем/КБРС/Task2/Server/storage/";

    private Socket clientSocket;
    private String clientAddress;
    private InputStream inputStream;
    private OutputStream outputStream;

    private static Logger log = Logger.getLogger(Server.class.getName());

    Key keyAES;
    PublicKey publicKeyRSA;
    Cipher cipherAES;
    Cipher encryptCipherRSA;

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
            byte[] bytes;
            String command;
            String[] commandSplit;
            //Get public key
            bytes=readBytes();
            System.out.println("GOT PUBLIC KEY");
            encryptCipherRSA = Cipher.getInstance("RSA");
            publicKeyRSA = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
            encryptCipherRSA.init(Cipher.ENCRYPT_MODE, publicKeyRSA);
            //Generate and send AES key

            writeBytes(encryptCipherRSA.doFinal("hello".getBytes()));

            while (true) {
                bytes = readBytes();
                command = new String(bytes);
                commandSplit = command.split(" ");

                if (commandSplit[0].equals(endMessage)) {
                    log.info(clientAddress + ": CONNECTION ABORTED");
                    stopClient();
                    break;
                } else if (commandSplit[0].equals(requestMessage)) {
                    String fileName = commandSplit[1];
                    log.info(clientAddress + ": GOT REQUEST FOR FILE " + fileName);
                    try {
                        bytes = readBytesFromFile(fileName);
                        log.info(clientAddress + ": SENDING ACCEPT MESSAGE... " + fileName);
                        writeBytes(acceptMessage.getBytes());
                        log.info(clientAddress + ": SENDING FILE... " + fileName);
                        writeBytes(bytes);
                        log.info(clientAddress + ": FILE SENT " + fileName);
                    } catch (IOException exception){
                        log.info(clientAddress + ": SENDING DENY MESSAGE... " + fileName);
                        writeBytes(denyMessage.getBytes());
                        log.info(clientAddress + ": FILE IS NOT FOUND " + fileName);
                    }

                } else if (commandSplit[0].equals(echoMessage)) {
                    String message = commandSplit[1];
                    log.info(clientAddress + ": GOT ECHO MESSAGE ");
                    writeBytes(message.getBytes());
                    log.info(clientAddress + ": SENT ECHO MESSAGE BACK");
                } else if (commandSplit[0].equals(saveMessage)){
                    String fileName = commandSplit[1];
                    log.info(clientAddress + ": GOT REQUEST FOR FILE SAVING " + fileName);
                    try {
                    log.info(clientAddress + ": SENDING ACCEPT MESSAGE... " + fileName);
                    writeBytes(acceptMessage.getBytes());
                    bytes = readBytes();
                    writeBytesToFile(bytes,fileName);
                    } catch (IOException exception){
                        log.info(clientAddress + ": SENDING DENY MESSAGE... " + fileName);
                    }

                } else {
                    log.info(clientAddress + ": INVALID COMMAND");
                }
            }
        } catch (Exception exception) {
            log.warning(exception.getMessage());
        }
    }

    private byte[] readBytes() throws IOException, IllegalBlockSizeException, BadPaddingException {
        int count;
        byte[] bytes = new byte[FILE_SIZE];
        while ((count = inputStream.read(bytes)) > 0) {
            bytes=Arrays.copyOfRange(bytes, 0, count);
            break;
        }
        return bytes;
    }

    private byte[] readBytesFromFile(String filename) throws IOException {
        File file = new File(storageFolderDestination + filename);
        return Files.readAllBytes(file.toPath());
    }

    private void writeBytes(byte[] bytes) throws IOException, IllegalBlockSizeException, BadPaddingException {
        outputStream.write(bytes);
        outputStream.flush();
    }

    private void writeBytesToFile(byte[] bytes, String fileName) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(
            storageFolderDestination + fileName);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        bufferedOutputStream.write(bytes, 0, bytes.length);
        bufferedOutputStream.flush();
    }

    public void stopClient() {
        try {
            clientSocket.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}

