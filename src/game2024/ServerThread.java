package game2024;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread{
    Socket connSocket;

    public ServerThread(Socket connSocket) {
        this.connSocket = connSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());

            // Do the work and the communication with the client here
            // The following two lines are only an example
            String clientSentence = inFromClient.readLine();
            outToClient.writeBytes("ecco " + clientSentence + '\n' );

        } catch (IOException e) {
            e.printStackTrace();
        }
        // do the work here
    }
}
