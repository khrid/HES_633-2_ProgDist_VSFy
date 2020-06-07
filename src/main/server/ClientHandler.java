package main.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import main.client.Client;
import main.network.ExchangeEnum;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Thread qui gère les sockets, les flux et l'UUID mis à disposition pour chaque client
 */
public class ClientHandler implements Runnable {

    private Server server;

    private Socket exchangeSocket;

    private DataInputStream dataIn;

    private DataOutputStream dataOut;

    private Client c;

    /**
     * Constructeur
     * @param server le serveur qui va mettre en relation les clients pour diffusion P2P
     * @param exchangeSocket le socket d'échange de données entre les clients
     * @param dataIn le flux de données entrant
     * @param dataOut le flux de données sortant
     */
    public ClientHandler(Server server, Socket exchangeSocket, DataInputStream dataIn, DataOutputStream dataOut) {
        this.server = server;
        this.exchangeSocket = exchangeSocket;
        this.dataIn = dataIn;
        this.dataOut = dataOut;
        //this.uuid = "";

        try {
            this.exchangeSocket.setKeepAlive(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui lance le thread pour la gestion du client:
     * -connexion du client et affectation d'un UUID
     * -envoi des informations selon les instructions données par le client
     * -diffusion des médias
     * -déconnexion du client
     */
    @Override
    public void run() {
        boolean interrupt = false;
        String input;
        while (true) {
            if (interrupt) {
                if (this.server != null) this.server.removeClient(c);
                break;
            }
            try {
                if (this.server != null) { // on communique avec le serveur
                    input = dataIn.readUTF();
                    //System.out.println("Data received : " + input);
                    switch (ExchangeEnum.valueOf(input)) {
                        case HELLO:
                            this.server.logger.info("New client saying hello.");
                            c = new Gson().fromJson(new JsonReader(new StringReader(dataIn.readUTF())), Client.class);
                            c.setUuid(UUID.randomUUID().toString()); // génération de l'identifiant du client
                            this.server.registerClient(c);
                            dataOut.writeUTF(c.getUuid());
                            break;
                        case BYE:
                            this.server.logger.info("Client " + this.c.getUuid() + " saying goodbye.");
                            dataIn.close();
                            interrupt = true;
                            break;
                        case GET_CLIENTS:
                            this.server.logger.info("Client " + this.c.getUuid() + " asking for clients list.");
                            ArrayList<Client> clientsWithoutCurrent = new ArrayList<>();
                            for (Client client :
                                    server.getClients()) {
                                if(!c.getUuid().equalsIgnoreCase(client.getUuid()))
                                    clientsWithoutCurrent.add(client);
                            }
                            dataOut.writeUTF(new GsonBuilder().create().toJson(clientsWithoutCurrent));
                            break;
                        default:
                            break;
                    }
                } else { // on communique avec un client (p2p)
                    // contient le nom de fichier à streamer
                    input = dataIn.readUTF();
                    // création d'un fichier
                    File file = new File("/tmp/vsfy/" + input);
                    // buffer pour le contenu du fichier
                    byte[] buffer = new byte[(int)file.length()];
                    // le stream avec le fichier
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                    // on écrit dans le buffer
                    in.read(buffer,0, buffer.length);
                    // on écrit dans le socket
                    dataOut.write(buffer, 0, buffer.length);
                    // on envoie
                    dataOut.flush();
                    // on clot le socket
                    exchangeSocket.close();
                }
            } catch (IOException e) {
                try {
                    dataIn.close();
                } catch (IOException ex) {
                    this.server.logger.error("Could not close connection gracefuly.");
                    this.server.logger.error(ex.getMessage());
                }
                //e.printStackTrace();
                interrupt = true;
            }
        }

    }

}
