import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            System.out.println("Server is waiting for clients...");
            ServerSocket serverSocket = new ServerSocket(8080);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New User connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler); 

                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Client handler class 
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.out.println("Error in ClientHandler: " + e.getMessage());
            }
        }

        
        public void run() {
            try {
                writer.println("Enter your name: ");
                clientName = reader.readLine();
                System.out.println(clientName + " has joined the chat!");
                Server.broadcastMessage(clientName + " has joined the chat!", this);
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                    Server.broadcastMessage(clientName + ": " + message, this);
                }

            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Failed to close socket: " + e.getMessage());
                }
                System.out.println(clientName + " has left the chat.");
                clients.remove(this);
                Server.broadcastMessage(clientName + " has left the chat.", this);
            }
        }

        public void sendMessage(String message) {
            writer.println(message);
        }
    }
}