package main.network;

import main.server.AppServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class NetworkTools {

    public static Logger logger = LogManager.getLogger(NetworkTools.class);
    boolean log = false;

    public NetworkTools(String callerName) {
        if(callerName.equals("main.server.AppServer")) {
            log = true;
        }
    }

    /**
     * Returns all available interfaces with their name, display name and ip
     * @return ArrayList with the interfaces found
     */
    public ArrayList<NetworkInterfacePerso> getAvalaibleInterfaces() {
        if(log) {
            logger.debug("Getting availables network interfaces.");
        }
        // interface name // display name // ip
        ArrayList<NetworkInterfacePerso> res = new ArrayList<>();
        Enumeration<NetworkInterface> allni = null;
        try {
            allni = NetworkInterface.getNetworkInterfaces();
            while (allni.hasMoreElements()) {
                NetworkInterface ni = allni.nextElement();
                if (ni.isUp()) {
                    Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inet = inetAddresses.nextElement();
                        if(!inet.isLinkLocalAddress() && !inet.isLoopbackAddress()) {
                            if(log)logger.debug("Found interface "+ni.getName()+" ("+ni.getDisplayName()+", ip "+inet.getHostAddress()+")");
                            res.add(new NetworkInterfacePerso(ni.getName(), ni.getDisplayName(), inet.getHostAddress(), inet));
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return res;
    }

    public NetworkInterfacePerso interfaceChooser() {
        NetworkInterfacePerso selectedInterface = null;
        ArrayList<NetworkInterfacePerso> availableInterfaces = getAvalaibleInterfaces();
        if (availableInterfaces.size() > 1) { // si plus d'une interface on demande à l'utilisateur
            Scanner scanner = new Scanner(System.in);
            int choice = 0;
            System.out.println("Please choose which interface to use");
            int idx = 1;
            for (NetworkInterfacePerso nip : availableInterfaces) {
                System.out.println(
                  "[" + (idx++) + "] " + nip.getInterfaceName() + " - " + nip.getInterfaceDisplayname() + " - " + nip
                    .getIp());
            }
            do {
                System.out.print("Choice [1-"+availableInterfaces.size()+"] : ");
                choice = scanner.nextInt();
            } while (choice < 0 || choice > availableInterfaces.size());
            selectedInterface = availableInterfaces.get(choice-1); // index du tab != index montré à l'utilisateur
        } else { // si une seule interface, on sait laquelle utiliser
            selectedInterface = availableInterfaces.get(0);
        }

        if(log) {
            logger.debug("Selected interface : "+selectedInterface.getInterfaceName()+".");
        }
        return selectedInterface;
    }
}
