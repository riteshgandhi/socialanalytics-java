package com.socialanalytics.api.model;

import twitter4j.*;

import static twitter4j.TwitterMethod.SEARCH_USERS;

public class TwitterUser extends TwitterBase {
    public ResponseList<User> foundUsers;

    public TwitterUser(String consumerKey, String consumerSecret, String accessToken,
                       String accessTokenSecret) {
        super(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        addListener();  // add listener
    }

    private void addListener() {
        // add listener
        super.addListener(new TwitterAdapter() {
            @Override
            public void searchedUser(ResponseList<User> users) {
//                System.out.println("Successfully got users");
                foundUsers = users;
                asyncNotify();
            }

            @Override
            public void onException(TwitterException e, TwitterMethod method) {
                if (method == SEARCH_USERS) {
                    e.printStackTrace();
                    asyncNotify();
                } else {
                    asyncNotify();
                    throw new AssertionError("Should not happen");
                }
            }
        });
    }

    public void searchUsers(String keyword) throws InterruptedException {
        asyncMethod(new UserFunction(), keyword);
    }
}
