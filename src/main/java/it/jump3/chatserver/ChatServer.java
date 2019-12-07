package it.jump3.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private Map<String, Socket> sockets = new ConcurrentHashMap<>();

    public Map<String, Socket> getSockets() {
        return sockets;
    }

    public void runServerForAllClient(int port, boolean isDebug) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            while (true) {

                Socket socket = serverSocket.accept();

                String uuid = getUUID();
                System.out.println("New client connected " + uuid);

                getSockets().put(uuid, socket);

                new ServerAllClientThread(socket, uuid, isDebug, this).start();
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }

    protected Socket createSocket() {
        return new Socket();
    }

    public void runServer(int port) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            while (true) {

                Socket socket = serverSocket.accept();

                String uuid = getUUID();
                System.out.println("New client connected " + uuid);

                new ServerThread(socket, uuid).start();
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }

    private String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
