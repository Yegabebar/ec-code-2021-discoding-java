package org.example.features.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

public class MiscController {

    private static final Logger logger = LoggerFactory.getLogger(MiscController.class);

    public String sendEmail(Request request, Response response){
        String email = request.queryParams("email");
        String firstName = request.queryParams("first-name");
        String lastName = request.queryParams("last-name");
        String subject = request.queryParams("subject");
        String content = request.queryParams("content");
        logger.info("New email! "+email+" Name: "+firstName+" Last Name: "+lastName+" Subject: "+subject+" "+content);
        return "";
    }

}
