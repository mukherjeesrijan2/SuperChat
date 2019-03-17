package messaging;

import java.util.*;

import messaging.ChatState;


class Users implements Runnable {
    private Thread thread; // for this class
    private ArrayList<ChatUserThread> threads;
    private boolean check;

    Users () {
        this.threads = new ArrayList<ChatUserThread>();
        this.check = true;
    }

    private void update() {
        // check if user thread is alive
        // IF NOT: remove the thread from the list

        Iterator<ChatUserThread> itr = this.threads.iterator();

        while(itr.hasNext()) {
            ChatUserThread user = itr.next();

            if (!user.getThread().isAlive() || user.getState() == ChatState.UNHEALTHY) {
                System.out.println("Removing: " + user.getUsername());
                itr.remove();
            }
        }
    }

    public void addUser(ChatUserThread user) {
        this.threads.add(user);
    }

    public void stop() {
        this.check = false;
    }

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

    @Override
    public void run () {
        // update the user list (threads)
        while (check) {
            this.update();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}