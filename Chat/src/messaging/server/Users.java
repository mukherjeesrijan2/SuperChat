package messaging.server;

import java.util.*;

class Users implements Runnable {
    private final ArrayList<ChatUserThread> threads;
    private final ArrayList<ChatUserThread> unauthorizedUsers;
    private Thread thread; // for this class
    private boolean check; // specifies if the tracking should continue or not
    
    /**
     * Constructor
     */
    Users () {
        this.threads = new ArrayList<>();
        this.unauthorizedUsers = new ArrayList<>();
        this.check = true;
    }
    
    /**
     * This method checks and removes if a user is not active (not connected to the server)
     */
    private void update() {
        // check if user thread is alive
        // IF NOT: remove the thread from the list

        Iterator<ChatUserThread> itr = this.threads.iterator();

        while(itr.hasNext()) {
            ChatUserThread user = itr.next();

            if (!user.getThread().isAlive()) {
                System.out.println("Removing: " + user.getUsername());
                itr.remove();
            }
        }
        
        // checks if user is authorized
        // IF: authorized add user to thread
        // else remove the user
        
        Iterator<ChatUserThread> itr_unauthorized = this.unauthorizedUsers.iterator();
        
        while (itr_unauthorized.hasNext()) {
            ChatUserThread user = itr_unauthorized.next();
            
            if (user.getAuthState() == AuthState.AUTHORIZED) {
                System.out.println("[AUTHORIZED] " + user.getUsername());
                this.threads.add(user);
                itr_unauthorized.remove();
            } else if (user.getAuthState() == AuthState.UNAUTHORIZED) {
                itr_unauthorized.remove();
                System.out.println("[UNAUTHORIZED] " + user.getUsername());
            }
        }
    }
    
    /**
     * This method adds a new user (if the user is authorized) to the active user list
     * @param user 
     */
    public void addUser(ChatUserThread user) {
        user.start();
        this.unauthorizedUsers.add(user);
    }
    
    /**
     * This method stops tracking (active/inactive) of users
     */
    public void stop() {
        this.check = false;
    }
    
    /**
     * This method starts tracking the users
     */
    public void start() {
        this.thread = new Thread(this);
        this.thread.start();
    }
    
    public void shout(ChatUserThread sender, String message) {
        Iterator<ChatUserThread> itr = this.threads.iterator();

        while(itr.hasNext()) {
            ChatUserThread user = itr.next();
            
            if (user != sender && user.getThread().isAlive()) {
                user.send(sender, message);
            }
        }
    }

    /**
     * Thread task
     */
    @Override
    public void run () {
        // update the user list (threads)
        while (check) {
            try {
                this.update();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}