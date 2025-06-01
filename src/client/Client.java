package client;

import coordinator.CoordinatorInterface;
import coordinator.User;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            CoordinatorInterface coordinator = (CoordinatorInterface) registry.lookup("Coordinator");

            Scanner sc = new Scanner(System.in);
            System.out.print("1- Register\n2- Login\nChoose: ");
            int choice = sc.nextInt(); sc.nextLine();

            System.out.print("Username: ");
            String username = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();

            if (choice == 1) {
                System.out.print("Department: ");
                String department = sc.nextLine();
                System.out.print("Role (manager/employee): ");
                String role = sc.nextLine();
                boolean success = coordinator.registerUser(username, password, department, role);
                System.out.println(success ? "Registered successfully" : "User already exists");
            } else {
                User user = coordinator.login(username, password);
                if (user != null) {
                    System.out.println("Login successful. Token: " + user.getToken());

                     System.out.println("\nAvailable operations:");
                    System.out.println("1 - Read file from another department");
                    System.out.println("2 - Upload file to your department");
                    System.out.println("3 - Update file in your department");
                    System.out.println("4 - Delete file from your department");
                    System.out.print("Choose operation: ");
                    int op = sc.nextInt(); sc.nextLine();

                    if (op == 1) {
                        System.out.print("Department to read from: ");
                        String targetDept = sc.nextLine();
                        System.out.print("Filename: ");
                        String targetFile = sc.nextLine();

                        String fileContent = coordinator.readFileFromAnyNode(user.getToken(), targetDept, targetFile);
                        System.out.println("File content:\n" + fileContent);

                    } else if (op == 2) {
                        System.out.print("Filename to upload: ");
                        String filename = sc.nextLine();
                        System.out.print("File content: ");
                        String content = sc.nextLine();

                        String result = coordinator.uploadFile(user.getToken(), user.getDepartment(), filename, content);
                        System.out.println(result);

                    } else if (op == 3) {
                        System.out.print("Filename to update: ");
                        String filename = sc.nextLine();
                        System.out.print("New content: ");
                        String content = sc.nextLine();

                        String result = coordinator.updateFile(user.getToken(), user.getDepartment(), filename, content);
                        System.out.println(result);

                    } else if (op == 4) {
                        System.out.print("Filename to delete: ");
                        String filename = sc.nextLine();

                        String result = coordinator.deleteFile(user.getToken(), user.getDepartment(), filename);
                        System.out.println(result);
                    } else {
                        System.out.println("Invalid operation.");
                    }

                } else {
                    System.out.println("Login failed");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
