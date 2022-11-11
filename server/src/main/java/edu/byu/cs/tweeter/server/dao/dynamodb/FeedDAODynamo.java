package edu.byu.cs.tweeter.server.dao.dynamodb;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.dao.FeedDAO;

public class FeedDAODynamo extends BaseDAODynamo implements FeedDAO {
    private final String tableName = "Feed";
    Table table = dynamoDB.getTable(tableName);

    @Override
    public FeedResponse getFeed(FeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        Status lastItem = request.getLastItem();
        List<Status> responseStatuses = new ArrayList<>();

        QuerySpec querySpec3 = new QuerySpec();

        if (lastItem == null) {
            querySpec3.withHashKey(new KeyAttribute("user_alias", request.getUserAlias()))
                    .withScanIndexForward(false).withMaxResultSize(request.getLimit());
        } else {
            querySpec3.withHashKey(new KeyAttribute("user_alias", request.getUserAlias()))
                    .withScanIndexForward(false).withMaxResultSize(request.getLimit())
                    .withExclusiveStartKey(new PrimaryKey("user_alias", lastItem.getUser().getAlias(),
                            "timestamp", lastItem.getDate()));
        }


        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        try {
            items = table.query(querySpec3);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                String post = item.getString("message");
                String timestamp = item.getString("timestamp");
                List<String> mentions = (List<String>) item.get("mentions");
                List<String> urls = (List<String>) item.get("urls");
                String userJson = item.getString("user");
                System.out.println(item.getString("user_alias") + ": " + item.getString("timestamp"));

                Gson gson = new Gson();
                User user = gson.fromJson(userJson, User.class);

                responseStatuses.add(new Status(post, user, timestamp, urls, mentions));
            }

            Map<String, AttributeValue> lastReturnItem = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
            boolean hasMorePages = lastReturnItem != null;

            return new FeedResponse(responseStatuses, hasMorePages);
        } catch (Exception e) {
            System.err.println("Unable to query feed of " + request.getUserAlias());
            System.err.println(e.getMessage());
            throw new RuntimeException("[DBError] get feed failed for " + request.getUserAlias());
        }
    }

    @Override
    public boolean addStatusToFeed(List<String> followerAliases, Status status) {
        for(String userAlias : followerAliases) {
            try {
                System.out.println("Adding a new status to feed...");
                Gson gson = new Gson();
                String userJson = gson.toJson(status.getUser());
                PutItemOutcome outcome = table
                        .putItem(new Item().withPrimaryKey("user_alias", userAlias, "timestamp", status.getDate())
                                .withString("message", status.getPost()).withList("mentions", status.getMentions())
                                .withList("urls", status.getUrls()).withString("user", userJson));

                System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult().toString());

            } catch (Exception e) {
                System.err.println("Unable to add item: " + status + " to feed for user: " + userAlias);
                System.err.println(e.getMessage());
                throw new RuntimeException("[DBError] postStatus failed when propagating to user feeds");
            }
        }

        return true;
    }
}
