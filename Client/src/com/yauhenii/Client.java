package com.yauhenii;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private final String endMessage="exit";

    private Socket clientSocket;
    private BufferedReader consoleReader;
    private BufferedReader in;
    private BufferedWriter out;

    int port;
    int remotePort;
    InetAddress address;
    InetAddress remoteAddress;

    public Client(InetAddress remoteAddress, int remotePort, InetAddress address, int port) {
        this.port = port;
        this.remotePort = remotePort;
        this.address = address;
        this.remoteAddress = remoteAddress;
    }

    public void start(){
        try {
            clientSocket = new Socket(remoteAddress,remotePort,address,port);
            System.out.println("Client is run");

            consoleReader = new BufferedReader(new InputStreamReader(System.in));

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            while (true) {
                String word = consoleReader.readLine();
                out.write(word + "\n");
                out.flush();
                String serverWord = in.readLine();
                if(serverWord.equals(endMessage)){
                    break;
                }
                System.out.println(serverWord);
            }

            stop();
        } catch (IOException exception){
            System.out.println(exception.getMessage());
        }
    }

    public void stop() {
        try {
            clientSocket.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
