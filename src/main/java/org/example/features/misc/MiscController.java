package org.example.features.misc;

import org.example.utils.InputUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MiscController {

    private static final Logger logger = LoggerFactory.getLogger(MiscController.class);

    /**
     * Used by the contact form to simulate a new mail being sent to contact@discoding.com, it's just logged for now
     */
    public String sendEmail(Request request, Response response){
        String email = request.queryParams("email");
        // If the given email is not valid, exit the function as we won't be able to reply to this email address
        if(!InputUtils.isEmailValid(email)){
            return "";
        }
        String firstName = request.queryParams("first-name");
        String lastName = request.queryParams("last-name");
        String subject = request.queryParams("subject");
        String content = request.queryParams("content");
        Date now = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String timeStamp = DATE_FORMAT.format(now);
        logger.info(timeStamp+" - contact@discoding.com: New email! - "+email+" Name: "+firstName+" Last Name: "+lastName+" Subject: "+subject+" Body: "+content);
        return "";
    }

}
