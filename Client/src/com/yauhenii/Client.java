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
import java.security.GeneralSecurityException;
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
    private final static String acceptMessage = "accept";
    private final static String denyMessage = "deny";

    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    int port;
    int remotePort;
    InetAddress address;
    InetAddress remoteAddress;

    RSAScrambler rsaScrambler;

    private static Logger log = Logger.getLogger(Client.class.getName());

    public Client(InetAddress remoteAddress, int remotePort, InetAddress address, int port)
        throws GeneralSecurityException {
        this.port = port;
        this.remotePort = remotePort;
        this.address = address;
        this.remoteAddress = remoteAddress;

        rsaScrambler=new RSAScrambler();
    }

    public void start() throws Exception {
        clientSocket = new Socket(remoteAddress, remotePort, address, port);
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
        log.info("CLIENT IS RUN");

        sendPublicKey();
    }

    public void sendPublicKey() throws IOException {
        writeBytes(rsaScrambler.getPublicKey().getEncoded());
        System.out.println("PUBLIC KEY IS SENT");
    }

    public void sendEndMessage() throws IOException {
        writeBytes(endMessage.getBytes());
        System.out.println("CONNECTION ABORTED");
        stop();
    }

    public byte[] sendRequestForFileMessage(String fileName) throws IOException {
        byte[] bytes = null;
        writeBytes((requestMessage + " " + fileName).getBytes());
        log.info("REQUEST FILE BY NAME: " + fileName);
        String message = new String(readBytes());
        if (message.equals(acceptMessage)) {
            log.info("RECEIVING FILE...");
            bytes = readBytes();
            log.info("FILE IS SUCCESSFULLY RECEIVED AND WROTE");
        } else if (message.equals(denyMessage)) {
            log.info("FILE IS NOT FOUND");
            throw new NoSuchFileException(fileName);
        }
        return bytes;
    }

    public void sendSaveAsMessage(String fileName, byte[] bytes) throws IOException {
        writeBytes((saveMessage + " " + fileName).getBytes());
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
}
