package coordinator;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CoordinatorInterface extends Remote {
    boolean registerUser(String username, String password, String department, String role) throws RemoteException;
    User login(String username, String password) throws RemoteException;
    String readFileFromAnyNode(String token, String department, String filename) throws RemoteException;


    String uploadFile(String token, String department, String filename, String content) throws RemoteException;
    String updateFile(String token, String department, String filename, String content) throws RemoteException;
    String deleteFile(String token, String department, String filename) throws RemoteException;
}
