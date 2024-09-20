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
//        this.inFromClient = inFromClient;
        this.inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
        outToClient = new DataOutputStream(connSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {


            // Do the work and the communication with the client here
//            String playerName = inFromClient.readLine();
//            player = new Player(playerName, 9, 4, "up");
//            players.add(player);

//            updateClientWithPlayerInfo();

            String command;
//            while ((command = inFromClient.readLine() != null)) {
//                processCommand(command);
//                broadcastGameState();
//            }

            while ((command = inFromClient.readLine()) != null) {
//                outToClient.writeBytes(command + "\n");
                Server.broadcast(command);
            }
//            Der skal nok laves en randomize position dimsedut eller sådan noget, men indtil videre er det lige sådan her vi gør.
//            Og så skal man vel have en liste over gyldige og ubrugte placeringer. Evt. kan man bare tage placering[0], 1, 2, 3, 4 osv
        } catch (IOException e) {
            e.printStackTrace();
        }
        // do the work here
    }

    //    by Maintz, TODO: Kill this? idk
    public void listenForChanges(String command) {
        try {
            outToClient.writeBytes(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
