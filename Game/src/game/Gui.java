package game;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

public class Gui extends Application {

	public static final int size = 30; 
	public static final int scene_height = size * 20 + 50;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;

	private Socket socket;

	private static Label[][] fields;
	private TextArea scoreList;

	//Connection to server
	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {


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

			fields = new Label[20][20];
			for (int j = 0; j < 20; j++) {
				for (int i = 0; i < 20; i++) {
					switch (Generel.board[j].charAt(i)) {
						case 'w':
							fields[i][j] = new Label("", new ImageView(image_wall));
							break;
						case ' ':
							fields[i][j] = new Label("", new ImageView(image_floor));
							break;
						default:
							throw new Exception("Illegal field value: " + Generel.board[j].charAt(i));
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

			//Connecting to server
			socket = new Socket("localhost", 7000);
			ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
			Packet firstPacket = new Packet(App.me, App.name);
			outToServer.writeObject(firstPacket);

			//Reciving the gamestate from the server
			ReciverThread reciverThread = new ReciverThread();
			reciverThread.start();

			//Taking a keypress and sending it to the server in a package
			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				String keypress = "";
				switch (event.getCode()) {
					case UP:
						keypress = "up";
						break;
					case DOWN:
						keypress = "down";
						break;
					case LEFT:
						keypress = "left";
						break;
					case RIGHT:
						keypress = "right";
						break;
					case ESCAPE:
						keypress = "exit";
					default:
						break;
				}
				try {
					//Create packet and send it to server
					Packet packet = new Packet(App. me, keypress);
					outToServer.writeObject(packet);

					if (keypress.equals("exit")) {
						reciverThread.interrupt();
						System.exit(0);
					}
				} catch (Exception e) {
					System.out.println("Error in sending packet");
					//System.out.println(e.getMessage());
				}
			});


			// Putting default players on screen
			for (int i = 0; i < GameLogic.players.size(); i++) {
				fields[GameLogic.players.get(i).getXpos()][GameLogic.players.get(i).getYpos()].setGraphic(new ImageView(hero_up));
			}
			scoreList.setText(getScoreList(GameLogic.players));



		} catch(Exception e){
				e.printStackTrace();
			}
		}

	public static void removePlayerOnScreen(pair oldpos) {
		Platform.runLater(() -> {
			fields[oldpos.getX()][oldpos.getY()].setGraphic(new ImageView(image_floor));
			});
	}
	
	public static void placePlayerOnScreen(pair newpos,String direction) {
		Platform.runLater(() -> {
			int newx = newpos.getX();
			int newy = newpos.getY();
			if (direction.equals("right")) {
				fields[newx][newy].setGraphic(new ImageView(hero_right));
			};
			if (direction.equals("left")) {
				fields[newx][newy].setGraphic(new ImageView(hero_left));
			};
			if (direction.equals("up")) {
				fields[newx][newy].setGraphic(new ImageView(hero_up));
			};
			if (direction.equals("down")) {
				fields[newx][newy].setGraphic(new ImageView(hero_down));
			};
			});
	}
	
	public static void movePlayerOnScreen(pair oldpos,pair newpos,String direction)
	{
		if (!oldpos.equals(null)) removePlayerOnScreen(oldpos);
		placePlayerOnScreen(newpos,direction);
	}
	

	
	public void updateScoreTable(List<Player> players)
	{
		Platform.runLater(() -> {
			scoreList.setText(getScoreList(players));
			});
	}
	public void playerMoved(Player player, int delta_x, int delta_y, String direction) {
		GameLogic.updatePlayer(player,delta_x,delta_y,direction);
		updateScoreTable(GameLogic.players);
	}
	
	public String getScoreList(List<Player> players) {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p+"\r\n");
		}
		return b.toString();
	}

	private class ReciverThread extends Thread{
		@Override
		public void run() {
			while (socket.isConnected()){
				try {
					ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream());
					ServerPacket serverPacket = (ServerPacket) inFromServer.readObject();

					if (App.me == null) {
						System.out.println(serverPacket.getPlayers().size());
						for (Player p : serverPacket.getPlayers()) {
							if (p.name.equals(App.name)) App.me = p;
						}
					}

					for (Player p : serverPacket.getPlayers()) {
						if (p.isConnected) {
							movePlayerOnScreen(p.getLastLocation(),p.getLocation(),p.getDirection());
						} else {
							removePlayerOnScreen(p.location);
						}
					}
					updateScoreTable(serverPacket.getPlayers());
				}catch (Exception e){
					//Connection terminated -> do nothing
				}
			}
			System.out.println("You have beeen disconnected from the server.");
		}
	}
}

