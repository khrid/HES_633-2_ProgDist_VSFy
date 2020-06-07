package main.server;

import main.network.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;


public class AppServer {

    public static Logger logger = LogManager.getLogger(AppServer.class);

    public static void main(String[] args) {
        NetworkInterfacePerso nip = null;

        logger.info("---------------");
        logger.info("- VSFy Server -");
        logger.info("---------------");
        logger.debug("Starting application VSFy Server");


        NetworkTools nt = new NetworkTools(AppServer.class.getName());
        nip = nt.interfaceChooser();
        Server s = new Server(nip);
        s.start();
        s.listen();
    }
}
