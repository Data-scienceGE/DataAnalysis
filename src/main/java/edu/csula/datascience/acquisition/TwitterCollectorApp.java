package edu.csula.datascience.acquisition;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import twitter4j.Status;
import edu.csula.datascience.models.Track;

/**
 * A simple example of using Twitter
 */
public class TwitterCollectorApp {
	
    public static void main(String[] args) throws IOException, URISyntaxException {
    	
    	FileReader reader=new FileReader("./tweets.txt");
    	BufferedReader br=new BufferedReader(reader);
    	String line=null;
    	long lastTweetId = 0;
       while((line=br.readLine())!=null){
    	   lastTweetId=Long.parseLong(line);
       }
       br.close();
       
       if(lastTweetId==0){
    	   lastTweetId=Long.MAX_VALUE;
       }
    	TwitterSource source = new TwitterSource(lastTweetId, "((#NowPlaying)OR (#NowListening)) AND (by)");
       TwitterCollector collector = new TwitterCollector();
       FileWriter writer=new FileWriter(("./tweets.txt"),true);
        while (source.hasNext()) {
        	
            ArrayList<Status> tweets = (ArrayList<Status>) source.next();
            source.finalList=new ArrayList<Status>();
            System.out.println("\t\t\t REceived Tweets size:"+tweets.size());
            writer.append(String.valueOf(source.minId)+"\n");
          
            writer.flush();
            
            System.out.println("sending tweets for munging");
            Collection<Track> cleanedTweets = collector.mungee(tweets);
            System.out.println("Out of 100 we get, cleaned: "+cleanedTweets.size());
            System.out.println("Received Track and Artist Name at Main");
            System.out.println("Calling Spotify API");
            Collection<Track> fetchedSongs =source.fetchSpotify(cleanedTweets);
            System.out.println("Received Spotify data");
            
            System.out.println("We Received Information for "+fetchedSongs.size()+" among "+cleanedTweets.size());
            //pass the above fethced songs to fetch song properties
            
            //source.fetchAudioProperties(fetchedSongs);
            //pass the above collection to youtube api.
            collector.save(fetchedSongs);
            
            
            
            
            //collector.save(cleanedTweets);
        }writer.close();
    	    }
}
