package com.yauhenii;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    private static int port=4000;
    private static String addressString="127.0.0.3";
    private static int remotePort=4004;
    private static String remoteAddressString="127.0.0.2";

    public static void main(String[] args) {
        try {
            Client client = new Client(InetAddress.getByName(remoteAddressString),remotePort,InetAddress.getByName(addressString),port);
            client.start();
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }
}
