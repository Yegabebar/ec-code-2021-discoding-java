package org.example.middlewares;

import spark.Request;
import spark.Response;
import spark.Session;

/**
 * Checks if the request is authenticated.
 */
public class AuthMiddleware implements Middleware {
    @Override
    public void process(Request request, Response response) {
        String path = request.pathInfo();
        // Handle routes with no auth first
        if (path.equals("/") ||
                path.equals("/login") ||
                path.equals("/signup")) {

            return;
        }

        // Check existing session
        Session session = request.session(false);
        if (session != null) {
            return;
        }

        // Check existing cookie
        String cookie = request.cookie("JSESSIONID");
        if (cookie != null) {
            session = request.session(true);
            String userId = request.cookie("user_id");
            session.attribute("user_id", userId);
            return;
        }

        // If this point is reached, the user could not be authenticated
        response.redirect("/");
    }
}
