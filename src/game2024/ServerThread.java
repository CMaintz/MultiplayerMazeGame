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
        outToClient = new DataOutputStream(connSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            // Do the work and the communication with the client here
            String sentence;
            while (!connSocket.isClosed()) {
                if ((sentence = inFromClient.readLine()) != null) {
                    Server.broadcast(sentence);
                }
            }
            Server.removeClient(this);
            inFromClient.close();
            outToClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
