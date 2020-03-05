package main.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import main.network.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        System.out.println("Client ip " + nip.getIp()); // info utilisateur concernant l'IP
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
            System.out.print("Connecting to server " + serverAddress.getHostAddress() + ":" + port + ". ");
            exchangeSocket = new Socket(serverAddress, port); // socket d'échange entre le client et le serveur
            exchangeSocket.setKeepAlive(true); // pour ne pas kill le socket trop vite
            dataIn = new DataInputStream(exchangeSocket.getInputStream()); // flux d'échange - entrée
            dataOut = new DataOutputStream(exchangeSocket.getOutputStream()); // flux d'échange - sortie
            dataOut.writeUTF(ExchangeEnum.HELLO.command); // on fait le handshake du début
            dataOut.writeUTF(
              new GsonBuilder().create().toJson(this)); // on s'envoie au serveur pour qu'il ait les infos du client
            System.out.println("Done."); // info utilisateur que la connexion est établie

        } catch (IOException e) {
            System.out.println("Could not establish a connection with the server.");
            System.exit(-1);
        }
    }

    public void communicate() {
        Scanner scanner = new Scanner(System.in);
        boolean interrupted = false;
        listActions();
        while (true) {
            if (interrupted) {
                break;
            }
            System.out.print("Enter action : ");
            String command = scanner.nextLine();
            String action = command.split(" ")[0].toUpperCase();

            try {
                switch (ExchangeEnum.valueOf(action)) {
                    case LIST_ACTIONS:
                        listActions();
                        break;
                    case BYE:
                        System.out.print("Disconnecting from server. ");
                        dataOut.writeUTF(action);
                        interrupted = true;
                        System.out.println("Done.");
                        break;
                    case GET_CLIENTS:
                        System.out.print("Getting list of server's clients. ");
                        dataOut.writeUTF(action);
                        String ret = dataIn.readUTF();
                        knownClients = new Gson()
                          .fromJson(new JsonReader(new StringReader(ret)), new TypeToken<List<Client>>() {
                          }.getType());
                        //System.out.println(knownClients.size());
                        System.out.println("Done.");
                        break;
                    case LIST_FILES:
                        if (knownClients.size() > 0) {
                            for (Client c : knownClients) {
                                System.out.println("Client " + c.getUuid() + " / IP " + c.getIp() + " : ");
                                if (c.getFiles().size() > 0) {
                                    for (String s : c.getFiles()) {
                                        System.out.println("\t" + s);
                                    }
                                } else {
                                    System.out.println("\tNothing to share");
                                }
                            }
                        } else {
                            System.out.println(
                              "No known clients. (Have you launched the " + ExchangeEnum.GET_CLIENTS.command
                                + " command?)");
                        }
                        break;
                    /*case "IP":
                        String param = command.split(" ")[1];
                        for (Client c : knownClients) {
                            if (c.getUuid().equalsIgnoreCase(param)) {
                                System.out.println(c.getIp());
                            } else {
                                //System.out.println("No known clients with UUID "+param);
                            }
                        }
                        break;*/
                    default:
                        System.out.println("Action unkown.");
                        listActions();
                        break;
                }
            } catch (IOException e) {
                System.out.println("Lost connection with the server.");
                System.exit(-1);
            } catch (IllegalArgumentException iae) {
                System.out.println("Action unkown.");
                listActions();
            }

        }
    }

    public Collection<String> getFiles() {
        return files;
    }

    public String getUuid() {
        return uuid;
    }

    public String getIp() {
        return address.getHostAddress();
    }

    public void listActions() {
        System.out.println("List of possible actions (not case sensitive) : ");
        for (String s : ExchangeEnum.getAvailableActions()) {
            System.out.println(s);
        }
    }

    public void scanFolder(String base_dir) {
        Path path = Paths.get(base_dir);
        if (Files.exists(path)) { // si le répertoire existe
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(base_dir))) {
                if (directoryStream.iterator().hasNext()) { // si le répertoire a des fichiers dedans
                    File folder = new File(base_dir);
                    for (File f : Objects.requireNonNull(folder.listFiles())) {
                        if (f.isFile()) {
                            files.add(f.toString());
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("No files to share in directory \"" + base_dir + "\"");
            }
        } else {
            System.out.println("Directory \"" + base_dir + "\" does not exist. Carrying on.");
        }
    }
}
