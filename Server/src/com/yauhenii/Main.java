package com.yauhenii;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    private static int port=4004;
    private static int connectionsMax=10;
    private static String addressString="127.0.0.2";


    public static void main(String[] args) {
        try {
            Server server = new Server(port, connectionsMax, InetAddress.getByName(addressString));
            server.start();
        } catch (UnknownHostException exception){
            System.out.println(exception.getMessage());
        }
    }
}
