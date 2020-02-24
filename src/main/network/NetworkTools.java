package main.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class NetworkTools {

    /**
     * Returns all available interfaces with their name, display name and ip
     * @return ArrayList with the interfaces found
     */
    public ArrayList<NetworkInterfacePerso> getAvalaibleInterfaces() {
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
                            res.add(new NetworkInterfacePerso(ni.getName(), ni.getDisplayName(), inet.getHostAddress()));
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return res;
    }
}
