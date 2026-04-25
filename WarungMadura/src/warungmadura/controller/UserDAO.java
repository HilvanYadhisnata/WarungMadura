package warungmadura.controller;

import warungmadura.model.User;
import warungmadura.util.DatabaseUtil;

public class UserDAO {
    public User login(String username, String password) {
        for (String[] row : DatabaseUtil.readAll(DatabaseUtil.USERS_FILE)) {
            if (row.length >= 4 && row[1].equals(username) && row[2].equals(password)) {
                User u = new User();
                u.setId(Integer.parseInt(row[0].trim()));
                u.setUsername(row[1]);
                u.setPassword(row[2]);
                u.setRole(row[3]);
                return u;
            }
        }
        return null;
    }
}
