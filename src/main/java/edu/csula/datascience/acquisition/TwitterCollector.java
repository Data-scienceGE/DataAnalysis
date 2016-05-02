package edu.csula.datascience.acquisition;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.csula.datascience.models.Track;
import edu.csula.datascience.models.Tweet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.bson.Document;

import twitter4j.Status;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * An example of Collector implementation using Twitter4j with MongoDB Java
 * driver
 */
public class TwitterCollector implements Collector<Track, Status> {
	MongoClient mongoClient;
	MongoDatabase database;
	MongoCollection<Document> collection;

	public TwitterCollector() {
		// establish database connection to MongoDB
		mongoClient = new MongoClient();

		// select `bd-example` as testing database
		database = mongoClient.getDatabase("bd-example");

		// select collection by name `tweets`
		collection = database.getCollection("tracks");
	}

	@Override
	public Collection<Track> mungee(Collection<Status> src) {
		// go through each src and extract text
		
		//Lucene Analyzer
		 Analyzer analyzer = new StopAnalyzer(Version.LUCENE_36);
		  
		
		List<Track> tracks = new ArrayList<Track>();
		for (Status status : src) {

			String dirtyStatus = status.getText();
			System.out.println(dirtyStatus);
			dirtyStatus = dirtyStatus.replaceAll("(@)\\S+", "");

			Pattern p = Pattern.compile(Pattern.quote("NowPlaying") + "(.*?)"
					+ Pattern.quote("by"));
			Matcher m = p.matcher(dirtyStatus);
			String trackName = null;

			while (m.find()) {

				trackName = ("\t" + m.group(1));

			}
			if (trackName != null && trackName.contains("#")) {
				trackName = trackName.replaceAll("#[A-Za-z]+", "");

			}
			if (trackName != null && trackName.contains(":")) {
				trackName = trackName.replaceAll(":", "");
				
				
				// set the trackName here when we make object
			}
			if(trackName != null && trackName.contains("http"))
			trackName=trackName.replaceAll("(http)\\s+", "");
			if(trackName != null && trackName.contains("-")){
				trackName=trackName.substring(0, trackName.lastIndexOf("-"));
			}
			// get the dirtyStatus and extract the part after by
			// System.out.println(status.getText());
			if (trackName != null) {

				// get lastindex of by and https

				int indexOfby = dirtyStatus.lastIndexOf("by");
				int indexOfHttps = dirtyStatus.indexOf("https", indexOfby);
				if (indexOfby != -1 && indexOfHttps != -1)

					dirtyStatus = dirtyStatus
							.substring(indexOfby, indexOfHttps);
				// remove hashtags
				dirtyStatus = dirtyStatus.replaceAll("#[A-Za-z]+", "");

				String[] artistDelimiter = { "?", "from", "on", "in", "-", "|",
						"#", "@" };
				for (int i = 0; i < artistDelimiter.length; i++) {
					if (dirtyStatus.contains(artistDelimiter[i])) {
						dirtyStatus = dirtyStatus.substring(0,
								(dirtyStatus.indexOf(artistDelimiter[i])));
						break;
					}
				}
				
				dirtyStatus = dirtyStatus.replace("by","");
				String artistName="";
				/* TokenStream tokenStream = analyzer.tokenStream("contents", 
					      new StringReader(dirtyStatus));
					   TermAttribute term = tokenStream.addAttribute(TermAttribute.class);
					   try {
						while(tokenStream.incrementToken()) {
						     artistName+=term.term()+" ";
						   }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				dirtyStatus=artistName.trim();	*/ 
				if(trackName !=null && dirtyStatus!=null && trackName.length()>0 && dirtyStatus.length()>0){
				Track track = new Track();
				Tweet tweet = new Tweet();
				tweet.setTweetId(status.getId());
				//tweet.setCreatedAt(status.getCreatedAt().toString());
				tweet.setLikes(status.getFavoriteCount());
				tweet.setRetweets(status.getRetweetCount());
				tweet.setUser(status.getUser());
				track.setTrackName(trackName);
				System.out.println("trackName: "+trackName.trim());
				System.out.println("Artist Name: "+dirtyStatus.trim());
				track.setArtistName(dirtyStatus);
				track.setTweetInfo(tweet);
				tracks.add(track);
				}
			}

		}

		return tracks;
	}

	@Override
	public void save(Collection<Track> fetchedSongs) {
		try {
		List<Document> documents = fetchedSongs
				.stream()
				
				.map(item -> new Document().append("trackId", item.getTrackId())
						.append("artistName", item.getArtistName())
						.append("trackDuration", item.getTrackDuration())
						.append("trackName", item.getTrackName())
						.append("spotifyPopularity", item.getTrackSpotifyPopularity())
						.append("tweetId",item.getTweetInfo().getTweetId())
						.append("createdAt",item.getTweetInfo().getCreatedAt())
						.append("tweetLikes",item.getTweetInfo().getLikes())
						.append("retweets",item.getTweetInfo().getRetweets())
						.append("status",item.getTweetInfo().getStatus())
						.append("tweetId",item.getTweetInfo().getTweetId())
						.append("userLocation",item.getTweetInfo().getUser().getLocation()))
						
						
				.collect(Collectors.toList());

		collection.insertMany(documents);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
