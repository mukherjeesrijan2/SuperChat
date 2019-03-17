package messaging;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    public static void main(String[] args) throws IOException {
        // Assign port
        int port = 8000;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port");
                System.exit(100);
            }
        }
        
        Users users = new Users(); // keeps track of users

        // Establish a socket
        try (ServerSocket s = new ServerSocket(port)) {
            String address = s.getLocalSocketAddress().toString();
            System.out.println("Server started at : " + address);
            
            users.start();

            int i = 1;
            while (true) {
                Socket incoming = s.accept();
                String incomingFrom = incoming.getLocalSocketAddress().toString();

                System.out.println("Spawing :" + i + " | " + incomingFrom);

                users.addUser(new ChatUserThread(incoming, users));
                
                i++;
            }

        } catch (Exception e) { // SocketServer
            e.printStackTrace();
        }

        users.stop();
    }
}