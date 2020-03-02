package main.client;

import main.network.*;
import main.server.Server;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class AppClient {

    public static void main(String[] args) {
        NetworkInterfacePerso nip = null;
        String serverIp = "172.16.41.134";

        System.out.println("---------------\n" +
                "- VSFy Client - \n" +
                "---------------");

        NetworkTools nt = new NetworkTools();
        nip = nt.interfaceChooser();
        Client c = new Client(nip);
        c.connectToServer(serverIp);
    }
}
