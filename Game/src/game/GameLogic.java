package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class GameLogic {
public static List<Player> players = new ArrayList<Player>();	

	
	
	public static Player makePlayer(String name) {
		Player me;
		pair p=getRandomFreePosition();
		me = new Player(name,p,"up");
		players.add(me);
		return me;
	};	
	
	public static void makeVirtualPlayer()	{    // just demo/testing player - not in real game 
		pair p=getRandomFreePosition();
		Player kaj = new Player("Kaj",p,"up");
		players.add(kaj);
	}
	
	public static pair getRandomFreePosition()
	// finds a random new position which is not wall 
	// and not occupied by other players 
	{
		int x = 1;
		int y = 1;
		boolean foundfreepos = false;
		while  (!foundfreepos) {
			Random r = new Random();
			x = Math.abs(r.nextInt()%18) +1;
			y = Math.abs(r.nextInt()%18) +1;
			if (Generel.board[y].charAt(x)==' ') // er det gulv ?
			{
				foundfreepos = true;
				for (Player p: players) {
					if (p.getXpos()==x && p.getYpos()==y) //pladsen optaget af en anden 
						foundfreepos = false;
				}
				
			}
		}
		pair p = new pair(x,y);
		return p;
	}
	
	public static void updatePlayer(Player player, int delta_x, int delta_y, String direction) {
		//Adds the player if they are not in the list, consider making this a separate method
		Boolean playerExists = false;
		for (Player p : players) {
			if (p.getName().equals(player.getName())) {
				playerExists = true;
				player = p;
			}
		}
		if (!playerExists) {
			players.add(player);
		}

		player.direction = direction;
		int x = player.getXpos(),y = player.getYpos();

		if (Generel.board[y+delta_y].charAt(x+delta_x)=='w') {
			player.addPoints(-1);
		} 
		else {
			// collision detection
			Player p = getPlayerAt(x+delta_x,y+delta_y);
			if (p!=null) {
              player.addPoints(10);
              //update the other player
              p.addPoints(-10);
              pair pa = getRandomFreePosition();
              p.setLocation(pa);
              pair oldpos = new pair(x+delta_x,y+delta_y);
              Gui.movePlayerOnScreen(oldpos,pa,p.direction);
			} else 
				player.addPoints(1);
			pair oldpos = player.getLocation();
			pair newpos = new pair(x+delta_x,y+delta_y); 
			Gui.movePlayerOnScreen(oldpos,newpos,direction);
			player.setLocation(newpos);
		}
		
		
	}
	
	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}
	
	
	

}
