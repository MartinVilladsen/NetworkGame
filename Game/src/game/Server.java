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
                System.out.println(server.clients.size() + " clients connected");
                ReciverThread reciverThread = server.new ReciverThread(socket);
                reciverThread.start();
                System.out.println("Client connected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendToClients(Packet packet){
        try {
            clients.forEach(client -> {
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                    objectOutputStream.writeObject(packet);
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
        public ReciverThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            System.out.println("Thread created");
            while (socket.isConnected()) {
                System.out.println("thread running");
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                    while (socket.isConnected()) {
                        Packet packet = (Packet) objectInputStream.readObject();
                        //GameLogic here




                        sendToClients(packet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
