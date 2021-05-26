package org.example.features.conversation;

import org.example.core.Database;
import org.example.features.user.UserDao;
import org.example.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationDao {
    public int getConversationBetweenUsers(int userId, int userId2) {
        int conversationId = 0;
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT id FROM conversations WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)");

            st.setInt(1, userId);
            st.setInt(2, userId2);
            st.setInt(3, userId2);
            st.setInt(4, userId);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                conversationId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversationId;
    }

    public int createConversationBetweenUsers(int userId, int userId2) {
        Connection connection = Database.get().getConnection();
        int newId = 0;
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO conversations VALUES (NULL, ?, ?, CURRENT_TIME())", Statement.RETURN_GENERATED_KEYS);

            st.setInt(1, userId);
            st.setInt(2, userId2);

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

    public List<ConvForUser> getAllConversationsForUser(int userId) {
        List<ConvForUser> conversations = new ArrayList<>();
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT c.id, u.id, u.username, u.avatar_url, c.updated_at FROM conversations as c JOIN users as u ON (c.user1_id = u.id OR c.user2_id = u.id) WHERE (c.user1_id = ? OR c.user2_id = ?) AND u.id != ? ORDER BY c.updated_at DESC;");

            st.setInt(1, userId);
            st.setInt(2, userId);
            st.setInt(3, userId);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                conversations.add(ConversationDao.mapToConvForUser(userId, rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversations;
    }

    public ConvForUser getConversationForUser(int conversationId, int userId) {
        ConvForUser conv = null;
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT c.id, u.id, u.username, u.avatar_url, c.updated_at FROM conversations as c JOIN users as u ON (c.user1_id = u.id OR c.user2_id = u.id) WHERE (c.id = ?) AND u.id != ?");

            st.setInt(1, conversationId);
            st.setInt(2, userId);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                conv = mapToConvForUser(userId, rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conv;
    }

    public static ConvForUser mapToConvForUser(int userId, ResultSet rs) throws SQLException {
        int i = 1;
        return new ConvForUser(
                rs.getInt(i++), // id
                userId, // userId
                rs.getInt(i++), // interlocutorId
                rs.getString(i++), // interlocutorName
                rs.getString(i++), // interlocutorAvatar
                rs.getString(i++) // updatedAt
        );
    }
}
