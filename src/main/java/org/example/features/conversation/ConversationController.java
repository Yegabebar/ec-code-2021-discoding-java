package org.example.features.conversation;

import org.example.core.Template;
import org.example.features.messages.MessageDao;
import org.example.features.servers.ServerDao;
import org.example.features.user.UserDao;
import org.example.models.Message;
import org.example.utils.SessionUtils;
import org.example.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationController {
    private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);

    final ConversationDao conversationDao = new ConversationDao();
    final MessageDao messageDao = new MessageDao();
    final UserDao userDao = new UserDao();
    ServerDao serverDao = new ServerDao();

    public String getOrCreateConversationWithUser(Request request, Response response) {
        int userId = SessionUtils.getSessionUserId(request);

        int userId2 = Integer.parseInt(request.queryParams("interlocutor_id"));

        if (userId2 <= 0) {
            Spark.halt(401, "Invalid user id!");
            return null;
        }

        // Try to find existing conversation
        int conversationId = conversationDao.getConversationBetweenUsers(userId, userId2);
        if (conversationId > 0) {
            response.redirect("/conversations/" + conversationId);
            return null;
        }

        conversationId = conversationDao.createConversationBetweenUsers(userId, userId2);
        logger.info("Created new conversation with id=" + conversationId);
        response.redirect("/conversations/" + conversationId);
        return null;
    }

    public String detail(Request request, Response response) {
        int userId = SessionUtils.getSessionUserId(request);
        int conversationId = Integer.parseInt(request.params(":id"));
        List<Message> messages = messageDao.getMessagesForConversationId(conversationId);
        ConvForUser conversation = conversationDao.getConversationForUser(conversationId, userId);
        Map<String, Object> model = new HashMap<>();
        model.put("conversationId", conversationId);
        model.put("conversations", conversationDao.getAllConversationsForUser(userId));
        model.put("user", userDao.getUserById(conversation.getUserId()));
        model.put("interlocutor", userDao.getUserById(conversation.getInterlocutorId()));
        model.put("messages", messages);
        // Used to populate the sidebar with the user's server list (created or joined) anywhere on the site
        model.put("servers", serverDao.getServersJoined(userId));
        return Template.render("conversation_detail.html", model);
    }

    public String addMessage(Request request, Response response) {
        int conversationId = Integer.parseInt(request.params(":id"));
        int userId = SessionUtils.getSessionUserId(request);

        Map<String, String> query = URLUtils.decodeQuery(request.body());
        String content = query.get("content");
        // The timestamp fix for all sent messages is here
        Date now = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String timeStamp = DATE_FORMAT.format(now);

        Message message = new Message(0, conversationId, userId, content, timeStamp);
        messageDao.createMessage(message);
        // The conversation order now work thanks to this method
        conversationDao.updateLastModification(conversationId, timeStamp);

        response.redirect("/conversations/" + conversationId);
        return null;
    }

    /**
     * Deletes a message, no confirmation sent as it only returns an empty String for now
     */
    public String deleteMessage(Request request, Response response) {
        int conversationId = Integer.parseInt(request.params(":id"));
        int messageId = Integer.parseInt(request.params(":msgid"));
        messageDao.deleteMessageById(messageId);

        response.redirect("/conversations/" + conversationId);
        return null;
    }

}
