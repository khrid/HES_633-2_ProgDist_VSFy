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

public class ClientHandler implements Runnable {

    private Server server;

    private Socket exchangeSocket;

    private DataInputStream dataIn;

    private DataOutputStream dataOut;

    private Client c;

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
                } finally {
                    this.server.logger.warn("Lost connection with " + this.c.getUuid() + ", killing thread");
                }
                //e.printStackTrace();
                interrupt = true;
            }
        }

    }

}
