package edu.csula.datascience.acquisition;

import twitter4j.Status;

import java.io.IOException;
import java.util.Collection;

/**
 * A simple example of using Twitter
 */
public class TwitterCollectorApp {
    public static void main(String[] args) throws IOException {
    	
       /* TwitterSource source = new TwitterSource(Long.MAX_VALUE, "#NowPlaying");
        TwitterCollector collector = new TwitterCollector();
       
        while (source.hasNext()) {
            Collection<Status> tweets = source.next();
            
            //Collection<Status> cleanedTweets = collector.mungee(tweets);
            //collector.save(cleanedTweets);
        }*/
    	StackExchangeSource source = new StackExchangeSource();
    	for (int year=2016; year>=2014;year--) {
    		source.setYear(year);
    		while(source.hasNext()) {
    			source.next();
    		}
    	}
    }
}
