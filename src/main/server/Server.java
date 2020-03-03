package main.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import main.client.Client;
import main.network.NetworkInterfacePerso;

import java.io.*;
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

    private ArrayList<String> clients = new ArrayList<>();

    private ArrayList<Client> clientsObject = new ArrayList<>();

    public Server(NetworkInterfacePerso nip) {
        address = nip.getAddress();
        //System.out.println("Server will use ip " + _nip.getIp());
    }

    public void start() {
        try {

            listeningSocket = new ServerSocket(port, buffer, address);
            System.out.println("Default timeout : " + listeningSocket.getSoTimeout());
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
                Thread t = new ClientHandler(this, exchangeSocket, dataIn, dataOut);
                System.out.println("New connection established, assigning new thread ("+t.getName()+")");
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
        clientsObject.add(client);
        System.out.println("Registered client "+client.getUuid());
    }

    public void removeClient(Client client) {
        clientsObject.remove(client);
        System.out.println("Removed client " + client.getUuid() + ", total " + clientsObject.size());
    }

    public ArrayList<String> getClients() {
        return clients;
    }

    public Object getClientsObject() {
        return clientsObject;
    }
}
