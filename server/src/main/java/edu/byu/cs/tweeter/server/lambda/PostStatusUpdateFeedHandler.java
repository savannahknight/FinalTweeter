package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import java.util.List;

import edu.byu.cs.tweeter.model.net.request.QueueFollowersRequest;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusUpdateFeedHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        System.out.println("Event" + event.toString());
        StatusService statusService = new StatusService(new DynamoDAOFactory());
        Gson gson = new Gson();
        List<SQSEvent.SQSMessage> messages = event.getRecords();
        for (SQSEvent.SQSMessage message : messages) {
            QueueFollowersRequest request = gson.fromJson(message.getBody(), QueueFollowersRequest.class);
            statusService.postStatusUpdateFeeds(request);
        }
        //get the list of followers from user who posted status and divide them
        //put message in the next queue for each sub section
        return null;
    }
}
