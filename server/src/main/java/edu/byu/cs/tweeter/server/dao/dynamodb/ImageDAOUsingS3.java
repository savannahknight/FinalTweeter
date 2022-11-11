package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.server.dao.ImageDAO;

public class ImageDAOUsingS3 implements ImageDAO {
    @Override
    public String uploadImage(String image, String alias) {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build();

        byte[] imageArray = Base64.getDecoder().decode(image);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageArray);

        String bucketName = "cs340-savannah";

        ObjectMetadata metadata = new ObjectMetadata();

        metadata.addUserMetadata("Content-Type", "image/jpeg");

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, alias, bis, metadata);
            s3.putObject(request);

            return s3.getUrl(bucketName, alias).toString();
        } catch (SdkClientException e) {
            e.printStackTrace();
            return "Failed";
        }

    }
}
