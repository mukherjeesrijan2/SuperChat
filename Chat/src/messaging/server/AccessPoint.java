package messaging.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class AccessPoint implements Runnable {
    private final Users users;
    private final ServerSocket server;
    private Thread apThread;
    private int totalUsers;
    
    /**
     * Constructor
     * @param server
     * @param users 
     */
    AccessPoint(ServerSocket server, Users users) {
        this.server = server;
        this.users = users;
        this.totalUsers = 0;
    }
    
    /**
     * This method creates and starts a thread for the AccessPoint
     */
    public void start() {
        apThread = new Thread(this, this.getClass().getSimpleName());
        apThread.start();
    }
    
    /**
     * Task for the thread
     */
    @Override
    public void run() {
        users.start(); // start tracking users
        
        try {
            while (true) {
                try {
                    Socket incoming = this.server.accept(); // listen for new user connection
                    ++this.totalUsers;
                
                    System.out.println("User -> " + this.totalUsers + 
                            " -> [REQUEST]: " + incoming.getLocalSocketAddress().toString());
                
                    this.users.addUser(new ChatUserThread(incoming, users)); // create a new thread for the user and add it to users for tracking
                } catch (IOException ex) {
                    System.err.println("Could not process imcoming request: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("Shutting down server: " + ex.getMessage());
        }
        
        try {
            this.server.close(); // shutdown server
            users.stop(); // stop tracking
        } catch (IOException ex) {
            System.err.println("An exception occured while closing server : " + 
                    ex.getMessage());
        }
    }
}
