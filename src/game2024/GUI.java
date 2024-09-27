package game2024;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GUI extends Application {
    private static Socket clientSocket;
    private static BufferedReader inFromServer;
    private static DataOutputStream outToServer;

    public static final int size = 20;
    public static final int scene_height = size * 20 + 100;
    public static final int scene_width = size * 20 + 200;

    public static Image image_floor;
    public static Image image_wall;
    public static Image hero_right, hero_left, hero_up, hero_down;

    private static String myName = "";
    public static Player me;
    public static Map<String, Player> playerMap = new HashMap<>();
    private static String[] spawnPoints = {"1 1", "17 1", "4 14", "16 17", "11 10", "5 7"};

    private int connectedClients = 0;

    private Label[][] fields;
    private TextArea scoreList;

    private String[] board = {    // 20x20
            "wwwwwwwwwwwwwwwwwwww",
            "w        ww        w",
            "w w  w  www w  w  ww",
            "w w  w   ww w  w  ww",
            "w  w               w",
            "w w w w w w w  w  ww",
            "w w     www w  w  ww",
            "w w     w w w  w  ww",
            "w   w w  w  w  w   w",
            "w     w  w  w  w   w",
            "w ww ww        w  ww",
            "w  w w    w    w  ww",
            "w        ww w  w  ww",
            "w         w w  w  ww",
            "w        w     w  ww",
            "w  w              ww",
            "w  w www  w w  ww ww",
            "w w      ww w     ww",
            "w   w   ww  w      w",
            "wwwwwwwwwwwwwwwwwwww"
    };


    // -------------------------------------------
    // | Maze: (0,0)              | Score: (1,0) |
    // |-----------------------------------------|
    // | boardGrid (0,1)          | scorelist    |
    // |                          | (1,1)        |
    // -------------------------------------------

    @Override
    public void start(Stage primaryStage) {
        try {
//           TODO: tilføj til inputDialog en mulighed for at indtaste serverens IP?

            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Indtast dit navn");
            nameDialog.setHeaderText("Velkommen til spillet!");
            nameDialog.setContentText("Indtast dit navn:");

//          Venter på brugerens input med showAndWait()
            nameDialog.showAndWait();
            myName = nameDialog.getResult();

            clientSocket = new Socket("localhost", 6789);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(0, 10, 0, 10));

            Text mazeLabel = new Text("Maze:");
            mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            Text scoreLabel = new Text("Score:");
            scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            scoreList = new TextArea();

            GridPane boardGrid = new GridPane();

            image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
            image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

            hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
            hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
            hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
            hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);

            GuiThread gt = new GuiThread();
            gt.start();

            fields = new Label[20][20];
            for (int j = 0; j < 20; j++) {
                for (int i = 0; i < 20; i++) {
                    switch (board[j].charAt(i)) {
                        case 'w':
                            fields[i][j] = new Label("", new ImageView(image_wall));
                            break;
                        case ' ':
                            fields[i][j] = new Label("", new ImageView(image_floor));
                            break;
                        default:
                            throw new Exception("Illegal field value: " + board[j].charAt(i));
                    }
                    boardGrid.add(fields[i][j], i, j);
                }
            }
            scoreList.setEditable(false);


            grid.add(mazeLabel, 0, 0);
            grid.add(scoreLabel, 1, 0);
            grid.add(boardGrid, 0, 1);
            grid.add(scoreList, 1, 1);

            Scene scene = new Scene(grid, scene_width, scene_height);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Setting up standard players

            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                try {
                    switch (event.getCode()) {
                        case UP:
                            sendMoveCommand("0 -1 up");
                            break;
                        case DOWN:
                            sendMoveCommand("0 +1 down");
                            break;
                        case LEFT:
                            sendMoveCommand("-1 0 left");
                            break;
                        case RIGHT:
                            sendMoveCommand("+1 0 right");
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            outToServer.writeBytes("CONNECT " + myName + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playerMoved(int delta_x, int delta_y, String direction, Player player) {
        player.direction = direction;
        int x = player.getXpos(), y = player.getYpos();

        if (board[y + delta_y].charAt(x + delta_x) == 'w') {
            player.addPoints(-1);
        } else {
            Player p = getPlayerAt(x + delta_x, y + delta_y);
            if (p != null) {
                player.addPoints(10);
                p.addPoints(-10);
            } else {
                player.addPoints(1);

                fields[x][y].setGraphic(new ImageView(image_floor));
                x += delta_x;
                y += delta_y;

                if (direction.equals("right")) {
                    fields[x][y].setGraphic(new ImageView(hero_right));
                }
                ;
                if (direction.equals("left")) {
                    fields[x][y].setGraphic(new ImageView(hero_left));
                }
                ;
                if (direction.equals("up")) {
                    fields[x][y].setGraphic(new ImageView(hero_up));
                }
                ;
                if (direction.equals("down")) {
                    fields[x][y].setGraphic(new ImageView(hero_down));
                }
                ;

                player.setXpos(x);
                player.setYpos(y);
            }
        }
        System.out.println("X-pos:" + player.getXpos() + " Y-pos: " + player.getYpos());
        scoreList.setText(getScoreList());
    }

    public String getScoreList() {
        StringBuffer b = new StringBuffer(100);
        for (Player p : playerMap.values()) {
            b.append(p + "\r\n");
        }
        return b.toString();
    }

    //Siger hvor spillere er
    public Player getPlayerAt(int x, int y) {
        for (Player p : playerMap.values()) {
            if (p.getXpos() == x && p.getYpos() == y) {
                return p;
            }
        }
        return null;
    }

    private void handleClosing() {

    }
    public void createAndRegisterSelf() {
        if (me == null) {
            // Vælg spawn point fra spawnPoints array
            //TODO: en getSpawnPoint metode? Eller assignspawnpoint?
            int spawnIndex = connectedClients - 1;
            String[] spawnPoint = spawnPoints[(spawnIndex) % spawnPoints.length].split(" ");
            int spawnX = Integer.parseInt(spawnPoint[0]);
            int spawnY = Integer.parseInt(spawnPoint[1]);
            while (getPlayerAt(spawnX, spawnY) != null) {
                spawnIndex++;
                spawnPoint = spawnPoints[(connectedClients) % spawnPoints.length].split(" ");
                spawnX = Integer.parseInt(spawnPoint[0]);
                spawnY = Integer.parseInt(spawnPoint[1]);
            }
            // Opretter egen spiller
            createPlayer(myName, spawnX, spawnY, "up", 0);
            System.out.println("Spawn X: " + spawnX + " Spawn Y:" + spawnY);
        }
    }

    public void createPlayer(String name, int xpos, int ypos, String direction, int points) {
        System.out.println("create player");

        if (playerMap.get(name) == null) {
            if (getPlayerAt(xpos, ypos) != null) {
                System.out.println("Der står allerede en player!");
            } else {
                Player newPlayer = new Player(name, xpos, ypos, direction);
                playerMap.put(name, newPlayer);
                fields[xpos][ypos].setGraphic(new ImageView(hero_up));
                newPlayer.setPoint(points);
                scoreList.setText(getScoreList());

                if (name.equalsIgnoreCase(myName)) {
                    me = newPlayer;
                    try {
                        sendPlayerState();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Player created: " + newPlayer.getXpos() + " " + newPlayer.getYpos()); //TODO fjern SOUT
            }
        }
    }

    //	new method by yours truly
    public void sendMoveCommand(String input) throws IOException {
        outToServer.writeBytes("MOVE " + me.getName() + " " + input + "\n");
    }

    private void sendPlayerState() throws IOException {
        System.out.println("sendplayerstate");
        if (me != null) {
            outToServer.writeBytes("REGISTER " + me.getState() + "\n");
            System.out.println("SendPlayerState success"); //TODO remove SOUT
        }
    }

    //    GuiThread is a simple message-reader thread.
    private class GuiThread extends Thread {
        private GuiThread() {
        }

        //Runs when the GUI has finished compiling
        @Override
        public void run() {

            String inboundMessage;
            try {
                while ((inboundMessage = inFromServer.readLine()) != null) {

                    System.out.println("Inbound message: " + inboundMessage);
                    String[] tokens = inboundMessage.split(" ");
                    String command = tokens[0];

                    if (command.equalsIgnoreCase("MOVE")) {
                        System.out.println("Inde i move: " + inboundMessage); //TODO: fjern sout
                        handleMoveCommand(tokens);
                    } else if (command.equalsIgnoreCase("REGISTER")) {
//                            int x_coord = Integer.parseInt(tokens[2]);
//                            int y_coord = Integer.parseInt(tokens[3]);
//                            String direction = tokens[4];
//
////                            createPlayer(name, x_coord, y_coord, direction);
//                            System.out.println("Player created: " + createPlayer(name, x_coord, y_coord, direction));
//
//                            int connectedClients = Integer.parseInt(tokens[5]);
//                            if (playerMap.size() == (connectedClients - 1)) {
////                            create player me
//                                sendPlayerState();
//                            }
                        handleRegisterCommand(tokens);
                    } else if (command.equalsIgnoreCase("CONNECT")) {
                        handleConnectCommand(tokens);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void handleMoveCommand(String[] tokens) {
            String name = tokens[1];
            int delta_x = Integer.parseInt(tokens[2]);
            int delta_y = Integer.parseInt(tokens[3]);
            String direction = tokens[4];

            Platform.runLater(() -> {
                playerMoved(delta_x, delta_y, direction, playerMap.get(name));
            });
        }

        private void handleRegisterCommand(String[] tokens) {
            String name = tokens[1];
            if (!name.equalsIgnoreCase(myName)) {
                int x_coord = Integer.parseInt(tokens[2]);
                int y_coord = Integer.parseInt(tokens[3]);
                String direction = tokens[4];
                int points = Integer.parseInt(tokens[5]);
                System.out.println("x_coord: " + x_coord);
                System.out.println("y_coord: " + y_coord);
                Platform.runLater(() -> createPlayer(name, x_coord, y_coord, direction, points)
                );
                System.out.println("Platform runLater thingie");

            }
//            if (me == null) { //TODO: fjern hele nedenstående, så det kun er i handleConnectCommand der kaldes createAndReg?
//                System.out.println("Me == null i handleRegisterCommand");
//                Platform.runLater(() -> createAndRegisterSelf());
//                System.out.println("Efter en run-later i handleRegisterCommand");
//            }

        }
    }


    private void handleConnectCommand(String[] tokens) throws IOException {
        System.out.println("handle connect command");

        String name = tokens[1];
        int clientCount = Integer.parseInt(tokens[2]);
        connectedClients = clientCount;
        if (name.equalsIgnoreCase(myName)) {
            Platform.runLater(() -> createAndRegisterSelf());
        } else {
            sendPlayerState();
        }
//            if (clientCount > connectedClients) {
//                System.out.println("clientCount > connectedClients");
//                System.out.println("clientCount: " + clientCount);
//                System.out.println("connectedClients: " + connectedClients);
//                connectedClients = clientCount;
//            }
//            System.out.println("Connected clients: " + connectedClients);
//            System.out.println("Name equals myName: " + name.equalsIgnoreCase(myName));
//            if (name.equalsIgnoreCase(myName) && (connectedClients - 1) == playerMap.size()) {
//                // Min egen CONNECT besked modtaget – nu kan vi oprette spilleren
//                System.out.println("name = myName, connectedClients -1 = map.size");
//                String[] spawnPoint = spawnPoints[playerMap.size()].split(" ");
//                int spawnX = Integer.parseInt(spawnPoint[0]);
//                int spawnY = Integer.parseInt(spawnPoint[1]);
//                System.out.println("There's a player here!");
//                System.out.println(playerMap.size());
//                System.out.println(spawnPoints[playerMap.size() + 1]);
//                spawnX = Integer.parseInt(spawnPoint[0]);
//                System.out.println(spawnX);
//                spawnY = Integer.parseInt(spawnPoint[1]);
//                System.out.println(spawnY);
//                if (getPlayerAt(spawnX, spawnY) == null) {
//                    System.out.println("runLater i handleConnectCommand");
//                    createAndRegisterSelf();
//                }
//            } else {
//                // Opretter ikke spilleren endnu, vi venter på en REGISTER kommando
//                System.out.println("Send player state in handleConnectCommand");
//                sendPlayerState();
//            }
    }


}


