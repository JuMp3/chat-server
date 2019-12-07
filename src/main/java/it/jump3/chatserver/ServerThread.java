package it.jump3.chatserver;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ServerThread extends Thread {

    private Socket socket;
    private String uuid;

    public ServerThread(Socket socket, String uuid) {
        setSocket(socket);
        setUuid(uuid);
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

    @Override
    public void run() {

        try {

            String text;

            do {
                InputStream input = getSocket().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = getSocket().getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                text = reader.readLine();
                writer.println(new Date().toString() + ": " + text);
            } while (!"bye".equalsIgnoreCase(text));

            System.out.println("Client disconnected " + getUuid());
            getSocket().close();

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}