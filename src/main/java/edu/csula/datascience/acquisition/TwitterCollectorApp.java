package edu.csula.datascience.acquisition;



import java.io.IOException;
import java.net.URISyntaxException;

/**
 * A simple example of using Twitter
 */
public class TwitterCollectorApp {
	
    public static void main(String[] args) throws IOException, URISyntaxException {
    	
    	/*FileReader reader=new FileReader("./tweets.txt");
    	BufferedReader br=new BufferedReader(reader);
    	String line=null;
    	long lastTweetId = 0;
       while((line=br.readLine())!=null){
    	   lastTweetId=Long.parseLong(line);
       }
       br.close();
       
       if(lastTweetId==0){
    	   lastTweetId=Long.MAX_VALUE;
       }*/
    	TwitterSource2 source = new TwitterSource2("#NowPlaying by");;
     //  TwitterCollector collector = new TwitterCollector();
      /* FileWriter writer=new FileWriter(("./tweets.txt"),true);*/
      /*  while (source.hasNext()) {
        	
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
        }*/
       source.readTwitterFeed();
       
      /* writer.close();*/
    	    }
}
