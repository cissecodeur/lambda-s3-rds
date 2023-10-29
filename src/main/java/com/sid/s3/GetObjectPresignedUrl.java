
package com.sid.s3;
import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@RestController
@RequestMapping("/s3")
public class GetObjectPresignedUrl {

    @GetMapping("/get/{file-name}")
    public ResponseEntity<?> getObjectPresignedUrlFromS3(@PathVariable("file-name") String fileName) {

       ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
       Region region = Region.EU_WEST_3;

       String bucketName = "mabox-s3";
       String  objectKey  = "images/"+fileName;

       S3Presigner presigner = S3Presigner.builder()
               .region(region)
               .credentialsProvider(credentialsProvider)
               .build();

       return  new ResponseEntity<>(getPresignedUrl(presigner, bucketName, objectKey), HttpStatus.OK) ;

    }
    public static String getPresignedUrl(S3Presigner presigner, String bucketName, String keyName ) {

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(60))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);

            return presignedGetObjectRequest.url().toString(); // Retourne le lien URL sous forme de chaîne
        } catch (S3Exception e) {
            e.printStackTrace();
            return null; // En cas d'erreur, renvoie null ou effectuez une gestion d'erreur appropriée
        }
    }

}
