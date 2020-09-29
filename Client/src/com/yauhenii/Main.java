package com.yauhenii;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    private static int port=4000;
    private static String addressString="127.0.0.4";
    private static int remotePort=4004;
    private static String remoteAddressString="127.0.0.2";

    public static void main(String[] args) {
        try {
            Client client = new Client(InetAddress.getByName(remoteAddressString),remotePort,InetAddress.getByName(addressString),port);
            client.start();
            MainWindow mainWindow=new MainWindow(client);
            mainWindow.setVisible(true);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }
}
