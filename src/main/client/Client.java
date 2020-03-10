package main.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import main.network.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Client implements Serializable {

    public static final long serialVersionUID = 42L;

    private String uuid; // identifiant du client, pour s'en sortir quand on lance plusieurs clients sur le même PC

    private InetAddress address; // pour gérer l'adresse IP

    private int port = 50001; // port du serveur

    transient private InetAddress serverAddress; // adresse IP du serveur

    transient private Socket exchangeSocket; // socket d'échange

    transient private DataInputStream dataIn; // pour les données venant du socket

    transient private DataOutputStream dataOut; // pour les données sortant vers le socket

    transient ArrayList<Client> knownClients = new ArrayList<>(); // Liste des clients

    private ArrayList<File> files = new ArrayList<>(); // liste des fichiers que le client peut mettre à dispo

    private int p2pPort;

    transient private DataInputStream p2pDataIn; // pour les données venant du socket

    transient private DataOutputStream p2pDataOut; // pour les données sortant vers le socket

    transient private Socket p2pExchangeSocket;

    public Client(NetworkInterfacePerso nip) {
        address = nip.getAddress(); // récupération de l'adresse IP de la machine
        System.out.println("Client ip " + nip.getIp()); // info utilisateur concernant l'IP
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
            dataIn = new DataInputStream(exchangeSocket.getInputStream()); // flux d'échange - entrée
            dataOut = new DataOutputStream(exchangeSocket.getOutputStream()); // flux d'échange - sortie
            dataOut.writeUTF(ExchangeEnum.HELLO.command); // on fait le handshake du début
            dataOut.writeUTF(
              new GsonBuilder().create().toJson(this)); // on s'envoie au serveur pour qu'il ait les infos du client
            this.uuid = dataIn.readUTF();
            System.out.println("Done."); // info utilisateur que la connexion est établie
            System.out.println("Got my UUID from server : " + this.uuid);
            getClients();
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
                        getClients();
                        break;
                    case LIST_FILES:
                        listFiles();
                        break;
                    case PLAY:
                        getClients();
                        listFiles();
                        System.out.print("Enter client UUID : ");
                        String target = scanner.nextLine();
                        Client client = null;
                        for (Client c : knownClients) {
                            if (c.getUuid().equalsIgnoreCase(target) && !this.getUuid().equalsIgnoreCase(target)) {
                                client = c;
                            }
                        }
                        if (client != null) {
                            System.out.println("Available files for selected client : ");
                            for (File f : client.getFiles()) {
                                System.out.println("\t" + f.getName());
                            }
                            System.out.print("Select file to play : ");
                            target = scanner.nextLine();
                            boolean exists = false;
                            for (File f : client.getFiles()) {
                                if (f.getName().equalsIgnoreCase(target)) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (exists) { // si le client possède bien le fichier demandé
                                serverAddress = InetAddress
                                  .getByName(client.getIp()); // récupération de l'objet InetAdress pour le serveur
                                System.out
                                  .print("Connecting to client " + client.getIp() + ":" + client.getP2pPort() + ". ");
                                p2pExchangeSocket = new Socket(serverAddress, client.getP2pPort());
                                System.out.println("Done.");
                                p2pDataIn = new DataInputStream(
                                  p2pExchangeSocket.getInputStream()); // flux d'échange - entrée
                                p2pDataOut = new DataOutputStream(
                                  p2pExchangeSocket.getOutputStream()); // flux d'échange - sortie
                                p2pDataOut.writeUTF(target);
                                System.out.println("Getting file content.");
                                //Thread.sleep(30000);
                                FileOutputStream fos = new FileOutputStream("C:\\tmp\\vsfy\\out\\test.txt");
                                byte[] buffer = new byte[1024];
                                int count;
                                while ((count = p2pDataIn.read(buffer)) >= 0) {
                                    fos.write(buffer, 0, count);
                                }
                                fos.close();
                                System.out.print("File transmitted from client, closing connection. ");
                                p2pExchangeSocket.close();
                                System.out.println("Done.");
                            } else {
                                System.out.println("Client does not have that file.");
                            }
                        } else {
                            System.out.println("Client not found.");
                        }
                        break;
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

    private void listFiles() {
        if (knownClients.size() > 1) { // supérieur à 1 pcq le serveur renvoie le client lui même
            // TODO adapter le renvoi du serveur pour qu'il ne retourne pas le client
            for (Client c : knownClients) {
                if (!c.getUuid().equalsIgnoreCase(this.getUuid())) {
                    System.out.println("Client " + c.getUuid()/* + " - IP " + c.getIp() + ":"+c.getP2pPort()*/ + " : ");
                    if (c.getFiles().size() > 0) {
                        for (File f : c.getFiles()) {
                            System.out.println("\t" + f.getName() + " - " + f.length() / 1024 + "ko");
                        }
                    } else {
                        System.out.println("\tNothing to share");
                    }
                }
            }
        } else {
            System.out
              .println("No known clients. (Have you launched the " + ExchangeEnum.GET_CLIENTS.command + " command?)");
        }
    }

    public int getP2pPort() {
        return this.p2pPort;
    }

    public Collection<File> getFiles() {
        return files;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    private void getClients() {
        System.out.print("Getting list of server's clients. ");
        try {
            dataOut.writeUTF(ExchangeEnum.GET_CLIENTS.command);
            String ret = dataIn.readUTF();
            knownClients = new Gson().fromJson(new JsonReader(new StringReader(ret)), new TypeToken<List<Client>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(knownClients.size());
        System.out.println("Done.");
    }

    public void scanFolder(String base_dir) {
        Path path = Paths.get(base_dir);
        if (Files.exists(path)) { // si le répertoire existe
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(base_dir))) {
                if (directoryStream.iterator().hasNext()) { // si le répertoire a des fichiers dedans
                    File folder = new File(base_dir);
                    for (File f : Objects.requireNonNull(folder.listFiles())) {
                        if (f.isFile()) {
                            files.add(f);
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

    public void startP2PServer() {
        try {
            ServerSocket p2pSocket = new ServerSocket(0, 10, address);
            this.p2pPort = p2pSocket.getLocalPort();
            //System.out.println(p2pPort);
            Thread t = new Thread(new AcceptClient(p2pSocket));
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
