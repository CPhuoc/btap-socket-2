import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;

public class Server {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8888); // Sử dụng cổng 8888
            System.out.println("Server đã khởi động và đang chờ kết nối...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Chấp nhận kết nối từ client
                System.out.println("Client đã kết nối: " + clientSocket);

                // Tạo một luồng mới để xử lý kết nối với client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            for (int i = 1; i <= 1000; i++) {
                out.println(i); // Gửi số i đến client
                Thread.sleep(1000); // Dừng 1 giây
            }
            out.println("END"); // Gửi ký tự "END" để client biết khi nào dừng nhận dữ liệu
            System.out.println("Đã gửi tất cả các số đến client.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}