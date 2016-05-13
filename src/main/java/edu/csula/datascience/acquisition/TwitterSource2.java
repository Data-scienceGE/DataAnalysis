package edu.csula.datascience.acquisition;

import java.util.ArrayList;
import java.util.List;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterSource2{
	List<Status> statusCollection=new ArrayList<Status>();
	TwitterCollector collector=new TwitterCollector();
	public static TwitterStream getStream() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY[0]);
		cb.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET[0]);
		cb.setOAuthAccessToken(Constants.TWITTER_ACCESS_TOKEN[0]);
		cb.setOAuthAccessTokenSecret(Constants.TWITTER_ACCESS_SECRET[0]);
		return new TwitterStreamFactory(cb.build()).getInstance();
	}

	public void readTwitterFeed() {
		TwitterStream stream = getStream();
		StatusListener listener = new StatusListener() {

			@Override
			public void onException(Exception e) {
				System.out.println("Exception occured:" + e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onTrackLimitationNotice(int n) {
				System.out.println("Track limitation notice for " + n);
			}

			@Override
			public void onStatus(Status status) {
				System.out.println("Got twit:" + status.getText()
						+ " ** Time :" + status.getCreatedAt());
				if(statusCollection.size()==100){
					//send this collection for munging and empty the list
					collector.mungee(statusCollection);
					statusCollection=new ArrayList<Status>();
				}
				statusCollection.add(status);
				
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				System.out.println("Stall warning");
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				System.out.println("Scrub geo with:" + arg0 + ":" + arg1);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				System.out.println("Status deletion notice");
			}
		};
		FilterQuery qry = new FilterQuery();
		// String[] keywords = {};
		qry.track("#NowPlaying by");
		stream.addListener(listener);
		stream.filter(qry);
	}

	public static void main(String sr[]) {
		TwitterSource2 src2 = new TwitterSource2();
		src2.readTwitterFeed();
	}
}
