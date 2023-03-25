import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) throws IOException {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress());

                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                String name = input.readLine();
                System.out.println(name + " joined the chat");

                clients.put(name, output);

                Thread thread = new Thread(new ClientHandler(clientSocket, name, input));
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final String name;
        private final BufferedReader input;

        public ClientHandler(Socket clientSocket, String name, BufferedReader input) {
            this.clientSocket = clientSocket;
            this.name = name;
            this.input = input;
        }

        public void run() {
            try {
                while (true) {
                    String message = input.readLine();
                    if (message == null) {
                        break;
                    }
                    System.out.println(name + ": " + message);

                    for (Map.Entry<String, PrintWriter> entry : clients.entrySet()) {
                        String clientName = entry.getKey();
                        PrintWriter output = entry.getValue();
                        if (!clientName.equals(name)) {
                            output.println(name + ": " + message);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e);
            } finally {
                clients.remove(name);
                System.out.println(name + " left the chat");
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e);
                }
            }
        }
    }
}
