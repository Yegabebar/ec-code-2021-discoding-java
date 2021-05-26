package org.example.features.servers;

import org.example.core.Database;
import org.example.models.Server;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ServerDao {

    /*public Server getServerByName(String name){
        Server server = null;
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM servers WHERE name=?");
            st.setString(1, name);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                server = mapToServer(rs);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return server;
    }*/

    public Server getServerById(int server_id){
        Server server = null;
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM servers WHERE id=?");
            st.setInt(1, server_id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                System.out.println("Result set: "+rs.getString(2));
                server = mapToServer(rs);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return server;
    }

    public int createServer(String name, String avatarUrl, String createdAt, int owner) {
        Connection connection = Database.get().getConnection();
        int newID=0;
        try {
            String query = "INSERT INTO servers (name, avatar_url, created_at, owner) VALUES (?, ?, ?, ?)";
            PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, name);
            st.setString(2, avatarUrl);
            st.setString(3, createdAt);
            st.setInt(4, owner);
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

    /*public List<Server> getServersOwned(int owner_id){
        List<Server> servers = new ArrayList<>();

        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM servers WHERE owner = ?");
            st.setInt(1, owner_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                servers.add(mapToServer(rs));
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        return servers;
    }*/

    public void joinServer(int user_id, int server_id){
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO membership (user_id, server_id) VALUES (?, ?)");
            st.setInt(1, user_id);
            st.setInt(2, server_id);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Server> getServersJoined(int user_id){
        List<Server> servers = new ArrayList<>();
        Connection connection = Database.get().getConnection();
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM membership WHERE user_id = ?");
            st.setInt(1, user_id);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                int server_id = rs.getInt(2);
                Server server = getServerById(server_id);
                servers.add(server);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servers;
    }

    private Server mapToServer(ResultSet rs) throws SQLException, ParseException {
        return new Server(
                rs.getInt(1), // id
                rs.getString(2), // name
                rs.getString(3), // avatar_url
                rs.getString(4), // created_at
                rs.getInt(5) // owner

        );
    }
}
