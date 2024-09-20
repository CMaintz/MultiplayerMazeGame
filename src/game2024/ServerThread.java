package game2024;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerThread extends Thread{
    Socket connSocket;
    List<Player> players;
    List<ServerThread> threads;
    Player player;
    DataOutputStream outToClient;


    public ServerThread(Socket connSocket, List<Player> players, List<ServerThread> threads) {
        this.connSocket = connSocket;
        this.threads = threads;
//        this.players = players;
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
            outToClient = new DataOutputStream(connSocket.getOutputStream());

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
//                while (true) {
//                listenForChanges(command);
                //Ekko

//                String[] splitterino = command.split(" ");
//                if (splitterino[3].equalsIgnoreCase("up")) {
//                    nuttin
//                } else {
                    outToClient.writeBytes("ekko: " + command + "\n");
                    outToClient.writeBytes("Hæhæhæ du er dum\n");
                    outToClient.writeBytes("Hæhæhæ du er klog\n");
                    outToClient.writeBytes("Hæhæhæ du er flot\n");
                    outToClient.writeBytes("Hæhæhæ du er grim\n");
//                }
            }
//            Der skal nok laves en randomize position dimsedut eller sådan noget, men indtil videre er det lige sådan her vi gør.
//            Og så skal man vel have en liste over gyldige og ubrugte placeringer. Evt. kan man bare tage placering[0], 1, 2, 3, 4 osv
        } catch (IOException e) {
            e.printStackTrace();
        }
        // do the work here
    }


    private void processCommand(String command) {
        // Simpel kommando for bevægelser (eks: "MOVE UP", "MOVE DOWN")
        String[] parts = command.split(" ");
        if (parts[0].equals("MOVE")) {
            switch (parts[1]) {
                case "UP": player.setYpos(player.getYpos() - 1); player.setDirection("up"); break;
                case "DOWN": player.setYpos(player.getYpos() + 1); player.setDirection("down"); break;
                case "LEFT": player.setXpos(player.getXpos() - 1); player.setDirection("left"); break;
                case "RIGHT": player.setXpos(player.getXpos() + 1); player.setDirection("right"); break;
            }
        }
    }

    private void updateClientWithPlayerInfo() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Player p : players) {
            sb.append(p.name).append(" ").append(p.getXpos()).append(" ").append(p.getYpos()).append(" ").append(p.getDirection()).append("\n");
        }
//        outToClient.writeBytes(sb.toString());
    }

    private void broadcastGameState() {
        // Send alle spilleres positioner til alle klienter
        for (Player p : players) {
            try {
                outToClient.writeBytes("PLAYER " + p.name + " " + p.getXpos() + " " + p.getYpos() + " " + p.getDirection() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void listenForChanges(String command)  {
        try {
            outToClient.writeBytes(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
