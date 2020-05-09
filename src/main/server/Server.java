package main.server;

import main.client.Client;
import main.network.NetworkInterfacePerso;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {

    private InetAddress address;

    private ServerSocket listeningSocket; // le socket server

    private Socket exchangeSocket;      // le socket d'échange

    private int buffer = 5;

    private int port = 50001;

    private ArrayList<Client> clients = new ArrayList<>();

    public static Logger logger = LogManager.getLogger(Server.class);


    public Server(NetworkInterfacePerso nip)  {
        address = nip.getAddress();
    }

    public void start() {
        logger.debug("Starting server");
        try {
            listeningSocket = new ServerSocket(port, buffer, address);
            logger.info("Used IP address : " + listeningSocket.getInetAddress().getHostAddress());
            logger.info("Listening port  : " + listeningSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        logger.info("Server is now listening for connections...");

        while (true) {
            try {
                exchangeSocket = listeningSocket.accept();
                DataInputStream dataIn = new DataInputStream(exchangeSocket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(exchangeSocket.getOutputStream());
                Thread t = new Thread(new ClientHandler(this, exchangeSocket, dataIn, dataOut));
                logger.info("New connection established, assigning new thread");
                t.start();
            } catch (Exception e) {
                try {
                    exchangeSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    public void registerClient(Client client) {
        clients.add(client);
        logger.info("Registered client " + client.getUuid() + " with ip address " + client.getIp()+". Total clients : "+ clients.size());
    }

    public void removeClient(Client client) {
        clients.remove(client);
        logger.info("Removed client " + client.getUuid() + ". Total clients : " + clients.size());
    }

    public ArrayList<Client> getClients() {
        return clients;
    }
}
