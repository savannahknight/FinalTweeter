package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class BaseDAODynamo {
    protected static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-east-1")
            .build();
    protected static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

    protected static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }
}
