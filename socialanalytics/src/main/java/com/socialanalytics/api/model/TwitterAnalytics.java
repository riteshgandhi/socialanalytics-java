package com.socialanalytics.api.model;

import twitter4j.ResponseList;
import twitter4j.User;

import java.util.ArrayList;

public class TwitterAnalytics {
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    public TwitterAnalytics(String consumerKey, String consumerSecret, String accessToken,
                            String accessTokenSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
    }

    public ArrayList<TimelineResponse> getUserTimeLine(String userScreenName,
                                                       boolean filterForLocation) throws InterruptedException {
        Timeline timeline = new Timeline(filterForLocation,consumerKey, consumerSecret, accessToken, accessTokenSecret);
        timeline.getUserTimeline(userScreenName);
        ArrayList<TimelineResponse> timelineResponses = timeline.timelineResponses;
        return timelineResponses;
    }

    public ResponseList<User> searchUsers(String keyword) throws InterruptedException {
        TwitterUser user = new TwitterUser(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        user.searchUsers(keyword);
        return user.foundUsers;
    }

    public ArrayList<TimelineResponse> getUsersTimeline(String keyword, boolean filterForLocation) throws InterruptedException {
        ArrayList<TimelineResponse> timelineResponses = new ArrayList<>();
        ResponseList<User> users = this.searchUsers(keyword);
        for (User user: users) {
            ArrayList<TimelineResponse> responses =
                    this.getUserTimeLine(user.getScreenName(), filterForLocation);
            timelineResponses.addAll(responses);
        }
        return timelineResponses;
    }


    public static void main(String[] args) throws InterruptedException {
        String consumerKey = "";
        String consumerSecret = "";
        String accessToken = "";
        String accessTokenSecret = "";

        TwitterAnalytics twitterAnalytics = new TwitterAnalytics(consumerKey, consumerSecret, accessToken, accessTokenSecret);

        ArrayList<TimelineResponse> timelineResponses = twitterAnalytics.getUsersTimeline("shreyas ravi", true);
        System.out.println(timelineResponses.size());

        timelineResponses = twitterAnalytics.getUsersTimeline("donald trump", false);
        System.out.println(timelineResponses.size());
        
    }
}
