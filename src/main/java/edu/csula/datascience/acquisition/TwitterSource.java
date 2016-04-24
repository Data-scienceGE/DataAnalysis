package edu.csula.datascience.acquisition;

import com.google.common.collect.Lists;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * An example of Source implementation using Twitter4j api to grab tweets
 */
public class TwitterSource implements Source<Status> {
	private long minId;
	private final String searchQuery;
	ConfigurationBuilder cb;
	TwitterFactory tf;
	Twitter twitter;
	int count = 1;
	int i = 0;
	FileWriter fw;
	public TwitterSource(long minId, String query) throws IOException {
		this.minId = minId;
		this.searchQuery = query;
		this.fw=new FileWriter(new  File("D:\\Courses\\Spring 2016\\BigData\\workspace\\data-science\\data-collector.json"));
	}

	@Override
	public boolean hasNext() {
		return minId > 0;
	}

	@Override
	public Collection<Status> next() {
		List<Status> list = Lists.newArrayList();
		System.out.println("i value: "+i);
		// twitter setup
		cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY[i])
				.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET[i])
				.setOAuthAccessToken(Constants.TWITTER_ACCESS_TOKEN[i])
				.setOAuthAccessTokenSecret(Constants.TWITTER_ACCESS_SECRET[i]);
		tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();

		// twitter query making to fetch tweets
		Query query = new Query(searchQuery);
		
		
		query.setCount(100);
		if (minId != Long.MAX_VALUE) {
			query.setMaxId(minId);
		}

		// save tweets in the list
		try{
			list.addAll(getTweets(twitter, query));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private List<Status> getTweets(Twitter twitter, Query query)
			throws IOException {
		QueryResult result;
		List<Status> list = Lists.newArrayList();
		int count=1;
		try {
			
			do {
				
				result = twitter.search(query);
				//System.out.println("No of Results Obtained :"+result.getMaxId());
				
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					
					minId = Math.min(minId, tweet.getId());
					System.out.println("tweet id:"+tweet.getId()+" Status:"+tweet.getText());
					fw.append("tweet id:"+tweet.getId()+" Status:"+tweet.getText()+"\n");
					
				}
				
				
			} while ((query = result.nextQuery()) != null);
			
		} catch (TwitterException e) {
			// Catch exception to handle rate limit and retry
			e.printStackTrace();
			System.out.println("catch happening");
			i++;
			if (i>=6) {
				try {
					System.out.println("Remaining time to Reset is: "+ e.getRateLimitStatus().getSecondsUntilReset());
					Thread.sleep((e.getRateLimitStatus().getSecondsUntilReset()+ (5000)));
					count = 1;
					i = 0;
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
				
				System.out.println("Incrementing i to " +i);
				//this.next();
				
			
			System.out
					.println("Got twitter exception. Current min id " + minId);
			System.out.println("Counter IS: "+count);
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			
			
			
			//getTweets(twitter, query);

			
		}

		return list;
	}
}
