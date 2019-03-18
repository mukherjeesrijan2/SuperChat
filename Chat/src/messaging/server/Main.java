package messaging.server;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer(args); // create new chat server
        server.start(); // start the chat server
    }
}