package org.example.features.friends;

import org.example.core.Database;
import org.example.features.user.UserDao;
import org.example.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendDao {

    public List<User> getFriendsForUserId(int userId) {
        List<User> users = new ArrayList<>();
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT users.* FROM users LEFT JOIN friends ON users.id = friends.friend_user_id WHERE friends.user_id = ?");

            st.setInt(1, userId);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                User user = UserDao.mapToUser(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean isAlreadyFriend(int userId, int friendId) {
        Connection connection = Database.get().getConnection();
        boolean isAlreadyFriend = false;
        try {
            PreparedStatement st = connection.prepareStatement("SELECT COUNT(*) FROM friends WHERE (user_id = ? AND friend_user_id = ?) OR (user_id = ? AND friend_user_id = ?)");

            st.setInt(1, userId);
            st.setInt(2, friendId);
            st.setInt(3, friendId);
            st.setInt(4, userId);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                isAlreadyFriend = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isAlreadyFriend;
    }

    public int addFriend(int userId, int friendId) {
        Connection connection = Database.get().getConnection();
        int newId = 0;
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO friends VALUES (NULL, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            st.setInt(1, userId);
            st.setInt(2, friendId);

            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newId;
    }
}
