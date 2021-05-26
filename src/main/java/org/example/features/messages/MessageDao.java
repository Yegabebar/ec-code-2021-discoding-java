package org.example.features.messages;

import org.example.core.Database;
import org.example.models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    public List<Message> getMessagesForConversationId(int conversationId) {
        List<Message> messages = new ArrayList<>();
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM messages WHERE conversation_id = ? ORDER BY created_at ASC");
            st.setInt(1, conversationId);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Message message = MessageDao.mapMessage(rs);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static Message mapMessage(ResultSet rs) throws SQLException {
        int i = 1;
        return new Message(
                rs.getInt(i++), // id
                rs.getInt(i++), // conversationId
                rs.getInt(i++), // userId
                rs.getString(i++), // content
                rs.getString(i++) // createdAt
        );
    }

    public int createMessage(Message message) {
        Connection connection = Database.get().getConnection();
        int newId = 0;
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            st.setInt(1, message.getConversationId());
            st.setInt(2, message.getUserId());
            st.setString(3, message.getContent());
            st.setString(4, message.getCreatedAt());

            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                newId = rs.getInt(1);
                message.setId(newId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newId;
    }
}
