package game;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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
    private void sendToClients(String keypress){
        try {
            clients.forEach(client -> {
                try {
                    DataOutputStream outToClient = new DataOutputStream(client.getOutputStream());
                    outToClient.writeBytes(keypress + '\n');
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
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String Keypress = "";

                    while (socket.isConnected()) {
                        Keypress = inFromClient.readLine();
                        sendToClients(Keypress);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
