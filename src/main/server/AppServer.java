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

/**
 * Interface console pour le serveur
 */
public class AppServer {

    public static Logger logger = LogManager.getLogger(AppServer.class);

    /**
     * Méthode de lancement du programme côté serveur
     * Initialisation
     * Demande à l'utilisateur de choisir une carte réseau
     * Attente de connexions
     */
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
