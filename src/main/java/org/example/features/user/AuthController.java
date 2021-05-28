package org.example.features.user;

import org.example.core.Conf;
import org.example.core.Template;
import org.example.models.User;
import org.example.utils.InputUtils;
import org.example.utils.SessionUtils;
import org.example.utils.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserDao userDao = new UserDao();

    /**
     * Used to GET the signup form, or POST a new user into the database.
     */
    public String signUp(Request request, Response response) throws NoSuchAlgorithmException {
        if (request.requestMethod().equals("GET")) {
            Map<String, Object> model = new HashMap<>();
            return Template.render("auth_signup.html", model);
        }

        // Get parameters
        Map<String, String> query = URLUtils.decodeQuery(request.body());
        String email = query.get("email");
        String username = query.get("username");
        String avatarUrl = query.get("avatar");
        if(!avatarUrl.substring(avatarUrl.lastIndexOf(".")+1).equals("png") ||
                !avatarUrl.substring(avatarUrl.lastIndexOf(".")+1).equals("jpeg") ||
                !avatarUrl.substring(avatarUrl.lastIndexOf(".")+1).equals("jpg")){
            avatarUrl = "/img/discord_logo.png";
        }
        String password = query.get("password");
        String passwordConfirm = query.get("password_confirm");

        // Variables
        AtomicReference<Boolean> duplicate = new AtomicReference<>(false);
        Map<String, Object> model = new HashMap<>();

        // If both passwords are the same, check if the user already exists with this email address
        if(password.equals(passwordConfirm)){
            User user = userDao.checkEmail(email);
            if(user==null){
                // If it doesn't exist, we determine which tag #ID will be used to create the new user
                username = username+"#"+userDao.getNextUid();
                String hash = hashPassword(password);
                userDao.createUser(email, username, hash, avatarUrl);
                // Simulates the confirmation link being sent to the user's email address.
                logger.info("Confirmation link: "+Conf.HOSTNAME+":"+Conf.HTTP_PORT+"/register/"+email);
            }

        }
        return "";
    }

    public String login(Request request, Response response) throws NoSuchAlgorithmException {
        Map<String, String> query = URLUtils.decodeQuery(request.body());
        String email = query.get("email");
        if (request.requestMethod().equals("GET") || !InputUtils.isEmailValid(email)) {
            Map<String, Object> model = new HashMap<>();
            return Template.render("auth_login.html", model);
        }

        String password = query.get("password");
        String hash = hashPassword(password);

        // Authenticate user
        User user = userDao.getUserByCredentials(email, hash);
        if (user == null || !user.isActivated()) {
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

    /**
     * Method used to handle click on the confirmation email.
     */
    public String confirmRegistration(Request request, Response response){
        String email = request.params(":email");
        // Checks if the email format is correct to avoid an unnecessary call to the database
        if(!InputUtils.isEmailValid(email)){
            return "Invalid email provided";
        } // If correct, check if a user exists
        User user = userDao.checkEmail(email);
        if(user!=null){
            String confirmation = userDao.confirmEmail(email);
            // Does not indicate if the email exists or not, to avoid a potential leak of mail addresses
            // followed by bruteforce/dictionnary attacks
            Map<String, Object> model = new HashMap<>();
            return Template.render("auth_login.html", model);
        }

        return "OK";
    }

    /**
     * Removes session and cookue, then disconnects the user
     */
    public String logout(Request request, Response response) {
        Session session = request.session(false);
        if (session != null) {
            session.invalidate();
        }
        response.removeCookie("session");
        response.removeCookie("JSESSIONID");
        response.redirect(Conf.ROUTE_LOGIN);

        return "";
    }

    /**
     * Hashes the password using a salt. This security measure should be improved, e.g. with per-user salt
     */
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
