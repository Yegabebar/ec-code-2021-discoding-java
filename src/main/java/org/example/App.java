package org.example;

import org.example.core.Conf;
import org.example.core.Database;
import org.example.core.Template;
import org.example.features.channels.ChannelController;
import org.example.features.conversation.ConversationController;
import org.example.features.friends.FriendController;
import org.example.features.misc.MiscController;
import org.example.features.servers.ServerController;
import org.example.features.user.AuthController;
import org.example.middlewares.AuthMiddleware;
import org.example.middlewares.LoggerMiddleware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.simple.SimpleLogger;
import spark.Session;
import spark.Spark;

public class App {
    static {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
    }

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    /**
     * Notes about security:
     * - A big part of the user input sanitization is implicitly done by the use of PreparedStatement
     * in all Dao methods.
     * - An additional security measure has been set regarding the email addresses, for more information
     * please see the MiscController
     * - Check added on user signup to check if the given avatar url is not anything other than png/jpg/jpeg.
     * It prevents the user to try using links to malicious script files
     */

    public static void main(String[] args) {
        initialize();

        final AuthController authController = new AuthController();
        final ConversationController conversationController = new ConversationController();
        final FriendController friendController = new FriendController();
        final ServerController serverController = new ServerController();
        final MiscController miscController = new MiscController();
        final ChannelController channelController = new ChannelController();

        logger.info("Welcome to Discoding Backend!");

        // Conversations
        Spark.get("/conversations/:id/delete/:msgid", (req, res) -> conversationController.deleteMessage(req, res));
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

        // Logout
        Spark.get("/logout", (req, res) -> authController.logout(req, res));

        // Signup
        Spark.get("/signup", (req, res) -> authController.signUp(req, res));
        Spark.post("/signup", (req, res) -> authController.signUp(req, res));

        // Account Confirmation
        Spark.get("/register/:email", (req, res) -> authController.confirmRegistration(req, res));
        Spark.post("/register/:email", (req, res) -> authController.confirmRegistration(req, res));

        // Servers
        Spark.get("/create-server/", (req, res) -> serverController.createServer(req, res));
        Spark.post("/create-server/", (req, res) -> serverController.createServer(req, res));
        Spark.get("/server/:id", (req, res) -> serverController.getServer(req, res));

        // Miscellaneous
        Spark.get("/contact/", (req, res) -> miscController.sendEmail(req, res));
        Spark.post("/contact/", (req, res) -> miscController.sendEmail(req, res));

        // Channels
        // Spark.get("/server/:server_id/channel/", (req, res) -> channelController.list(req, res));

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
