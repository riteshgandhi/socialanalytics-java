package com.socialanalytics.api.model;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterListener;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterBase {
    public AsyncTwitter twitter = null;
    private static final Object LOCK = new Object();

    public TwitterBase(String consumerKey, String consumerSecret, String accessToken,
                       String accessTokenSecret) {
        twitter = getTwitterInstanceAsync(consumerKey, consumerSecret, accessToken, accessTokenSecret);
    }

    public void addListener(TwitterListener listener) {
        twitter.addListener(listener);
    }

    public void asyncWait() throws InterruptedException {
        synchronized (LOCK) {
            LOCK.wait();
        }
    }

    public void asyncNotify() {
        synchronized (LOCK) {
            LOCK.notify();
        }
    }

    public interface TwitterFunction {
        public void execute(Object data) throws InterruptedException;
    }

    public class TimelineFunction implements TwitterFunction {
        @Override
        public void execute(Object data) throws InterruptedException {
            twitter.getUserTimeline(data.toString());
        }
    }

    public class UserFunction implements TwitterFunction {
        @Override
        public void execute(Object data) throws InterruptedException {
            twitter.searchUsers(data.toString(), 50);
        }
    }

    public void asyncMethod(TwitterFunction function, Object data) throws InterruptedException {
        function.execute(data);
        asyncWait();
    }

    private AsyncTwitter getTwitterInstanceAsync(String consumerKey, String consumerSecret, String accessToken,
                                                 String accessTokenSecret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

        AsyncTwitterFactory tf = new AsyncTwitterFactory(cb.build());
        return tf.getInstance();
    }
}
