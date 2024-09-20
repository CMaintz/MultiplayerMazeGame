package game2024;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
private static List<Player> players = new ArrayList<>();
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

            ServerThread serverThread = new ServerThread(connectionSocket, players, threads);
            serverThread.start();
            threads.add(serverThread);


            System.out.println("Ny klient forbundet.");

//            TODO indsæt den nye klients "player" i alle klienters liste over players.
//            broadcast("Hej hej\n");

        }

    }

//    by yours truly. TODO remove?
    private static void broadcast(String command) throws IOException {
        for (ServerThread st : threads) {
//            st.listenForChanges(command);
            st.outToClient.writeBytes(command);
        }
    }

}
