package org.example.features.user;

import org.example.core.Database;
import org.example.models.User;

import java.sql.*;

public class UserDao {

    public void createUser(String email, String username, String password, String avatarUrl) {
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO users (email, username, password, avatar_url, confirmed) VALUES (?, ?, ?, ?, ?)");

            st.setString(1, email);
            st.setString(2, username);
            st.setString(3, password);
            st.setString(4, avatarUrl);
            st.setInt(5, 0);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNextUid(){
        /**
         * This method checks which UID is the greater within the users table
         * then increment it by one, and returns it
         */
        Connection connection = Database.get().getConnection();
        String nextUid = "";
        try {
            // PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE email=? AND password=?");
            String query = "SELECT MAX(substring_index(username, '#', -1)) AS ID\n"+
                            "FROM users\n"+
                            "WHERE ID REGEXP '^[0-9]+$';\n";
            PreparedStatement st = connection.prepareStatement(query);

            ResultSet rs = st.executeQuery();
            // We strip the UID part, convert it to an int, increment this number by one and then convert it back
            // to a String so we can add leading zeros up to 4 characters total.
            if(rs.next()){
                int nextNumber = (Integer.parseInt(rs.getString(1)))+1;
                nextUid = String.format("%04d", nextNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nextUid;
    }

    public String confirmEmail(String email) {
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE users SET confirmed = 1 WHERE email = ?");
            st.setString(1, email);
            System.out.println("Statement: "+st);
            st.executeUpdate();
            return "OK";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public User checkEmail(String email) {
        User user = null;

        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE email=?");

            st.setString(1, email);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                user = mapToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public User getUserByCredentials(String email, String password) {
        User user = null;

        Connection connection = Database.get().getConnection();
        try {
            // PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE email=? AND password=?");
            PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE email=? AND password=?");

            st.setString(1, email);
            st.setString(2, password);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                user = mapToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public User findUserWithUsername(String username) {
        User user = null;

        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE username=? ");

            st.setString(1, username);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                user = mapToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public User getUserById(int userId) {
        User user = null;

        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE id=? ");

            st.setInt(1, userId);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                user = mapToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static User mapToUser(ResultSet rs) throws SQLException {
        int i = 1;
        return new User(
                rs.getInt(i++), // id
                rs.getString(i++), // email
                rs.getString(i++), // username,
                rs.getString(i++), // password
                rs.getString(i++) // avatarUrl
        );
    }

}
