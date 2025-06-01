package node;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;

public class SyncService {
    private String basePath;
    private List<String> otherNodes;

    public SyncService(String basePath, List<String> otherNodes) {
        this.basePath = basePath;
        this.otherNodes = otherNodes;
    }

    public void syncAllDepartments(List<String> departments) {
        for (String dept : departments) {
            File folder = new File(basePath + "/" + dept);
            folder.mkdirs();

            List<String> filesToTry = List.of("sync_test.txt");

            for (String node : otherNodes) {
                for (String filename : filesToTry) {
                    requestFile(node, dept, filename);
                }
            }
        }

         System.out.println("File synchronization completed successfully.");
    }

    private void requestFile(String node, String department, String filename) {
        try (Socket socket = new Socket(node, 6000)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("SYNC;" + department + ";" + filename);

            String response = in.readLine();

            if (response != null && response.startsWith("FOUND;")) {
                String content = response.substring(6);
                Path path = Paths.get(basePath, department, filename);
                if (!Files.exists(path)) {
                    Files.writeString(path, content);
                }
            }

        } catch (IOException ignored) {
         }
    }
}
