package main.server;

import main.network.*;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class AppServer {

    public static void main(String[] args) {
        NetworkInterfacePerso nip = null;

        System.out.println("---------------\n" +
          "- VSFy Server - \n" +
          "---------------");

        NetworkTools nt = new NetworkTools();
        nip = nt.interfaceChooser();
        Server s = new Server(nip);
        s.start();
        s.listen();
    }
}
