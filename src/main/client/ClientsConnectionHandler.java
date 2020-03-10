package main.client;

import main.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientsConnectionHandler extends Thread {

    private ServerSocket listeningSocket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private InetAddress hostAddress;
    private int portP2p;

    public ClientsConnectionHandler(InetAddress hostAddress, int portP2p) {
        this.hostAddress = hostAddress;
        this.portP2p = portP2p;
    }

    @Override
    public void start() {
        System.out.println("Waiting on another client connection.");
        new Thread() {
            public void run() {
                boolean interrupted = false;
                try {
                    while (true) {
                        if(interrupted) {
                            break;
                        }
                        listeningSocket = new ServerSocket(portP2p, 5, hostAddress);
                        Socket exchangeSocket = listeningSocket.accept();
                        DataInputStream dataIn = new DataInputStream(exchangeSocket.getInputStream());
                        DataOutputStream dataOut = new DataOutputStream(exchangeSocket.getOutputStream());

                        try {
                            System.out.println(dataIn.readUTF());
                            interrupted = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
