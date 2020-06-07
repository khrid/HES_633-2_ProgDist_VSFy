package main.client;

import main.server.ClientHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AcceptClient implements Runnable {

    private ServerSocket serverSocket;

    private Socket exchangeSocket;

    public AcceptClient(ServerSocket s) {
        this.serverSocket = s;
    }

    @Override public void run() {
        try {
            while (true) {
                exchangeSocket = serverSocket.accept();
                DataInputStream dataIn = new DataInputStream(exchangeSocket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(exchangeSocket.getOutputStream());
                Thread t = new Thread(new ClientHandler(null, exchangeSocket, dataIn, dataOut));
                //System.out.println("New connection established, assigning new thread");
                t.start();
                //System.out.println("new client");
                //exchangeSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
