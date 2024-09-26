package game2024;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {
    Socket connSocket;
    DataOutputStream outToClient;
    BufferedReader inFromClient;


    public ServerThread(Socket connSocket, BufferedReader inFromClient) throws IOException {
        this.connSocket = connSocket;
        this.inFromClient = inFromClient;
//        this.inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
        outToClient = new DataOutputStream(connSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            // Do the work and the communication with the client here
            String sentence;
            while ((sentence = inFromClient.readLine()) != null) {
//                if (sentence.split(" ")[0].equalsIgnoreCase("CONNECT")) {
//
//                }
                Server.broadcast(sentence);
            }
            System.out.println("ded thread (left while)");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("deder thread (left try-catch)");
        // do the work here
//        TODO: kald en metode (som skal laves i Server) som fjerner en tråd fra thread-listen når den ryger herud og er ved at lukke.
        Server.removeThread(this);
    }

    //    by Maintz
    public void listenForChanges(String command) {
        try {
            System.out.println("Listen for changes: " + command);
            outToClient.writeBytes(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
