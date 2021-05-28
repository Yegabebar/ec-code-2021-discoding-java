package org.example.features.servers;

import org.example.core.Template;
import org.example.features.channels.ChannelDao;
import org.example.features.user.AuthController;
import org.example.models.Channel;
import org.example.models.Server;
import org.example.utils.SessionUtils;
import org.example.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.sql.Date;
import java.util.*;

public class ServerController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final ServerDao serverDao = new ServerDao();
    private final ChannelDao channelDao = new ChannelDao();

    public String createServer(Request request, Response response){
        if (request.requestMethod().equals("GET")) {
            Map<String, Object> model = new HashMap<>();
            int userId = SessionUtils.getSessionUserId(request);
            model.put("servers", serverDao.getServersJoined(userId));
            return Template.render("create_server.html", model);
        }

        // Get parameters
        Map<String, String> query = URLUtils.decodeQuery(request.body());
        String name = query.get("server-name");
        String avatarUrl = query.get("avatar-url");

        // Set variables
        int owner_id= SessionUtils.getSessionUserId(request);
        Date now = new Date(Calendar.getInstance().getTimeInMillis());

        // Creates the new server, gets the ID and adds the owner in the membership table
        int newServerId = serverDao.createServer(name, avatarUrl, now.toString(), owner_id);
        // Creates the default channels for the new server
        List<String> channelNames = new ArrayList<>();
        channelNames.add("General");
        channelNames.add("Random");
        channelNames.add("Music");
        channelNames.add("Games");
        channelNames.add("Help");

        channelNames.forEach(channelName ->{
            channelDao.createChannel(newServerId, channelName, now.toString());
        });

        serverDao.joinServer(owner_id, newServerId);

        response.redirect("/friends/");
        // Map<String, Object> model = new HashMap<>();
        // return Template.render("auth_login.html", model);
        return "KO";
    }

    public String getServer(Request request, Response response){
        int id = Integer.parseInt(request.params(":id"));
        int userId = SessionUtils.getSessionUserId(request);
        Server currentServer = serverDao.getServerById(id);
        List<Channel> channels = channelDao.getChannelsByServerId(id);
        // response.redirect("/friends/");
        Map<String, Object> model = new HashMap<>();
        model.put("current-server", currentServer.getId());
        model.put("channels", channels);
        channels.forEach(channel -> {
            System.out.println(channel.getName());
        });
        model.put("servers", serverDao.getServersJoined(userId));
        return Template.render("channel_list.html", model);
    }

    /*public String listServers(Request request, Response response){
        if (request.requestMethod().equals("GET")) {
            Map<String, Object> model = new HashMap<>();
            return Template.render("create_server.html", model);
        }

        // Get parameters
        Map<String, String> query = URLUtils.decodeQuery(request.body());
        String name = query.get("server-name");
        String avatarUrl = query.get("avatar-url");

        // Set variables
        int owner_id= SessionUtils.getSessionUserId(request);
        Date now = new Date(Calendar.getInstance().getTimeInMillis());

        // Creates the new server, gets the ID and adds the owner in the membership table
        int newServerId = serverDao.createServer(name, avatarUrl, now.toString(), owner_id);
        serverDao.joinServer(owner_id, newServerId);

        response.redirect("/friends/");
        // Map<String, Object> model = new HashMap<>();
        // return Template.render("auth_login.html", model);
        return "KO";
    }*/

}
