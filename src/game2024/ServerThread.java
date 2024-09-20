package game2024;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

            String command;

            while ((command = inFromClient.readLine()) != null) {
//                outToClient.writeBytes(command + "\n");
//                Skal man efter broadcast have en read eller sådan noget, så man venter på et svar, før man kan indføre bevægelsen?
                Server.broadcast(command);
//                TODO: Man kan selvfølgelig også implementere så der kun laves
//                 en bevægelse når der kommer en returbesked ud, dvs.
//                 playerMoved kaldes ikke når man trykker på piltasterne, men når man
//                 får en besked ind; man får selv beskeden når man kommer gennem sync køen

                System.out.println(command);
            }
            System.out.println("ded thread (left while)");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("deder thread (left try-catch)");
        // do the work here
//        TODO: kald en metode (som skal laves i Server) som fjerner en tråd fra thread-listen når den ryger herud og er ved at lukke.
        System.out.println(Server.removeThread(this) + " kiiiya!");
    }

    //    by Maintz
    public void listenForChanges(String command) {
        try {
            outToClient.writeBytes(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
