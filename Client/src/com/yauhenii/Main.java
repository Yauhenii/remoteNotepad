package com.yauhenii;

import com.yauhenii.client.Client;
import com.yauhenii.gui.MainWindow;
import java.net.InetAddress;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Main {

    private static int port = 4000;
    private static String addressString = "127.0.0.3";
    private static int remotePort = 4004;
    private static String remoteAddressString = "127.0.0.2";

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        try {
            Client client = new Client(InetAddress.getByName(remoteAddressString), remotePort,
                InetAddress.getByName(addressString), port);
            MainWindow mainWindow = new MainWindow(client);
            mainWindow.setVisible(true);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}
