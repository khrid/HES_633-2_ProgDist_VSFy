package main.server;

import main.network.*;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        String serverIp = "";

        System.out.println("- VSFy Server - ");

        NetworkTools nt = new NetworkTools();

        ArrayList<NetworkInterfacePerso> availableInterfaces = nt.getAvalaibleInterfaces();
        if (availableInterfaces.size() > 1) { // si plus d'une interface on demande à l'utilisateur
            Scanner scanner = new Scanner(System.in);
            int choice = 0;
            System.out.println("Please chose which interface to use");
            int idx = 1;
            for (NetworkInterfacePerso nip : availableInterfaces) {
                System.out.println(
                  "[" + (idx++) + "] - " + nip.getInterfaceName() + " - " + nip.getInterfaceDisplayname() + " - " + nip
                    .getIp());
            }
            do {
                System.out.print("Choice : ");
                choice = scanner.nextInt();
            } while (choice < 0 || choice > availableInterfaces.size());
            serverIp = availableInterfaces.get(choice-1).getIp(); // index du tab != index montré à l'utilisateur
        } else { // si une seule interface, on sait laquelle utiliser
            serverIp = availableInterfaces.get(0).getIp();
        }

        Server s = new Server(serverIp);
    }
}
