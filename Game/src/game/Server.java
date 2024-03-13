package game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public List<Socket> clients = new ArrayList<>();
    public static void main(String[] args) {
        Server server = new Server();
        try {
            ServerSocket serverSocket = new ServerSocket(7000);
            System.out.println("Server is running");
            while (true) {
                Socket socket = serverSocket.accept();
                server.clients.add(socket);
                ReciverThread reciverThread = server.new ReciverThread(socket);
                reciverThread.start();
                System.out.println("Client with ip: " + socket.getInetAddress() + " connected");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void sendToClients(ServerPacket serverPacket){
        try {
            clients.forEach(client -> {
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                    objectOutputStream.writeObject(serverPacket);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private class ReciverThread extends Thread {
        private Socket socket;
        private Player player;
        private Packet packet;
        public ReciverThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public synchronized void run() {
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    System.out.println("Client connected");
                    while (socket.isConnected()) {
                        packet = (Packet) objectInputStream.readObject();
                        player = packet.getPlayer();

                        if (player == null) {
                            player = GameLogic.makePlayer(packet.getKeypress());
                        }

                        switch (packet.getKeypress()) {
                            case "up":    GameLogic.updatePlayer(player,0,-1,"up");    break;
                            case "down":  GameLogic.updatePlayer(player,0,+1,"down");  break;
                            case "left":  GameLogic.updatePlayer(player,-1,0,"left");  break;
                            case "right": GameLogic.updatePlayer(player,+1,0,"right"); break;
                            case "exit":  GameLogic.disablePlayer(player); break;
                            default: break;
                        }
                        sendToClients(new ServerPacket(GameLogic.players));

                        //Special case player exits game
                        if (!player.isConnected){
                            GameLogic.removePlayer(player);
                            System.out.println("Client with ip: " + socket.getInetAddress() + " disconnected");
                            clients.remove(socket);
                            objectInputStream.close();
                            socket.close();
                        }
                    }
                } catch (Exception e) {
                    GameLogic.removePlayer(player);
                    clients.remove(socket);
                    System.out.println(e.getMessage());
                }
            }
        }
    }

