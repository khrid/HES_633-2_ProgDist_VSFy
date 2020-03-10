package main.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import main.client.Client;
import main.network.ExchangeEnum;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
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
                            System.out.println("New client saying hello.");
                            c = new Gson().fromJson(new JsonReader(new StringReader(dataIn.readUTF())), Client.class);
                            c.setUuid(UUID.randomUUID().toString()); // génération de l'identifiant du client
                            this.server.registerClient(c);
                            dataOut.writeUTF(c.getUuid());
                            break;
                        case BYE:
                            System.out.println("Client " + this.c.getUuid() + " saying goodbye.");
                            dataIn.close();
                            interrupt = true;
                            break;
                        case GET_CLIENTS:
                            System.out.println("Client " + this.c.getUuid() + " asking for clients list.");
                            dataOut.writeUTF(new GsonBuilder().create().toJson(this.server.getClients()));
                            break;
                        default:
                            break;
                    }
                } else { // on communique avec un client (p2p)
                    input = dataIn.readUTF();
                    //System.out.println(input);

                    int count;
                    byte[] buffer = new byte[8192];

                    BufferedInputStream in = new BufferedInputStream(new FileInputStream("/tmp/vsfy/" + input));
                    while ((count = in.read(buffer)) > 0) {
                        dataOut.write(buffer, 0, count);
                    }
                    exchangeSocket.close();
                }
            } catch (IOException e) {
                //System.out.println("Lost connection with " + this.c.getUuid() + ", killing thread");
                try {
                    dataIn.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                //e.printStackTrace();
                interrupt = true;
            }
        }

    }

}
