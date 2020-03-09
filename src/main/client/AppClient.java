package main.client;

import main.network.*;

import java.net.ServerSocket;

public class AppClient {

    public static void main(String[] args) {
        NetworkInterfacePerso nip;
        String serverIp = "172.16.41.134"; //192.168.2.223
        ServerSocket mySkServer;
        String BASE_DIR = "/tmp/vsfy";

        System.out.println("---------------\n" +
                "- VSFy Client - \n" +
                "---------------");

        NetworkTools nt = new NetworkTools();
        nip = nt.interfaceChooser();
        Client c = new Client(nip);
        c.scanFolder(BASE_DIR);
        if(c.getFiles().size() > 0) {
            c.startP2PServer();
        }
        System.out.println(c.getP2pPort());
        c.connectToServer(serverIp);
        c.communicate();
    }
}
