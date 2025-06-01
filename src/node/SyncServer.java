package node;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class SyncServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(6000)) {
            System.out.println("Sync server running on port 6000");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleSync(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleSync(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String request = in.readLine(); // SYNC;department;filename
            String[] parts = request.split(";");
            String department = parts[1];
            String filename = parts[2];

            Path filePath = Paths.get("node_storage_node1", department, filename);
            if (Files.exists(filePath)) {
                String content = Files.readString(filePath);
                out.println("FOUND;" + content);
            } else {
                out.println("NOT_FOUND");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
