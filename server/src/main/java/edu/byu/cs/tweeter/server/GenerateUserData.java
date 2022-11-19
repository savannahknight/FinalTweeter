package edu.byu.cs.tweeter.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.dynamodb.DynamoDAOFactory;

public class GenerateUserData {
    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
    private final static int NUM_USERS = 11;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@sav";
    private final static List<Status> allStatuses = new ArrayList<>();

    public static void main(String args[]) {
        // Get instance of DAOs by way of the Abstract Factory Pattern
        UserDAO userDAO = new DynamoDAOFactory().getUserDAO();
        FollowDAO followDAO = new DynamoDAOFactory().getFollowDAO();
        StatusDAO statusDAO = new DynamoDAOFactory().getStatusDAO();

        List<User> followers = new ArrayList<>();
        List<User> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 1; i <= NUM_USERS; i++) {

            String firstName = "first " + i;
            String lastName = "last " + i;
            String alias = "@user" + i;
            String image = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png";

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            User user = new User(firstName, lastName, alias, image);
            user.setAlias(alias);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setImage(image);
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(user);
        }

        // Call the DAOs for the database logic
        if (users.size() > 0) {
            userDAO.addUserBatch(users);
        }
        if (followers.size() > 0) {
            followDAO.addFollowersBatch(followers, FOLLOW_TARGET);
        }

        allStatuses.clear();

        Calendar calendar = new GregorianCalendar();

        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < users.size(); ++j) {
                User sender = users.get(j);
                User mention = ((j < users.size() - 1) ? users.get(j + 1) : users.get(0));
                List<String> mentions = Collections.singletonList(mention.getAlias());
                String url = "https://byu.edu";
                List<String> urls = Collections.singletonList(url);
                String post = "Post " + i + " " + j +
                        "\nMy friend " + mention.getAlias() + " likes this website" +
                        "\n" + url;
                calendar.add(Calendar.MINUTE, 1);
                String datetime = calendar.getTime().toString();
                Status status = new Status(post, sender, datetime, urls, mentions);
                allStatuses.add(status);
            }
        }

//        if(allStatuses.size() > 0){
//            for(Status status : allStatuses){
//                statusDAO.postStatus(status)
//            }
//
//        }
    }
}
