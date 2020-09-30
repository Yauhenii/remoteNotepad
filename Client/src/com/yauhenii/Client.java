package com.yauhenii;

import com.yauhenii.scrambler.RSAScrambler;
import com.yauhenii.scrambler.SerpentScrambler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Arrays;
import java.util.logging.Logger;

public class Client {

    private final static int FILE_SIZE = 8192;//6022386;
    private final static String endMessage = "exit";
    private final static String requestMessage = "request";
    private final static String saveMessage = "save";
    private final static String acceptMessage = "accept";
    private final static String denyMessage = "deny";
    private final static String generateMessage = "generate";

    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    int port;
    int remotePort;
    InetAddress address;
    InetAddress remoteAddress;

    RSAScrambler rsaScrambler;
    SerpentScrambler serpentScrambler;

    private static Logger log = Logger.getLogger(Client.class.getName());

    public Client(InetAddress remoteAddress, int remotePort, InetAddress address, int port)
        throws IOException, GeneralSecurityException {
        this.port = port;
        this.remotePort = remotePort;
        this.address = address;
        this.remoteAddress = remoteAddress;

        rsaScrambler = new RSAScrambler();
        serpentScrambler = null;

        start();
    }

    public void start() throws IOException, GeneralSecurityException {
        log.info("CLIENT IS RUN");
        clientSocket = new Socket(remoteAddress, remotePort, address, port);
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();

        sendPublicKey();

        receiveSessionKey();

    }

    public void sendGenerateMessage() throws IOException, GeneralSecurityException{
        log.info("SEND GENERATE NEW SESSION KEY MESSAGE");
        writeBytes(generateMessage.getBytes());
        serpentScrambler = null;
        receiveSessionKey();
        log.info("SESSION KEY IS CHANGED");
    }

    public void sendEndMessage() throws IOException, GeneralSecurityException {
        log.info("SEND END MESSAGE");
        writeBytes(endMessage.getBytes());
        stop();
        log.info("CONNECTION ABORTED");
    }

    public byte[] sendRequestForFileMessage(String fileName)
        throws IOException, GeneralSecurityException {
        log.info("SEND REQUEST MESSAGE");
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

    public void sendSaveAsMessage(String fileName, byte[] bytes)
        throws IOException, GeneralSecurityException {
        log.info("SEND SAVE AS MESSAGE");
        writeBytes((saveMessage + " " + fileName).getBytes());
        log.info("SAVE FILE AS: " + fileName);
        String message = new String(readBytes());
        if (message.equals(acceptMessage)) {
            log.info("SENDING FILE...");
            writeBytes(bytes);
            log.info("FILE IS SENT");
        } else if (message.equals(denyMessage)) {
            log.warning("UNEXPECTED ERROR");
        }
    }

    public void stop() {
        try {
            clientSocket.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public Key getSessionKey(){
        return serpentScrambler.getKey();
    }

    //utils

    private byte[] readBytes() throws IOException, GeneralSecurityException {
        byte[] bytes = null;
        int count;
        bytes = new byte[FILE_SIZE];
        while ((count = inputStream.read(bytes)) > 0) {
            bytes = Arrays.copyOfRange(bytes, 0, count);
            break;
        }
        if (!isConnectionSecured()) {
            log.warning("CONNECTION IS NOT SECURED");
        } else {
            bytes = serpentScrambler.decrypt(bytes);
        }
        return bytes;
    }

    private void writeBytes(byte[] bytes) throws IOException, GeneralSecurityException {
        if (!isConnectionSecured()) {
            log.warning("CONNECTION IS NOT SECURED");
        } else {
            bytes = serpentScrambler.encrypt(bytes);
        }
        outputStream.write(bytes);
        outputStream.flush();
    }

    private void receiveSessionKey() throws IOException, GeneralSecurityException {
        log.info("GET SESSION KEY");
        byte[] sessionKey = readBytes();
        sessionKey = rsaScrambler.decrypt(sessionKey);
        byte[] iv = readBytes();
        iv = rsaScrambler.decrypt(iv);
        serpentScrambler = new SerpentScrambler(sessionKey, iv);
        log.info("CONNECTION IS SECURED");
    }

    private void sendPublicKey() throws IOException, GeneralSecurityException {
        log.info("SEND PUBLIC KEY");
        writeBytes(rsaScrambler.getPublicKey().getEncoded());
        log.info("PUBLIC KEY IS SENT");
    }

    private boolean isConnectionSecured() {
        if (serpentScrambler != null) {
            return true;
        } else {
            return false;
        }
    }
}
