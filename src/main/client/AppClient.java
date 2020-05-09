package main.client;

import main.network.NetworkInterfacePerso;
import main.network.NetworkTools;

import java.net.ServerSocket;

public class AppClient {

    public static void main(String[] args) {
        NetworkInterfacePerso nip;
        String serverIp = "192.168.2.238"; //192.168.2.223 - 172.16.41.134
        ServerSocket mySkServer;


        System.out.println("---------------\n" + "- VSFy Client - \n" + "---------------");

        NetworkTools nt = new NetworkTools(AppClient.class.getName());
        nip = nt.interfaceChooser();
        Client c = new Client(nip);
        c.scanFolder();
        if (c.getFiles().size() > 0) {
            c.startP2PServer();
        }
        //System.out.println(c.getP2pPort());
        c.connectToServer(serverIp);
        c.communicate();
    }
}
