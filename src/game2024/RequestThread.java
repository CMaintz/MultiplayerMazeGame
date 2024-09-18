package game2024;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RequestThread extends Thread{
private BufferedReader inFromPlayer;
private Socket conSocket;
public RequestThread(Socket conSocket) throws IOException {
    this.conSocket = conSocket;
    this.inFromPlayer = new BufferedReader(new InputStreamReader(conSocket.getInputStream()));
}
    @Override
    public void run() {

    }
}
