package coordinator;

import java.util.HashMap;

public class UserManager {
    private HashMap<String, User> users = new HashMap<>();

    public boolean registerUser(String username, String password, String department, String role) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, password, department, role));
        return true;
    }

    public User login(String username, String password) {
        if (users.containsKey(username)) {
            User user = users.get(username);
            if (user.getPassword().equals(password)) return user;
        }
        return null;
    }

    public User getUserByToken(String token) {
        for (User user : users.values()) {
            if (user.getToken().equals(token)) return user;
        }
        return null;
    }
}
