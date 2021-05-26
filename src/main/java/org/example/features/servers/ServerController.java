package org.example.features.servers;

import org.example.core.Template;
import org.example.features.user.AuthController;
import org.example.utils.SessionUtils;
import org.example.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ServerController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final ServerDao serverDao = new ServerDao();

    public String createServer(Request request, Response response){
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
    }


}
