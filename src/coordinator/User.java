package coordinator;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private String username, password, department, role, token;

    public User(String username, String password, String department, String role) {
        this.username = username;
        this.password = password;
        this.department = department;
        this.role = role;
        this.token = UUID.randomUUID().toString();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDepartment() { return department; }
    public String getRole() { return role; }
    public String getToken() { return token; }
}
