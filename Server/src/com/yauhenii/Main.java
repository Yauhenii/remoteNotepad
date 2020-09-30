package com.yauhenii;


import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Main {

    private static int port = 4004;
    private static int connectionsMax = 10;
    private static String addressString = "127.0.0.2";


    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        try {
            Server server = new Server(port, connectionsMax, InetAddress.getByName(addressString));
            server.start();
        } catch (UnknownHostException exception){
            System.out.println(exception.getMessage());
        }
    }

}
