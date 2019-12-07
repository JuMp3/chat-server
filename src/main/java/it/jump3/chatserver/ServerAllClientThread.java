package it.jump3.chatserver;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ServerAllClientThread extends Thread {

    private Socket socket;
    private String uuid;
    private ChatServer chatServer;
    private boolean isDebug;

    public ServerAllClientThread(Socket socket, String uuid, boolean isDebug, ChatServer chatServer) {
        setSocket(socket);
        setUuid(uuid);
        setChatServer(chatServer);
        setDebug(isDebug);
    }

    public ChatServer getChatServer() {
        return chatServer;
    }

    public void setChatServer(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    @Override
    public void run() {

        Response response = new Response();

        try {
            do {
                InputStream input = getSocket().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                // wait for client message
                response.setText(reader.readLine());
                // broadcast msg all clients
                broadcast(response);

            } while (!"bye".equalsIgnoreCase(response.getText()));

            System.out.println("Client disconnected " + getUuid());

            socket.close();
            // clear closed socket
            getChatServer().getSockets().keySet().removeIf(k -> k.equals(getUuid()));

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }

    // broadcast msg all clients
    private void broadcast(Response response) {

        System.out.println("Write on " + getChatServer().getSockets().size() + " clients");
        getChatServer().getSockets().forEach((k, socket) -> {

            try {
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                if ("bye".equalsIgnoreCase(response.getText())) {
                    if (!getUuid().equals(k)) {
                        writer.println(new Date().toString() + ": " + response.getText() + " from " + getUuid());
                    }
                } else {
                    writer.println(new Date().toString() + " | " + (isDebug() ? k : getUuid()) + ": " + response.getText());
                }
            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
            }
        });
    }

    private class Response {

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}