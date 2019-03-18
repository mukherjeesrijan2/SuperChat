package messaging.server;

import java.net.ServerSocket;

public class ChatServer extends Server {
    private String serverAddress;
    private Users users;
    
    /**
     * Constructor
     * @param args 
     */
    ChatServer(String args[]) {
        super(args);
    }
    
    /**
     * This methods performs the tasks after the server is created and started
     * @param server (SocketServer)
     */
    @Override
    protected void initiate(ServerSocket server) {
        this.serverAddress = server.getLocalSocketAddress().toString();
        this.users = new Users();
        
        // Create new access point
        AccessPoint ap = new AccessPoint(server, this.users);
        ap.start(); // start listening to users
        
        System.out.println("Server started at " + this.serverAddress);
    }
}
