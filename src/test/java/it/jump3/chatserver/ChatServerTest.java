package it.jump3.chatserver;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4.class)
public class ChatServerTest {

    private static final int PORT = 10000;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static ChatServer chatServer;
    private Client client1, client2, client3;
    private String response, responseToCheck;

    @BeforeClass
    public static void before() {
        // start server for all tests
        new Thread(() -> {
            chatServer = new ChatServer();
            chatServer.runServerForAllClient(PORT, true);
        }).start();
    }

    @Test
    public void A_testClientCall() throws IOException {
        Client client = new Client();
        client.startConnection("localhost", PORT);
        String text = generateString(10);
        response = client.sendMessage(text);
        System.out.println("client1: " + response);
        response = getOnlyGeneretedMsg(response);
        assertEquals(response, text);

        client.sendMessage("bye");
        chatServer.getSockets().clear();
    }

    @Test
    public void B_failClientCall() throws IOException {

        int wrongPort = 8000;

        try {
            Client client = new Client();
            client.startConnection("localhost", wrongPort);
        } catch (ConnectException e) {
            System.out.println("port 8000 wrong");
        }

        assertNotEquals(PORT, wrongPort);
    }

    @Test
    public void C_testBroadcast() throws IOException {

        client1 = new Client();
        client2 = new Client();
        client3 = new Client();

        client1.startConnection("localhost", PORT);
        client2.startConnection("localhost", PORT);
        client3.startConnection("localhost", PORT);

        response = client2.sendMessage(generateString(10));
        System.out.println("client2: " + response);
        response = getOnlyGeneretedMsg(response);

        responseToCheck = client1.readMsg();
        System.out.println("client1 " + responseToCheck);
        responseToCheck = getOnlyGeneretedMsg(responseToCheck);
        assertEquals(response, responseToCheck);

        responseToCheck = client3.readMsg();
        System.out.println("client3 " + responseToCheck);
        responseToCheck = getOnlyGeneretedMsg(responseToCheck);
        assertEquals(response, responseToCheck);

        client1.sendMessage("bye");
        client2.sendMessage("bye");
        client3.sendMessage("bye");
        chatServer.getSockets().clear();
    }

    @Test
    //Test multiple clients that send messages and log off, continuing to broadcast messages to all clients still connected
    public void D_testUseCase() throws IOException {

        client1 = new Client();
        client2 = new Client();
        client3 = new Client();

        System.out.println("*** stage 1 -> 1 client ***");
        client1.startConnection("localhost", PORT);
        response = client1.sendMessage(generateString(10));
        System.out.println("client1: " + response);
        System.out.println("*** stage 1 | end ***");


        System.out.println("*** stage 2 -> 2 clients ***");
        client2.startConnection("localhost", PORT);
        response = client2.sendMessage(generateString(10));
        System.out.println("client2: " + response);
        response = getOnlyGeneretedMsg(response);
        responseToCheck = client1.readMsg();
        System.out.println("client1 " + responseToCheck);
        responseToCheck = getOnlyGeneretedMsg(responseToCheck);
        assertEquals(response, responseToCheck);
        System.out.println("*** stage 2 | end ***");


        System.out.println("*** stage 3 -> 3 clients ***");
        client3.startConnection("localhost", PORT);
        response = client3.sendMessage(generateString(10));
        System.out.println("client3: " + response);
        response = getOnlyGeneretedMsg(response);
        responseToCheck = client1.readMsg();
        System.out.println("client1 " + responseToCheck);
        responseToCheck = getOnlyGeneretedMsg(responseToCheck);
        assertEquals(response, responseToCheck);
        responseToCheck = client2.readMsg();
        System.out.println("client2 " + responseToCheck);
        responseToCheck = getOnlyGeneretedMsg(responseToCheck);
        assertEquals(response, responseToCheck);
        System.out.println("*** stage 3 | end ***");


        System.out.println("*** stage 4 -> 3 clients random ***");
        response = client2.sendMessage(generateString(10));
        System.out.println("client2: " + response);
        response = getOnlyGeneretedMsg(response);
        responseToCheck = client1.readMsg();
        System.out.println("client1: " + responseToCheck);
        responseToCheck = getOnlyGeneretedMsg(responseToCheck);
        assertEquals(response, responseToCheck);
        responseToCheck = client3.readMsg();
        System.out.println("client3: " + responseToCheck);
        responseToCheck = getOnlyGeneretedMsg(responseToCheck);
        assertEquals(response, responseToCheck);
        System.out.println("*** stage 4 | end ***");


        System.out.println("*** stage 5 -> 2 active clients, 1 closed client ***");
        // close client1 socket
        client1.sendMessage("bye");
        // clear closed socket
        chatServer.getSockets().values().removeIf(Socket::isClosed);
        assertEquals(2, chatServer.getSockets().size());

        responseToCheck = client2.readMsg();
        System.out.println("client2: " + responseToCheck);
        responseToCheck = client3.readMsg();
        System.out.println("client3: " + responseToCheck);

        response = client3.sendMessage(generateString(10));
        System.out.println("client3: " + response);
        response = getOnlyGeneretedMsg(response);
        responseToCheck = client2.readMsg();
        System.out.println("client2: " + responseToCheck);
        responseToCheck = getOnlyGeneretedMsg(responseToCheck);
        assertEquals(response, responseToCheck);

        response = client2.sendMessage(generateString(10));
        System.out.println("client2: " + response);
        response = getOnlyGeneretedMsg(response);
        responseToCheck = client3.readMsg();
        System.out.println("client3: " + responseToCheck);
        responseToCheck = getOnlyGeneretedMsg(responseToCheck);
        assertEquals(response, responseToCheck);
        System.out.println("*** stage 5 | end ***");


        System.out.println("*** stage 6 -> 1 active client, 2 closed clients ***");
        // close client3 socket
        client3.sendMessage("bye");
        // clear closed socket
        chatServer.getSockets().values().removeIf(Socket::isClosed);
        assertEquals(1, chatServer.getSockets().size());
        response = client2.sendMessage(generateString(10));
        System.out.println("client2: " + response);
        System.out.println("*** stage 6 | end ***");


        System.out.println("*** stage 7 -> all closed clients ***");
        // close client2 socket
        client2.sendMessage("bye");
        // clear closed socket
        chatServer.getSockets().values().removeIf(Socket::isClosed);
        assertEquals(0, chatServer.getSockets().size());
        System.out.println("*** stage 7 | end ***");
    }

    private static String getOnlyGeneretedMsg(String response) {
        return response.substring(response.lastIndexOf(":") + 1).trim();
    }

    public static String generateString(int count) {

        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}
