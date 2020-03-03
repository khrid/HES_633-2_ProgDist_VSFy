package main.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import main.network.Exchange;
import main.network.NetworkInterfacePerso;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class Client implements Serializable {

    private String uuid; // identifiant du client, pour s'en sortir quand on lance plusieurs clients sur le même PC

    private InetAddress address; // pour gérer l'adresse IP

    private int port = 50001; // port du serveur

    transient private InetAddress serverAddress; // adresse IP du serveur

    transient private Socket exchangeSocket; // socket d'échange

    transient private DataInputStream dataIn; // pour les données venant du socket

    transient private DataOutputStream dataOut; // pour les données sortant vers le socket

    transient Collection<Client> knownClients = new ArrayList<>(); // Liste des clients

    private Collection<String> files = new ArrayList<>(); // liste des fichiers que le client peut mettre à dispo

    public Client(NetworkInterfacePerso nip) {
        uuid = UUID.randomUUID().toString(); // génération de l'identifiant du client
        address = nip.getAddress(); // récupération de l'adresse IP de la machine
        System.out.println("Client will use ip " + nip.getIp()); // info utilisateur concernant l'IP
        System.out.println("Client UUID : " + uuid); // info utilisateur concernant l'id
    }

    /**
     * Méthode de connexion au serveur
     *
     * @param serverName l'adresse IP du serveur
     */
    public void connectToServer(String serverName) {
        try {
            serverAddress = InetAddress.getByName(serverName); // récupération de l'objet InetAdress pour le serveur
            exchangeSocket = new Socket(serverAddress, port); // socket d'échange entre le client et le serveur
            exchangeSocket.setKeepAlive(true); // pour ne pas kill le socket trop vite
            dataIn = new DataInputStream(exchangeSocket.getInputStream()); // flux d'échange - entrée
            dataOut = new DataOutputStream(exchangeSocket.getOutputStream()); // flux d'échange - sortie
            dataOut.writeUTF(Exchange.HELLO); // on fait le handshake du début
            dataOut.writeUTF(
              new GsonBuilder().create().toJson(this)); // on s'envoie au serveur pour qu'il ait les infos du client

            System.out.println("Handshake with server done."); // info utilisateur que la connexion est établie

        } catch (IOException e) {
            System.out.println("Could not establish a connection with the server.");
            System.exit(-1);
        }
    }

    public void communicate() {
        Scanner scanner = new Scanner(System.in);
        boolean interrupted = false;
        while (true) {
            if (interrupted) {
                break;
            }
            System.out.print("Enter action : ");
            String action = scanner.nextLine().toUpperCase();
            try {
                if (action.equalsIgnoreCase(Exchange.BYE)) {
                    dataOut.writeUTF(action);
                    interrupted = true;
                } else if (action.equalsIgnoreCase(Exchange.GETCLIENTS)) {
                    dataOut.writeUTF(action);
                    String ret = dataIn.readUTF();
                    knownClients = new Gson()
                      .fromJson(new JsonReader(new StringReader(ret)), new TypeToken<List<Client>>() {
                      }.getType());
                    System.out.println(knownClients.size());
                }
            } catch (IOException e) {
                System.out.println("Lost connection with the server.");
                System.exit(-1);
            }

        }
    }

    public String getUuid() {
        return uuid;
    }

    public void scanFolder(String base_dir) {
        File folder = new File(base_dir);

        for (File f : Objects.requireNonNull(folder.listFiles())) {
            if (f.isFile()) {
                files.add(f.toString());
            }
        }
    }
}
