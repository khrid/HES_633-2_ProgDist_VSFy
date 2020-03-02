package main.client;

import main.network.Exchange;
import main.network.NetworkInterfacePerso;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private InetAddress address;
    private InetAddress serverAddress;
    private Socket exchangeSocket;
    private int port = 50001;

    public Client(NetworkInterfacePerso _nip) {
        address = _nip.getAddress();
        System.out.println("Client will use ip " + _nip.getIp());
    }

    public void connectToServer(String _serverName) {
        try {
            serverAddress = InetAddress.getByName(_serverName);
            exchangeSocket = new Socket(serverAddress, port);
            DataOutputStream dOut = new DataOutputStream(exchangeSocket.getOutputStream());
            dOut.writeInt(Exchange.EX_HELLO);
            dOut.writeUTF("client");
            dOut.flush(); // Send off the data
            exchangeSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
