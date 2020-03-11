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

public class Server {

    private InetAddress address;

    private ServerSocket listeningSocket; // le socket server

    private Socket exchangeSocket;      // le socket d'échange

    private int buffer = 5;

    private int port = 50001;

    private ArrayList<Client> clients = new ArrayList<>();

    public Server(NetworkInterfacePerso nip)  {
        address = nip.getAddress();
    }

    public void start() {
        try {
            listeningSocket = new ServerSocket(port, buffer, address);
            System.out.println("Used IP address : " + listeningSocket.getInetAddress().getHostAddress());
            System.out.println("Listening port  : " + listeningSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        System.out.println("Server is now listening for connections...");

        while (true) {
            try {
                exchangeSocket = listeningSocket.accept();
                DataInputStream dataIn = new DataInputStream(exchangeSocket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(exchangeSocket.getOutputStream());
                Thread t = new Thread(new ClientHandler(this, exchangeSocket, dataIn, dataOut));
                System.out.println("New connection established, assigning new thread");
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
        System.out.println("Registered client " + client.getUuid() + " with ip address " + client.getIp()+". Total clients : "+ clients.size());
    }

    public void removeClient(Client client) {
        clients.remove(client);
        System.out.println("Removed client " + client.getUuid() + ". Total clients : " + clients.size());
    }

    public ArrayList<Client> getClients() {
        return clients;
    }
}
