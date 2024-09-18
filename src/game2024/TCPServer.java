package game2024;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    public static void main(String[] args) throws Exception {
        System.out.println("Venter på klient...");
        System.out.println("Lytter på port 6789...");

        ServerSocket welcomeSocket = new ServerSocket(6789);


        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            (new ServerThread(connectionSocket)).start();
            System.out.println("Ny klient forbundet.");

        }

    }


}
