package com.yauhenii;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import lombok.SneakyThrows;

//@Log4j2
public class Server {

    private ServerSocket serverSocket;

    private int port;
    private int connectionsMax;
    private InetAddress address;

    private LinkedList<ServerThread> serverThreads;

    public Server(int port, int connectionsMax, InetAddress address) {
        this.port = port;
        this.connectionsMax = connectionsMax;
        this.address = address;

        serverThreads = new LinkedList<>();
    }

//    @SneakyThrows
    public void start() {
        try {
            serverSocket = new ServerSocket(port, connectionsMax, address);
            System.out.println("SERVER is run");

            while (true){
                Socket clientSocket= serverSocket.accept();
                serverThreads.add(new ServerThread(clientSocket));
            }

        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
