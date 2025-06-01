package coordinator;

import java.io.*;
import java.net.Socket;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;

public class CoordinatorImpl extends UnicastRemoteObject implements CoordinatorInterface {
    private UserManager userManager;
    private List<String> nodes = Arrays.asList("localhost");
    private Map<String, Boolean> nodeStatus = new HashMap<>();
    private int currentIndex = 0;

    public CoordinatorImpl() throws RemoteException {
        super();
        userManager = new UserManager();
        for (String node : nodes) nodeStatus.put(node, true);
    }

    @Override
    public boolean registerUser(String username, String password, String department, String role) {
        return userManager.registerUser(username, password, department, role);
    }

    @Override
    public User login(String username, String password) {
        return userManager.login(username, password);
    }

    @Override
    public synchronized String readFileFromAnyNode(String token, String department, String filename) {
        User user = userManager.getUserByToken(token);
        if (user == null) return "Unauthorized";

        int start = currentIndex;
        currentIndex = (currentIndex + 1) % nodes.size();

        for (int i = 0; i < nodes.size(); i++) {
            int index = (start + i) % nodes.size();
            String node = nodes.get(index);

            if (!nodeStatus.getOrDefault(node, true)) continue;

            try (Socket socket = new Socket(node, 5000)) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("READ;" + department + ";" + filename);
                String response = in.readLine();

                nodeStatus.put(node, true);
                if (response != null && !response.startsWith("Error")) return response;

            } catch (IOException e) {
                nodeStatus.put(node, false);
            }
        }

        return "File not found in any node.";
    }

     @Override
    public String uploadFile(String token, String department, String filename, String content) {
        User user = userManager.getUserByToken(token);
        if (user == null || !user.getDepartment().equals(department)) return "Unauthorized";

        return sendCommandToNode("UPLOAD", department, filename, content);
    }

     @Override
    public String updateFile(String token, String department, String filename, String content) {
        User user = userManager.getUserByToken(token);
        if (user == null || !user.getDepartment().equals(department)) return "Unauthorized";

        return sendCommandToNode("UPDATE", department, filename, content);
    }

     @Override
    public String deleteFile(String token, String department, String filename) {
        User user = userManager.getUserByToken(token);
        if (user == null || !user.getDepartment().equals(department)) return "Unauthorized";

        return sendCommandToNode("DELETE", department, filename, "");
    }

     private String sendCommandToNode(String action, String department, String filename, String content) {
        for (String node : nodes) {
            try (Socket socket = new Socket(node, 5000)) {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String command = action + ";" + department + ";" + filename + ";" + content;
                out.println(command);

                return in.readLine();

            } catch (IOException e) {
                System.out.println("Node " + node + " is unreachable.");
            }
        }
        return "Operation failed: no nodes available.";
    }
}
