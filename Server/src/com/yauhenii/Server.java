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
import java.util.logging.Logger;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

//@Log
public class Server {

    private ServerSocket serverSocket;

    private int port;
    private int connectionsMax;
    private InetAddress address;

    private LinkedList<ServerThread> serverThreads;

    private static Logger log = Logger.getLogger(Server.class.getName());

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
            log.info("SERVER IS RUN");
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
