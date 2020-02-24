package main.server;

public class Server {

    private String ip;

    public Server(String _ip) {
        ip = _ip;
        System.out.println("Server will use ip " + ip);
    }
}
