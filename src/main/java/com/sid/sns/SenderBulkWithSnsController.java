package com.sid.sns;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class SenderBulkWithSnsController {

           final String TOPIC_ARN = "arn:aws:sns:eu-west-3:767138341720:testSnsTopic.fifo";

            @PostMapping("/sns/sendBulkSms")
            public ResponseEntity<?> sendBulkSmsWithSns(@RequestBody Map<String,String> dest) {

                String message = dest.get("message");
                List<String> phoneNumbers = List.of("+33623702258");

                SnsClient snsClient = SnsClient.builder()
                        .region(Region.EU_WEST_3)
                        .credentialsProvider(ProfileCredentialsProvider.create())
                        .build();

                subscribeToTopic(snsClient, TOPIC_ARN, phoneNumbers);
                publishTextSMSToTopicSubscriber(snsClient, message, TOPIC_ARN);

                snsClient.close();

                return new ResponseEntity<>(HttpStatus.OK);

            }


            public static void publishTextSMSToTopicSubscriber(SnsClient snsClient, String message, String topicArn) {
            try {
                PublishRequest request = PublishRequest.builder()
                        .message(message)
                        .topicArn(topicArn)
                        .build();

                PublishResponse result = snsClient.publish(request);
                System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

            } catch (SnsException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
        }

    public static void subscribeToTopic(SnsClient snsClient, String topicArn, List<String> phoneNumbers) {

        try {

            for (String phoneNumber : phoneNumbers) {
                SubscribeRequest request = SubscribeRequest.builder()
                        .protocol("sms")
                        .endpoint(phoneNumber)
                        .returnSubscriptionArn(true)
                        .topicArn(topicArn)
                        .build();

                SubscribeResponse result = snsClient.subscribe(request);
                System.out.println("Subscription ARN: " + result.subscriptionArn() + "\n\n Status is " + result.sdkHttpResponse().statusCode());
            }
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}