package com.socialanalytics.api.model;

import twitter4j.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static twitter4j.TwitterMethod.USER_TIMELINE;

public class Timeline extends TwitterBase {
    public ArrayList<TimelineResponse> timelineResponses = new ArrayList<>();
    private boolean showOnlyLocationEnabled;

    public Timeline(boolean showOnlyLocationEnabled, String consumerKey, String consumerSecret, String accessToken,
                    String accessTokenSecret) {
        super(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        this.showOnlyLocationEnabled = showOnlyLocationEnabled;
        addListener();  // add listener
    }

    private void addListener() {
        // add listener
        super.addListener(new TwitterAdapter() {
            @Override
            public void gotUserTimeline(ResponseList<Status> statuses) {
//                System.out.println("Successfully got timeline");
                List<TimelineResponse> timelineResponseList = statuses.stream().map(status -> {
                    TimelineResponse response = new TimelineResponse();
                    response.userScreenName = status.getUser().getScreenName();
                    response.userName = status.getUser().getName();
                    response.message = status.getText();
                    response.createdDate = status.getCreatedAt();
                    if (status.getPlace() != null) {
                        response.placeName = status.getPlace().getFullName();
                        response.streetAddress = status.getPlace().getStreetAddress();
                        response.countryCode = status.getPlace().getCountryCode();
                        response.country = status.getPlace().getCountry();
                        response.latitude = status.getGeoLocation().getLatitude();
                        response.longitude = status.getGeoLocation().getLongitude();
                    }
                    return response;
                }).collect(Collectors.toList());

                if (showOnlyLocationEnabled == true) {
                    List<TimelineResponse> filtered = timelineResponseList.stream().
                            filter(timelineResponse -> timelineResponse.latitude > 0).
                            collect(Collectors.toList());
                    timelineResponseList = filtered;
                }

                timelineResponses.addAll(timelineResponseList);
                asyncNotify();
            }

            @Override
            public void onException(TwitterException e, TwitterMethod method) {
                if (method == USER_TIMELINE) {
                    e.printStackTrace();
                    asyncNotify();
                } else {
                    asyncNotify();
                    throw new AssertionError("Should not happen");
                }
            }
        });
    }

    public void getUserTimeline(String screenName) throws InterruptedException {
        asyncMethod(new TimelineFunction(), screenName);
    }
}
