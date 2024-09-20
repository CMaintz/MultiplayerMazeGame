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
private static List<Socket> sockets = new ArrayList<>();
private static List<ServerThread> threads = new ArrayList<>();

//Send players og / eller sockets med ind i tråden?
    public static void main(String[] args) throws Exception {
        int port = 6789;


        ServerSocket welcomeSocket = new ServerSocket(port);

        System.out.println("Venter på klient...");
        System.out.println("Lytter på port " + port);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            sockets.add(connectionSocket);

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            String connectionInfo = inFromClient.readLine();

//            loop gennem alle tråde, send dem en besked?


//            ServerThread serverThread = new ServerThread(connectionSocket, inFromClient);
            ServerThread serverThread = new ServerThread(connectionSocket, null);
            serverThread.start();
            threads.add(serverThread);

            broadcast(connectionInfo);
//            broadcast("hej hej");


            System.out.println("Ny klient forbundet.");

//            TODO indsæt den nye klients "player" i alle klienters liste over players.
//            broadcast("Hej hej\n");

        }

    }

//    by yours truly.
    public static void broadcast(String command) throws IOException {
        for (ServerThread st : threads) {
            st.listenForChanges(command);
        }
    }

}
