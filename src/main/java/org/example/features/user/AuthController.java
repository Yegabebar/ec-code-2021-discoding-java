package org.example.features.user;

import org.example.core.Conf;
import org.example.core.Template;
import org.example.models.User;
import org.example.utils.SessionUtils;
import org.example.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserDao userDao = new UserDao();

    public String signUp(Request request, Response response) throws NoSuchAlgorithmException {
        if (request.requestMethod().equals("GET")) {
            Map<String, Object> model = new HashMap<>();
            return Template.render("auth_signup.html", model);
        }

        // Get parameters
        Map<String, String> query = URLUtils.decodeQuery(request.body());
        String email = query.get("email");
        String username = query.get("username");
        String password = query.get("password");
        String passwordConfirm = query.get("password_confirm");

        // Variables
        AtomicReference<Boolean> duplicate = new AtomicReference<>(false);
        Map<String, Object> model = new HashMap<>();

        // If both passwords are the same, check if user already exists with this email address
        if(password.equals(passwordConfirm)){
            User user = userDao.checkEmail(email);
            if(user==null){

                username = username+"#"+userDao.getNextUid();
                String hash = hashPassword(password);
                userDao.createUser(email, username, hash);
            }

        }

        return Template.render("auth_login.html", model);
    }

    public String login(Request request, Response response) {
        if (request.requestMethod().equals("GET")) {
            Map<String, Object> model = new HashMap<>();
            return Template.render("auth_login.html", model);
        }

        Map<String, String> query = URLUtils.decodeQuery(request.body());
        String email = query.get("email");
        String password = query.get("password");

        // Authenticate user
        User user = userDao.getUserByCredentials(email, password);
        if (user == null) {
            logger.info("User not found. Redirect to login");
            response.removeCookie("session");
            response.redirect(Conf.ROUTE_LOGIN);
            return "KO";
        }

        // Create session
        SessionUtils.createSession(user, request, response);

        // Redirect to medias page
        response.redirect(Conf.ROUTE_AUTHENTICATED_ROOT);
        return null;
    }
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        password = password+"tH1sC@nTS3RI0u51yb345@1T4M1riT3!§!§§§??";
        byte [] digest = sha256Encrypt(password);
        return hexaToString(digest);
    }

    public static byte[] sha256Encrypt(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256"); //SHA (Secure Hash Algorithm)
        md.update(message.getBytes());
        byte[] digest = md.digest();
        return digest;
    }

    public static String hexaToString(byte[] digest ){
        // Convert digest to a string
        StringBuffer hexString = new StringBuffer();
        for (byte b : digest) {
            if ((0xff & b) < 0x10) {
                hexString.append("0").append(Integer.toHexString((0xFF & b)));
            } else {
                hexString.append(Integer.toHexString(0xFF & b));
            }
        }
        return hexString.toString();
    }

}
