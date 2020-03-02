package main.server;

import main.client.Client;
import main.network.Exchange;
import main.network.NetworkInterfacePerso;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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

    public Server(NetworkInterfacePerso _nip) {
        address = _nip.getAddress();
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
        try {
            while (true) {
                exchangeSocket = listeningSocket.accept();
                DataInputStream dIn = new DataInputStream(exchangeSocket.getInputStream());
                int messageType = dIn.readInt();

                switch (messageType) {
                    case Exchange.EX_HELLO: // Type A
                        System.out.println("New connection established. Registering client");
                        registerClient(((InetSocketAddress) exchangeSocket.getRemoteSocketAddress()).getAddress().getHostAddress());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + messageType);
                }
                dIn.close();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void registerClient(String client) {
        clients.add(client);
        System.out.println("Registered client " + client + ", total " + clients.size());
    }
}
