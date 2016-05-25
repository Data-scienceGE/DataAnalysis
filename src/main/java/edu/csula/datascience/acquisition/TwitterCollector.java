package edu.csula.datascience.acquisition;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import edu.csula.datascience.models.Track;
import edu.csula.datascience.models.Tweet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.bson.Document;
import org.bson.conversions.Bson;

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

		// Lucene Analyzer
		Analyzer analyzer = new StopAnalyzer(Version.LUCENE_36);

		List<Track> tracks = new ArrayList<Track>();
		for (Status status : src) {

			String dirtyStatus = status.getText();

			dirtyStatus = dirtyStatus.replaceAll("(@)\\S+", "");

			Pattern p = Pattern.compile(Pattern.quote("NowPlaying") + "(.*?)" + Pattern.quote("by"));
			Matcher m = p.matcher(dirtyStatus);
			String trackName = null;

			while (m.find()) {

				trackName = ("\t" + m.group(1));

			}
			if (trackName != null) {
				trackName = trackName.replace("*", "\\*");
				trackName = trackName.replace("?", "\\?");
				trackName = trackName.replace("(", "\\(");
				trackName = trackName.replace(")", "\\)");
				trackName = trackName.replace("+", "\\+");
				trackName = trackName.replace("[", "\\[");
				trackName = trackName.replace("]", "\\]");

			}
			
			if (trackName != null && trackName.contains("#")) {
				trackName = trackName.replaceAll("#[A-Za-z]+", "");

			}
			if (trackName != null && trackName.contains(":")) {
				trackName = trackName.replaceAll(":", "");

				// set the trackName here when we make object
			}
			if (trackName != null && trackName.contains("http"))
				trackName = trackName.replaceAll("(http)\\s+", "");
			if (trackName != null && trackName.contains("-")) {
				trackName = trackName.substring(0, trackName.lastIndexOf("-"));
			}
			// get the dirtyStatus and extract the part after by
			// System.out.println(status.getText());
			if (trackName != null) {

				// get lastindex of by and https

				int indexOfby = dirtyStatus.lastIndexOf("by");
				int indexOfHttps = dirtyStatus.indexOf("https", indexOfby);
				if (indexOfby != -1 && indexOfHttps != -1)

					dirtyStatus = dirtyStatus.substring(indexOfby, indexOfHttps);
				// remove hashtags
				dirtyStatus = dirtyStatus.replaceAll("#[A-Za-z]+", "");

				String[] artistDelimiter = { "?", "from", "on", "in", "-", "|", "#", "@" };
				for (int i = 0; i < artistDelimiter.length; i++) {
					if (dirtyStatus.contains(artistDelimiter[i])) {
						dirtyStatus = dirtyStatus.substring(0, (dirtyStatus.indexOf(artistDelimiter[i])));
						break;
					}
				}

				dirtyStatus = dirtyStatus.replace("by", "");
				String artistName = dirtyStatus;
				
				char[] arr=artistName.toCharArray();
				String finalArtist="";
				for(int i=0;i<arr.length;i++){
					if((int)arr[i]==9835){
						continue;
					}
					finalArtist+=arr[i];
				}
				artistName=finalArtist.trim();
				System.out.println("Artist Name: "+artistName);
				if (trackName != null && artistName != null && trackName.length() > 0 && artistName.length() > 0) {
					//get track by track name and artist name from mongo and if record already exist than 
					//just add the tweet info in the track.
					if (artistName != null) {
						artistName = artistName.replace("*", "\\*");
						artistName = artistName.replace("?", "\\?");
						artistName = artistName.replace("(", "\\(");
						artistName = artistName.replace(")", "\\)");
						artistName = artistName.replace("+", "\\+");
						artistName = artistName.replace("[", "\\[");
						artistName = artistName.replace("]", "\\]");

					}
					System.out.println("Looking for track: "+trackName+" and Artist: "+artistName);
					BasicDBObject doc=new BasicDBObject();
					QueryBuilder query=new QueryBuilder();
					trackName="Boomerang";
					artistName ="Lali";
					Pattern regex1 = Pattern.compile(trackName); 
					Pattern regex2 = Pattern.compile(artistName); 
					query.and(new QueryBuilder().put("trackName").is(regex1).get(),new QueryBuilder().put("artistName").is(regex2).get());
					doc.putAll(query.get());
					FindIterable iterator=collection.find(doc);
					BasicDBObject tweetobj = new BasicDBObject();
					tweetobj.put("likes", status.getFavoriteCount());
					tweetobj.put("retweets", status.getRetweetCount());
					tweetobj.put("userID", status.getUser().getId());
					tweetobj.put("createdAt", status.getUser().getCreatedAt());
					if(iterator.first()==null){
					MongoCursor cursor=iterator.iterator();
					
					Track track = new Track();
					BasicDBList tweet_info = new BasicDBList(); 
					Tweet tweet = new Tweet();
					tweet.setTweetId(status.getId());
					// tweet.setCreatedAt(status.getCreatedAt().toString());
					tweet.setLikes(status.getFavoriteCount());
					tweet.setRetweets(status.getRetweetCount());
					tweet.setUser(status.getUser());
					
					tweet_info.add(tweetobj);
					track.setTrackName(trackName);
					List<Tweet> tweetsList=new ArrayList<Tweet>();
					tweetsList.add(tweet);
					track.setArtistName(artistName);
					track.setTweetInfo(tweet_info);
					//track.setTweetInfo(tweetsList);
					//track.setTweetInfo(tweet);
					tracks.add(track);
					}else{
						MongoCursor<Document> c = iterator.iterator();
						Document d=c.next();
						ArrayList list=new ArrayList();
						list=(ArrayList) d.get("tweetInfo");
						list.add(tweetobj);
						System.out.println("Array List now:"+list);
						d.replace("tweetInfo", list);
						collection.replaceOne(doc, d);
						
						 						
					}
				}
			}

		}

		return tracks;
	}

@Override
	public void save(Collection<Track> fetchedSongs) {
		try {
			List<Document> documents = fetchedSongs.stream()

					.map(item -> new Document()
							.append("trackId", item.getTrackId())
							.append("trackName", item.getTrackName())
							.append("artistName", item.getArtistName())
							.append("duration", item.getTrackDuration())
							.append("trackPopularity", item.getTrackSpotifyPopularity())
							.append("audioProperties", new BasicDBObject().append("loudness", item.getAudioProperties().getLoudness())
									
														.append("liveness", item.getAudioProperties().getLiveness())
														.append("tempo", item.getAudioProperties().getTempo())
														.append("valence", item.getAudioProperties().getValence())
														.append("instrumentalness", item.getAudioProperties().getInstrumentalness())
														.append("danceability", item.getAudioProperties().getDanceability())
														.append("speechiness", item.getAudioProperties().getSpeechiness())
														.append("mode", item.getAudioProperties().getMode())
														.append("acousticness", item.getAudioProperties().getAcousticness())
														.append("energy", item.getAudioProperties().getEnergy()))
									.append("tweetInfo", item.getTweetInfo() ))

					.collect(Collectors.toList());

			collection.insertMany(documents);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
