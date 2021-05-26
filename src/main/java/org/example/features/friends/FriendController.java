package org.example.features.friends;

import org.example.core.Template;
import org.example.features.conversation.ConversationDao;
import org.example.features.user.AuthController;
import org.example.features.user.UserDao;
import org.example.models.User;
import org.example.utils.SessionUtils;
import org.example.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

public class FriendController {

    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);

    ConversationDao conversationDao = new ConversationDao();
    FriendDao friendDao = new FriendDao();
    UserDao userDao = new UserDao();

    public String list(Request request, Response response) {
        int userId = SessionUtils.getSessionUserId(request);
        if (userId == 0) {
            return null;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("conversationId", 0);
        model.put("conversations", conversationDao.getAllConversationsForUser(userId));
        model.put("friends", friendDao.getFriendsForUserId(userId));
        return Template.render("friend_list.html", model);
    }

    public String addFriend(Request request, Response response) {
        int userId = SessionUtils.getSessionUserId(request);

        Map<String, Object> model = new HashMap<>();
        model.put("conversations", conversationDao.getAllConversationsForUser(userId));

        if (request.requestMethod().equals("GET")) {
            model.put("message", "");
            return Template.render("friend_add.html", model);
        }

        Map<String, String> query = URLUtils.decodeQuery(request.body());
        String username = query.get("username");

        // FIXME What happens if the user does not exist?
        User newFriend = userDao.findUserWithUsername(username);
        logger.info("User = " + newFriend);
        if (friendDao.isAlreadyFriend(userId, newFriend.getId())) {
            model.put("message", "Déjà ami avec " + newFriend.getUsername() + " !");
        } else {
            friendDao.addFriend(userId, newFriend.getId());
            model.put("message", "Ami " + newFriend.getUsername() + " ajouté !");
        }

        return Template.render("friend_add.html", model);
    }
}
