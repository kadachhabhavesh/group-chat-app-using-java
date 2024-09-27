import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);
            System.out.println("Connected to server.");

            
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            // Thread for receiving messages from server
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Error receiving message from server: " + e.getMessage());
                }
            }).start();

            // Sending messages to the server
            String clientMessage;
            while ((clientMessage = consoleReader.readLine()) != null) {
                writer.println(clientMessage);
            }

        } catch (IOException e) {
            System.out.println("Client exception: " + e.getMessage());
        }
    }
}
