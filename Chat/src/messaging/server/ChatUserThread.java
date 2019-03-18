package messaging.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

class ChatUserThread implements Runnable {
    private final Socket incoming;
    private final Users users;
    private Thread thread;
    private Scanner in;
    private PrintWriter out;

    private String username;


    ChatUserThread(Socket incoming, Users users) {
        this.incoming = incoming;
        this.users = users;
        this.username = UTILITY.getAlphaNumericString(10); // temporary
        
        try {
            this.in = new Scanner(this.incoming.getInputStream(), 
                    StandardCharsets.UTF_8.toString());
            
            this.out = new PrintWriter(
                new OutputStreamWriter(this.incoming.getOutputStream(),
                        StandardCharsets.UTF_8.toString()),
                true /* auto flush */);
        } catch (Exception ex) {
            
        }
        
    }
    
    // --- PRIVATE METHODS ---
    
    // --- PUBLIC METHODS ---
    public void start() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void send(ChatUserThread sender, String message) {
        try {

        this.out.printf("\n\r[%s]: %s", sender.getUsername(), message);
        this.out.flush();
        this.out.printf("\n\r[%s]: ", this.getUsername());
        this.out.flush();
        } catch (Exception e) {e.printStackTrace();}
    }

    public Thread getThread() {
        return this.thread;
    }

    public String getUsername() {
        return this.username;
    }

    private void setUsername(String username) {
        this.username = username;
    }
        
    @Override
    public void run() {
        try {
            this.out.printf("\n\rHello! Enter 'shutdown' to exit.");
            this.out.flush();
            this.out.printf("\n\r[%s]: ", username);
            this.out.flush();
            while (this.in.hasNextLine()) {
                String message = this.in.nextLine();
                if (message.trim().toLowerCase().equals("shutdown")) {
                    break;
                }
                this.users.shout(this, message);
                
                this.out.printf("\r[%s]: ", this.getUsername());
                this.out.flush();

            }

            this.in.close();
            this.out.close();
        } catch (Exception e) {e.printStackTrace();}
    }
}