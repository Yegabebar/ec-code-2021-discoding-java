package org.example.features.channels;

import org.example.core.Template;
import org.example.features.servers.ServerDao;
import org.example.models.Server;
import org.example.utils.SessionUtils;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class ChannelController {

    public final ServerDao serverDao = new ServerDao();
    public final ChannelDao channelDao = new ChannelDao();

    public String list(Request request, Response response){
        int id = Integer.parseInt(request.params(":id"));
        int userId = SessionUtils.getSessionUserId(request);
        Server currentServer = serverDao.getServerById(id);

        Map<String, Object> model = new HashMap<>();
        model.put("currentServer", currentServer);
        model.put("channels", channelDao.getChannelsByServerId(id));
        // Used to populate the sidebar with the user's server list (created or joined) anywhere on the site
        model.put("servers", serverDao.getServersJoined(userId));
        return Template.render("channel_list.html", model);
    }
}
