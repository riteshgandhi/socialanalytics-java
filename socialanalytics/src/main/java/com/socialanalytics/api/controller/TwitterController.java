package com.socialanalytics.api.controller;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socialanalytics.api.model.TimelineResponse;
import com.socialanalytics.api.model.TwitterAnalytics;

import twitter4j.ResponseList;
import twitter4j.User;

@RestController
@RequestMapping("/twtapi")
public class TwitterController {
	/**
	 * Gets User Timeline for the supplied keyword and for the date rage. Only the
	 * tweets having location are returned
	 * 
	 * @param consumerKey
	 *            consumer key
	 * @param consumerSecret
	 *            consumer secret
	 * @param accessToken
	 *            access token
	 * @param accessTokenSecret
	 *            access token secret
	 * @param userScreenName
	 *            search keyword. It can be partial or exact user screen name
	 * @param startDate
	 *            start date
	 * @param endDate
	 *            end date
	 * @return
	 * @throws InterruptedException
	 */
	@RequestMapping(value = "/searchTimelineWithLocation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<TimelineResponse>> getUserTimelineWithLocation(
			@RequestHeader(value = "consumerKey") String consumerKey,
			@RequestHeader(value = "consumerSecret") String consumerSecret,
			@RequestHeader(value = "accessToken") String accessToken,
			@RequestHeader(value = "accessTokenSecret") String accessTokenSecret, @RequestParam String userScreenName,
			@RequestParam String startDate, @RequestParam String endDate) throws InterruptedException {

		ArrayList<TimelineResponse> timelineResponses = null;

		try {
			TwitterAnalytics twitterAnalytics = new TwitterAnalytics(consumerKey, consumerSecret, accessToken,
					accessTokenSecret);
			timelineResponses = twitterAnalytics.getUsersTimeline(userScreenName, true);
		} catch (Exception e) {
			return new ResponseEntity<ArrayList<TimelineResponse>>(timelineResponses, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<ArrayList<TimelineResponse>>(timelineResponses, HttpStatus.OK);
	}

	/**
	 * Gets User Timeline of the user for the date rage
	 * 
	 * @param consumerKey
	 *            consumer key
	 * @param consumerSecret
	 *            consumer secret
	 * @param accessToken
	 *            access token
	 * @param accessTokenSecret
	 *            access token secret
	 * @param userScreenName
	 *            exact user screen name
	 * @param startDate
	 *            start date
	 * @param endDate
	 *            end date
	 * @return
	 */
	@RequestMapping(value = "/searchtimeline", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<TimelineResponse>> getUserTimeline(
			@RequestHeader(value = "consumerKey") String consumerKey,
			@RequestHeader(value = "consumerSecret") String consumerSecret,
			@RequestHeader(value = "accessToken") String accessToken,
			@RequestHeader(value = "accessTokenSecret") String accessTokenSecret, @RequestParam String userScreenName,
			@RequestParam String startDate, @RequestParam String endDate) throws InterruptedException {

		ArrayList<TimelineResponse> timelineResponses = null;

		try {
			TwitterAnalytics twitterAnalytics = new TwitterAnalytics(consumerKey, consumerSecret, accessToken,
					accessTokenSecret);
			timelineResponses = twitterAnalytics.getUsersTimeline(userScreenName, false);
		} catch (Exception e) {
			return new ResponseEntity<ArrayList<TimelineResponse>>(timelineResponses, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<ArrayList<TimelineResponse>>(timelineResponses, HttpStatus.OK);
	}

	/**
	 * Gets Users that matches supplied keyword
	 * 
	 * @param consumerKey
	 *            consumer key
	 * @param consumerSecret
	 *            consumer secret
	 * @param accessToken
	 *            access token
	 * @param accessTokenSecret
	 *            access token secret
	 * @param userScreenName
	 *            exact user screen name
	 * @param startDate
	 *            start date
	 * @param endDate
	 *            end date
	 * @return
	 */
	@RequestMapping(value = "/searchusers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseList<User>> getUsers(@RequestHeader(value = "consumerKey") String consumerKey,
			@RequestHeader(value = "consumerSecret") String consumerSecret,
			@RequestHeader(value = "accessToken") String accessToken,
			@RequestHeader(value = "accessTokenSecret") String accessTokenSecret, @RequestParam String keyword)
			throws InterruptedException {

		ResponseList<User> users = null;

		try {
			TwitterAnalytics twitterAnalytics = new TwitterAnalytics(consumerKey, consumerSecret, accessToken,
					accessTokenSecret);
			users = twitterAnalytics.searchUsers(keyword);
		} catch (Exception e) {
			return new ResponseEntity<ResponseList<User>>(users, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<ResponseList<User>>(users, HttpStatus.OK);
	}
}
