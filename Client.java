package ChatApp;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
   // private String name;

    public Client(String name, String host, int port) {
       // this.name = name;
        
        try {
            socket = new Socket(host, port);
            System.out.println("Connected to the chat server");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Reading messages from the server
            new Thread(new ReceiveMessages()).start();

            // Sending messages to the server
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                out.println(name + ": " + message);
                scanner.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ReceiveMessages implements Runnable {
        public void run() {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        new Client(name, "localhost", 1234);
        sc.close();
    }
}