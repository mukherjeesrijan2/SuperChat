package messaging;

import java.io.*;
import java.net.Socket;
import java.nio.*;
import java.util.*;

import messaging.ChatState;


class ChatUserThread implements Runnable {
    private Socket incoming;
    private Users users;
    private Thread thread;
    private ChatState state;
    private Scanner in;
    private PrintWriter out;

    private String username;


    ChatUserThread(Socket incoming, Users users) {
        this.incoming = incoming;

        try (InputStream inputStream = this.incoming.getInputStream();
            OutputStream outputStream = this.incoming.getOutputStream()) {
                this.in = new Scanner(inputStream, "UTF-8");
                this.out = new PrintWriter(
                    new OutputStreamWriter(outputStream, "UTF-8"),
                    true /* auto flush */
                );
        } catch (Exception e) {
            System.out.println("----> Unexpected error: User cannot be initialized!");
            this.state = ChatState.UNHEALTHY;
        }

        System.out.println(":User initialized!");

        this.state = ChatState.HEALTHY;

        this.users = users;
        
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void send(ChatUserThread sender, String message) {
        this.out.println("[" + sender.getUsername() + "] : " + message);
    }

    public Thread getThread() {
        return this.thread;
    }

    public String getUsername() {
        return this.username;
    }

    public ChatState getState() {
        return this.state;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void run() {
        try {
            this.out.println("Hello! Enter 'shutdown' to exit");

            // // set username
            if (getUsername() == null || getUsername().isEmpty()) {
                boolean usernameComplete = false;
                
                this.out.print("\nUsername: ");

                while (!usernameComplete && this.in.hasNextLine()) {
                    String name = this.in.nextLine();
                    if (!name.trim().isEmpty()) {
                        setUsername(name);
                        usernameComplete = true;
                    } else {
                        this.out.print("\nUsername: ");
                    }
                }
            }

            this.out.print("\n[" + username + "] : ");
            while (this.in.hasNextLine()) {
                String message = this.in.nextLine();
                if (message.trim().toLowerCase().equals("shutdown")) {
                    break;
                }
                // this.users.shout(this, message);
                this.out.print("\n[" + username + "] : ");
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    
}