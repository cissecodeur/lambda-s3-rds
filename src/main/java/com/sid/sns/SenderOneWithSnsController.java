package com.sid.sns;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.List;
import java.util.Map;

@RestController
public class SenderOneWithSnsController {


            final String usage = "\n" +
                    "Usage: " +
                    "   <message> <phoneNumber>\n\n" +
                    "Where:\n" +
                    "   message - The message text to send.\n\n" +
                    "   phoneNumber - The mobile phone number to which a message is sent (for example, +1XXX5550100). \n\n";


            @PostMapping("/sns/sendOneSms")
            public void sendOneSmsWithSns(@RequestBody Map<String,String> dest) {

                String message = dest.get("message");
                String phoneNumber = dest.get("phone");

                SnsClient snsClient = SnsClient.builder()
                        .region(Region.EU_WEST_3)
                        .credentialsProvider(ProfileCredentialsProvider.create())
                        .build();
                pubTextSMS(snsClient, message, phoneNumber);
                snsClient.close();

            }


            public static void pubTextSMS(SnsClient snsClient, String message, String phoneNumber) {
            try {
                PublishRequest request = PublishRequest.builder()
                        .message(message)
                        .phoneNumber(phoneNumber)
                        .build();

                PublishResponse result = snsClient.publish(request);
                System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

            } catch (SnsException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
        }


}