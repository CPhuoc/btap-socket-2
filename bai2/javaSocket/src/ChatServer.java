import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<Socket> clientSockets = new HashSet<>();
    private static Set<String> usernames = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Chat server started...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new ClientHandler(clientSocket).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String username;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Enter your username:");
                username = in.readLine();

                synchronized (usernames) {
                    while (usernames.contains(username)) {
                        out.println("Username already taken. Enter a different username:");
                        username = in.readLine();
                    }
                    usernames.add(username);
                }

                out.println("Welcome " + username + "! You can start chatting now.");
                synchronized (clientSockets) {
                    clientSockets.add(socket);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    broadcastMessage(username + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Error in client communication: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientSockets) {
                    clientSockets.remove(socket);
                }
                synchronized (usernames) {
                    usernames.remove(username);
                }
                broadcastMessage(username + " has left the chat.");
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clientSockets) {
                for (Socket clientSocket : clientSockets) {
                    try {
                        PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                        clientOut.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}