package org.example;

import org.example.core.Conf;
import org.example.core.Database;
import org.example.core.Template;
import org.example.features.conversation.ConversationController;
import org.example.features.friends.FriendController;
import org.example.features.user.AuthController;
import org.example.middlewares.AuthMiddleware;
import org.example.middlewares.LoggerMiddleware;
import org.example.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.simple.SimpleLogger;
import spark.Session;
import spark.Spark;

import java.util.HashMap;

public class App {
    static {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
    }

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        initialize();

        final AuthController authController = new AuthController();
        final ConversationController conversationController = new ConversationController();
        final FriendController friendController = new FriendController();

        logger.info("Welcome to Discoding Backend!");

        // Conversations
        Spark.get("/conversations/start_with_user", (req, res) -> conversationController.getOrCreateConversationWithUser(req, res));
        Spark.post("/conversations/:id/add_message", (req, res) -> conversationController.addMessage(req, res));
        Spark.get("/conversations/:id", (req, res) -> conversationController.detail(req, res));

        // Friends
        Spark.get("/friends/add", (req, res) -> friendController.addFriend(req, res));
        Spark.post("/friends/add", (req, res) -> friendController.addFriend(req, res));
        Spark.get("/friends/", (req, res) -> friendController.list(req, res));

        // Login
        Spark.get(Conf.ROUTE_LOGIN, (req, res) -> authController.login(req, res));
        Spark.post(Conf.ROUTE_LOGIN, (req, res) -> authController.login(req, res));


        // Default
        Spark.get("/", (req, res) -> {
            Session session = req.session(false);
            if (session == null) {
                res.redirect(Conf.ROUTE_LOGIN);
            } else {
                res.redirect(Conf.ROUTE_AUTHENTICATED_ROOT);
            }
            return null;
        });
    }

    static void initialize() {
        Template.initialize();
        Database.get().checkConnection();

        // Display exceptions in logs
        Spark.exception(Exception.class, (e, req, res) -> e.printStackTrace());

        // Serve static files (img/css/js)
        Spark.staticFiles.externalLocation(Conf.STATIC_DIR.getPath());

        // Configure server port
        Spark.port(Conf.HTTP_PORT);
        final LoggerMiddleware log = new LoggerMiddleware();
        Spark.before(log::process);

        // Protect logged routes
        final AuthMiddleware auth = new AuthMiddleware();
        Spark.before((auth::process));
    }
}
