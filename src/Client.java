import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";
        int serverPort = 12345;

        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Connected to server");

            System.out.print("Enter your name: ");
            String name = userInput.readLine();

            output.println(name);

            Thread thread = new Thread(new MessageHandler(input));
            thread.start();

            while (true) {
                String message = userInput.readLine();
                output.println(message);
            }
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e);
        }
    }

    private static class MessageHandler implements Runnable {
        private final BufferedReader input;

        public MessageHandler(BufferedReader input) {
            this.input = input;
        }

        public void run() {
            try {
                while (true) {
                    String message = input.readLine();
                    if (message == null) {
                        break;
                    }
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Error handling message: " + e);
            }
        }
    }
}
