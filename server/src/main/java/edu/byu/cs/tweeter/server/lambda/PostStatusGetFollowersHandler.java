package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusGetFollowersHandler implements RequestHandler<SQSEvent, Void>{
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        StatusService statusService = new StatusService(new DynamoDAOFactory());
        Gson gson = new Gson();
        PostStatusRequest request = gson.fromJson(event.getRecords().get(0).getBody(), PostStatusRequest.class);
        statusService.postStatusGetFollowers(request);
        return null;
    }
}
