package com.socialanalytics.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socialanalytics.api.model.TimelineResponse;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

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
	 */
	@RequestMapping(value = "/searchTimelineWithLocation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<TimelineResponse>> getUserTimelineWithLocation(
			@RequestHeader(value = "consumerKey") String consumerKey,
			@RequestHeader(value = "consumerSecret") String consumerSecret,
			@RequestHeader(value = "accessToken") String accessToken,
			@RequestHeader(value = "accessTokenSecret") String accessTokenSecret, @RequestParam String userScreenName,
			@RequestParam String startDate, @RequestParam String endDate) {

		ArrayList<TimelineResponse> timelineResponses = null;

		Twitter twitter = getTwitterInstance(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		ResponseList<Status> responseList = null;
		try {
			timelineResponses = new ArrayList<>();
			ResponseList<User> users = twitter.searchUsers(userScreenName, 20);
			for (User twitterUser : users) {
				responseList = twitter.getUserTimeline(twitterUser.getScreenName());
				List<TimelineResponse> timelineResponseList = responseList.stream()
						.filter(status -> status.getGeoLocation() != null).map(status -> {
							TimelineResponse response = new TimelineResponse();
							response.message = status.getText();
							response.createdDate = status.getCreatedAt();
							response.placeName = status.getPlace().getFullName();
							if (status.getPlace() != null) {
								response.streetAddress = status.getPlace().getStreetAddress();
								response.countryCode = status.getPlace().getCountryCode();
								response.country = status.getPlace().getCountry();
							}
							response.latitude = status.getGeoLocation().getLatitude();
							response.longitude = status.getGeoLocation().getLongitude();
							return response;
						}).collect(Collectors.toList());
				timelineResponses.addAll(timelineResponseList);
			}
		} catch (TwitterException e) {
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
	public ResponseEntity<List<TimelineResponse>> getUserTimeline(
			@RequestHeader(value = "consumerKey") String consumerKey,
			@RequestHeader(value = "consumerSecret") String consumerSecret,
			@RequestHeader(value = "accessToken") String accessToken,
			@RequestHeader(value = "accessTokenSecret") String accessTokenSecret, @RequestParam String userScreenName,
			@RequestParam String startDate, @RequestParam String endDate) {

		List<TimelineResponse> timelineResponseList = null;

		Twitter twitter = getTwitterInstance(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		ResponseList<Status> responseList = null;
		try {
			responseList = twitter.getUserTimeline(userScreenName);
			timelineResponseList = responseList.stream().map(status -> {
				TimelineResponse response = new TimelineResponse();
				response.message = status.getText();
				response.createdDate = status.getCreatedAt();
				response.placeName = status.getPlace().getFullName();
				if (status.getPlace() != null) {
					response.streetAddress = status.getPlace().getStreetAddress();
					response.countryCode = status.getPlace().getCountryCode();
					response.country = status.getPlace().getCountry();
				}
				response.latitude = status.getGeoLocation().getLatitude();
				response.longitude = status.getGeoLocation().getLongitude();
				return response;
			}).collect(Collectors.toList());

		} catch (TwitterException e) {
			return new ResponseEntity<List<TimelineResponse>>(timelineResponseList, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<List<TimelineResponse>>(timelineResponseList, HttpStatus.OK);
	}

	/**
	 * Returns all the tweets for the supplied keyword
	 * 
	 * @param consumerKey
	 *            consumer key
	 * @param consumerSecret
	 *            consumer secret
	 * @param accessToken
	 *            access token
	 * @param accessTokenSecret
	 *            access token secret
	 * @param keyword
	 *            search keyword
	 * @param maxResultCount
	 *            max result size. default is 100
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<QueryResult> search(@RequestHeader(value = "consumerKey") String consumerKey,
			@RequestHeader(value = "consumerSecret") String consumerSecret,
			@RequestHeader(value = "accessToken") String accessToken,
			@RequestHeader(value = "accessTokenSecret") String accessTokenSecret, @RequestParam String keyword,
			@RequestParam(defaultValue = "100") int maxResultCount) {

		Twitter twitter = getTwitterInstance(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		QueryResult responseList = null;
		try {
			Query query = new Query(keyword);
			query.setCount(maxResultCount);
			responseList = twitter.search(query);
		} catch (TwitterException e) {
			return new ResponseEntity<QueryResult>(responseList, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<QueryResult>(responseList, HttpStatus.OK);
	}

	private Twitter getTwitterInstance(String consumerKey, String consumerSecret, String accessToken,
			String accessTokenSecret) {
		ConfigurationBuilder cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

		TwitterFactory tf = new TwitterFactory(cb.build());
		return tf.getInstance();
	}
}
