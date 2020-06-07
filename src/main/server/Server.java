package main.server;

import main.client.Client;
import main.network.NetworkInterfacePerso;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Librairie qui contient toutes les fonctions du serveur:
 * -démarrage du serveur
 * -attente de connexions clientes
 * -enregistrement et suppression des clients
 */
public class Server {

    private InetAddress address;

    private ServerSocket listeningSocket; // le socket server

    private Socket exchangeSocket;      // le socket d'échange

    private int buffer = 5;

    private int port = 50001;

    private ArrayList<Client> clients = new ArrayList<>();

    //public static Logger logger = LogManager.getLogger(Server.class);
    public static Logger logger = LogManager.getLogger();

    /**
     * Contructeur de la classe Serveur
     *
     * @param nip Toutes les informations de la carte réseau séléctionnée par l'utilisateur
     */
    public Server(NetworkInterfacePerso nip)  {
        address = nip.getAddress();
    }

    /**
     * Méthode qui démarre le serveur avec les paramètres définis auparavant
     */
    public void start() {
        logger.debug("Starting server");
        try {
            listeningSocket = new ServerSocket(port, buffer, address);
            logger.info("Used IP address : " + listeningSocket.getInetAddress().getHostAddress());
            logger.info("Listening port  : " + listeningSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui écoute en permanence les connexions clientes
     * Affecte un thread à chaque nouvelle connexion
     */
    public void listen() {
        logger.info("Server is now listening for connections...");

        while (true) {
            try {
                exchangeSocket = listeningSocket.accept();
                DataInputStream dataIn = new DataInputStream(exchangeSocket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(exchangeSocket.getOutputStream());
                Thread t = new Thread(new ClientHandler(this, exchangeSocket, dataIn, dataOut));
                logger.info("New connection established, assigning new thread");
                t.start();
            } catch (Exception e) {
                try {
                    exchangeSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    /**
     * Ajoute un client dans la liste des clients gérés par le serveur
     *
     * @param client un objet Client
     */
    public void registerClient(Client client) {
        clients.add(client);
        logger.info("Registered client " + client.getUuid() + " with ip address " + client.getIp()+". Total clients : "+ clients.size());
    }

    /**
     * Supprime un client de la liste des clients gérés par le serveur
     *
     * @param client un objet Client
     */
    public void removeClient(Client client) {
        clients.remove(client);
        logger.info("Removed client " + client.getUuid() + ". Total clients : " + clients.size());
    }

    /**
     * Retourne une liste des clients connectés au serveur
     *
     * @return ArrayList d'objet Client des clients connectés au serveur
     */
    public ArrayList<Client> getClients() {
        return clients;
    }
}
