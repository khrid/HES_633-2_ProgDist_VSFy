package main.client;

import main.server.ClientHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Thread qui gère les sockets pour chaque connexion P2P avec un client
 */
public class AcceptClient implements Runnable {

    private ServerSocket serverSocket;

    private Socket exchangeSocket;

    /**
     * Constructeur
     * @param s le socket d'échange du serveur P2P
     */
    public AcceptClient(ServerSocket s) {
        this.serverSocket = s;
    }

    /**
     * Méthode qui démarre le serveur P2P
     */
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
