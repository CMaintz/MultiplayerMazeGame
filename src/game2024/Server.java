package game2024;

import javax.sound.sampled.Port;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
private static List<ServerThread> threads = new ArrayList<>();

//Send players og / eller sockets med ind i tråden?
    public static void main(String[] args) throws Exception {
        int port = 6789;

        ServerSocket welcomeSocket = new ServerSocket(port);

        System.out.println("Venter på klient...");
        System.out.println("Lytter på port " + port);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("Three-way handshake completed.");

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            String connectionInfo = inFromClient.readLine();

            ServerThread serverThread = new ServerThread(connectionSocket, inFromClient);

            serverThread.start();
            threads.add(serverThread);
// TODO: if threads.size == 1, altså det er kun dig der er connected, så create din spiller (dvs. REGISTER)
//  ellers, send din spillers info, dvs. CONNECT.

            connectionInfo += " " + threads.size();
            System.out.println(connectionInfo);
            broadcast(connectionInfo);


            System.out.println("Ny klient forbundet.");

//            TODO indsæt den nye klients "player" i alle klienters liste over players.
//            broadcast("Hej hej\n");

        }

    }

//    I did a thing!
    public synchronized static void  broadcast(String command) throws IOException {
        for (ServerThread st : threads) {
            st.listenForChanges(command);
        }
    }

    public static boolean removeThread(ServerThread thread) {
        boolean toReturn;
        if (toReturn = threads.contains(thread)) {
            System.out.println(threads.size());
            threads.remove(thread);
            System.out.println(threads.size());
        }
        return toReturn;
    }

}
