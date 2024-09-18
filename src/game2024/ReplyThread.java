package game2024;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReplyThread extends Thread{
    private DataOutputStream outToPlayer;
    private BufferedReader inFromServer; //Ved ikke om skal bruges
    private Socket conSocket;
    public ReplyThread(Socket conSocket) throws IOException {
        this.conSocket = conSocket;
        this.outToPlayer = new DataOutputStream(conSocket.getOutputStream());
        this.inFromServer = new BufferedReader(new InputStreamReader(System.in));
    }
    @Override
    public void run() {

    }

}
