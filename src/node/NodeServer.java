package node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeServer {
    public static void main(String[] args) {
        FileManager fileManager = new FileManager("node_storage_node1");

        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Node server running on port 5000");

            while (true) {
                Socket client = serverSocket.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                String command = in.readLine();
                String[] parts = command.split(";", 4);

                String action = parts[0];
                String department = parts[1];
                String filename = parts[2];
                String content = (parts.length == 4) ? parts[3] : "";

                String result;

                switch (action) {
                    case "READ":
                        result = fileManager.readFile(department, filename);
                        break;
                    case "UPLOAD":
                        result = fileManager.saveFile(department, filename, content);
                        break;
                    case "UPDATE":
                        result = fileManager.updateFile(department, filename, content);
                        break;
                    case "DELETE":
                        result = fileManager.deleteFile(department, filename);
                        break;
                    default:
                        result = "Unknown command.";
                        break;
                }

                out.println(result);
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
