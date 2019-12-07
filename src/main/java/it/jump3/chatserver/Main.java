package it.jump3.chatserver;

public class Main {

    public static void main(String[] args) {

        ChatServer chatServer = new ChatServer();
        chatServer.runServerForAllClient(10000, false);
    }
}
