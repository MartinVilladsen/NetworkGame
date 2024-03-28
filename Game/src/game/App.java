package game;

import java.net.*;
import java.io.*;

import game.GameLogic;
import game.Gui;
import game.Player;
import javafx.application.Application;;

public class App {
	public static Player me;
	public static String ip;
	public static String name;
	public static void main(String[] args) throws Exception{	
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Indtast IP: ");
		ip = inFromUser.readLine();
		//ip = "localhost";
		System.out.println("Indtast spillernavn");
		name = inFromUser.readLine();
		Application.launch(Gui.class);
	}
}
