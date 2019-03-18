package messaging.server;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class Server {
    public static final int DEFAULT_PORT = 8000; // default port number
    
    private int port;
    
    /**
     * Constructor
     * @param args command line arguments
     */
    Server(String[] args) {
        
        // assign/set port        
        if (args.length > 0) { // check command line argument for port (first argument)
            try {
                this.port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                System.exit(ErrorCode.PORT_ASSIGN_ERROR.getCode());
            }
        } else {
            this.port = DEFAULT_PORT;
        }
    }
    
    // --- PRIVATE METHODS ---
    /**
     * Handler for server start failure
    */
    private void HandleServerStartFailure (IOException ex) {
        
    }
    
    // --- PROTECTED METHODS ---
    abstract protected void initiate(ServerSocket socket);
    
    // --- PUBLIC METHODS ---
    
    /**
     * This method sets port number for the server
     * @param port server port number 
    */
    public void assignPort(int port) {
        this.port = port;
    }
    
    /**
     * This method starts the server
    */
    
    public void start() {
        try {
            ServerSocket server = new ServerSocket(this.port);
            initiate(server);
        } catch (IOException ex) {
            HandleServerStartFailure(ex);
            System.exit(ErrorCode.SERVER_START_ERROR.getCode());
        }
    }
    
}
