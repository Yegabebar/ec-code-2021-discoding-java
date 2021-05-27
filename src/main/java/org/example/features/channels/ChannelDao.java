package org.example.features.channels;

import org.example.core.Database;
import org.example.models.Channel;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ChannelDao {

    public List<Channel> getChannelsByServerId(int server_id){
        List<Channel> channels = new ArrayList<>();

        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM channels WHERE server_id = ? ");
            st.setInt(1, server_id);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                channels.add(mapToChannel(rs));
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        return channels;
    }

    public Channel getChannelById(int channel_id){
        Channel channel = null;
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM channels WHERE id=?");
            st.setInt(1, channel_id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                System.out.println("Result set: "+rs.getString(1));
                channel = mapToChannel(rs);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public int createChannel(int serverId, String name, String createdAt) {
        Connection connection = Database.get().getConnection();
        int newID=0;
        try {
            String query = "INSERT INTO channels (server_id, name, created_at) VALUES (?, ?, ?)";
            PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, serverId);
            st.setString(2, name);
            st.setString(3, createdAt);

            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                newID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newID;
    }


    // Get messages in a given channel
    /*public List<> getMessagesByChannelId(int media_id){
        List<> messages = new ArrayList<>();

        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM episode WHERE media_id = ? ORDER BY release_date DESC");
            st.setInt(1, media_id);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                episodes.add(mapToEpisode(rs));
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        return episodes;
    }*/
    private Channel mapToChannel(ResultSet rs) throws SQLException, ParseException {
        return new Channel(
                rs.getInt(1), // id
                rs.getInt(2), // server_id
                rs.getString(3), // name
                rs.getString(4) // created_at

        );
    }
}
