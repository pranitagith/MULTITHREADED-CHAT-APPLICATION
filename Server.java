package ChatApp;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        int port = 1234; // Server port

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all clients
    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Remove client from the set
    public static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);
                Server.broadcast(message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeEverything();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void closeEverything() {
        try {
            Server.removeClient(this);
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Client disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}