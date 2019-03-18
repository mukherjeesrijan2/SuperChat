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
    private long userid;
    private String password;
    private AuthState authState;
    
    private int passwordTries;


    ChatUserThread(Socket incoming, Users users) {
        this.incoming = incoming;
        this.users = users;
        this.username = UTILITY.getAlphaNumericString(10); // temporary
        this.authState = AuthState.PENDING;
        this.passwordTries = 3;
        
        try {
            this.in = new Scanner(this.incoming.getInputStream(), 
                    StandardCharsets.UTF_8.toString());
            
            this.out = new PrintWriter(
                new OutputStreamWriter(this.incoming.getOutputStream(),
                        StandardCharsets.UTF_8.toString()),
                true /* auto flush */);
        } catch (Exception ex) {
            // do something
        } 
    }
    
    // --- PRIVATE METHODS ---
    /**
     * This method changes/sets the username
     * @param username 
     */
    private void setUsername(String username) {
        this.username = username;
    }
    
    // --- PUBLIC METHODS ---
    public void start() {
        if ((username != null || !username.isEmpty())) {
            this.thread = new Thread(this);
            this.thread.start();
        } else {
            System.out.println("[ERROR]: Initialize user!");
        }
    }

    public void send(ChatUserThread sender, String message) {
        try {
            this.out.printf("\n\r[%s]: %s", sender.getUsername(), message);
            this.out.flush();
            this.out.printf("\n\r[%s]: ", this.getUsername());
            this.out.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Thread getThread() {
        return this.thread;
    }

    public String getUsername() {
        return this.username;
    }
    
    public AuthState getAuthState() {
        return this.authState;
    }
        
    @Override
    public void run() {
        try {
            this.out.printf("\n\rHello! Enter 'shutdown' to exit.\n\r");
            this.out.flush();
            
            // get password and authorize
            this.out.printf("\n\rPassword: ");
            this.out.flush();
            while (this.authState != AuthState.AUTHORIZED && this.passwordTries > 0) {
                this.passwordTries--;
                this.password = this.in.nextLine();
                this.authState = Authorizer.authorize(this.username, this.password) ? 
                                AuthState.AUTHORIZED : AuthState.UNAUTHORIZED;

                if (this.authState == AuthState.UNAUTHORIZED && this.passwordTries > 0) {
                    this.out.printf("\n\r[INVALID PASSWORD] Password: ");
                    this.out.flush();

                    this.authState = AuthState.PENDING;
                }
                
                
            }
            
            if (this.authState == AuthState.AUTHORIZED) {
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
            }
            
            this.in.close();
            this.out.close();
        } catch (Exception e) {e.printStackTrace();}
    }
}