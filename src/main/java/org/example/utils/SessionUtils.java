package org.example.utils;

import org.example.models.User;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.Spark;

public class SessionUtils {

    public static void createSession(User user, Request request, Response response) {
        Session session = request.session(true);
        session.attribute("user_id", user.getId());
        response.cookie("/", "user_id", "" + user.getId(), 3600, true);
    }

    public static int getSessionUserId(Request request) {
        Session session = request.session(false);
        if (session == null) {
            Spark.halt(401, "No valid session found");
            return 0;
        }

        int userId = 0;

        Object userIdObj = session.attribute("user_id");
        if (userIdObj instanceof Integer) {
            userId = (Integer) userIdObj;
        } else if (userIdObj instanceof String) {
            userId = Integer.parseInt((String) userIdObj);
        } else {
            Spark.halt(401, "No valid session found");
        }

        return userId;
    }
}
