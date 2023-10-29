package com.sid.ses;

import java.io.IOException;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ses")
public class AmazonSES {


 final static String FROM = "yacoubc01@gmail.com";
  // The configuration set to use for this email. If you do not want to use a
  // configuration set, comment the following variable and the 
  // .withConfigurationSetName(CONFIGSET); argument below.
  static final String CONFIGSET = "ConfigSet";

  // The subject line for the email.
  static final String SUBJECT = "Amazon SES test (AWS SDK for Java)";
  
  // The HTML body for the email.
  static final String HTMLBODY = """
                     <h1>Amazon SES test With SDK</h1>
                     <a> by cisse </a>
                      """;
  // The email body for recipients with non-HTML email clients.
  static final String TEXTBODY = """
                               This email was sent through Amazon SES "
                               using the AWS SDK for Java.""";

  @PostMapping("/send")
  public ResponseEntity<?> sendEmailWithAmazonSES (@RequestBody Map<String,String> mail) throws IOException {

     final String TO = mail.get("destinataire");

    try {
      AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                       .standard()
                       .withRegion(Regions.EU_WEST_3)
                       .build();

            SendEmailRequest request = new SendEmailRequest()
                       .withDestination(new Destination().withToAddresses(TO))
                       .withMessage(new Message()
                         .withBody(new Body()
                            .withHtml(new Content()
                            .withCharset("UTF-8").withData(HTMLBODY))
                            .withText(new Content()
                            .withCharset("UTF-8").withData(TEXTBODY)))
                        .withSubject(new Content()
                           .withCharset("UTF-8").withData(SUBJECT)))
                       .withSource(FROM);
      client.sendEmail(request);
      System.out.println("Email sent!");
    } catch (Exception ex) {
      System.out.println("The email was not sent. Error message: " 
          + ex.getMessage());
      return new  ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }
    return  new ResponseEntity<>(HttpStatus.OK);
  }
}