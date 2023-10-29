package com.sid.s3;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.Instant;

@RestController
public class S3ImageUploader {

    @PostMapping("/s3/putObject")
    public ResponseEntity<?> putImageToS3Bucket(@RequestParam("datasFile") MultipartFile[] imageFiles) {

        S3Client s3Client = createS3Client();

        String bucketName = "mabox-s3";
        String folderName = "images";

        // Créez le compartiment S3 s'il n'existe pas
        if (!doesBucketExist(s3Client, bucketName)) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }

        // Parcourez les fichiers et uploadez-les
        for (MultipartFile imageFile : imageFiles) {
            String objectKey = folderName + "/" + Instant.now().toEpochMilli() + "_" + imageFile.getOriginalFilename();
            putS3Object(s3Client, bucketName, objectKey, imageFile);
            System.out.println("Image " + imageFile.getOriginalFilename() + " uploaded as " + objectKey);
        }

        s3Client.close();

        return new ResponseEntity<>(HttpStatus.OK);
    }


    public static void putS3Object(S3Client s3, String bucketName, String objectKey, MultipartFile imageFile) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3.putObject(putObjectRequest, RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize()));
            System.out.println("Successfully placed " + objectKey + " into bucket " + bucketName);
        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean doesBucketExist(S3Client s3Client, String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    private  S3Client createS3Client(){
        return S3Client.builder()
                .region(Region.EU_WEST_3)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }
}
