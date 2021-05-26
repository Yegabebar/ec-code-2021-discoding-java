package org.example.features.user;

import org.example.core.Database;
import org.example.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public User getUserByCredentials(String email, String password) {
        User user = null;

        Connection connection = Database.get().getConnection();
        try {
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
