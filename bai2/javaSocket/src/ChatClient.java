import java.io.*;
import java.net.*;

public class ChatClient {
    private static BufferedReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(in.readLine());  // Enter your username:
            String username = userInput.readLine();
            out.println(username);

            // Create a new thread to read messages from the server
            new Thread(new IncomingReader()).start();

            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                out.println(userMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class IncomingReader implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}