package edu.csula.datascience.acquisition;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import edu.csula.datascience.models.Discog;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import twitter4j.Status;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An example of Collector implementation using Twitter4j with MongoDB Java driver
 */
public class TwitterCollector implements Collector<Discog, Discog> {
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
    public Collection<Discog> DiscogMungee(Collection<Discog> src) {
    	
    	for (Iterator<Discog> iterator = src.iterator(); iterator.hasNext();) {
    	    Discog d = iterator.next();
    	    String [] title = d.getTitle().split("-");
    	    if (title.length <2) {
    	        // Remove the current element from the iterator and the list.
    	        iterator.remove();
    	    } else {
    	    	d.setAlbum(title[1]);
    	    	d.setArtist(title[0]);
    	    }
    	    
    	}
    	
        return src;
    }

    @Override
    public void save(Collection<Discog> data) {
    	List<Document> documents = data.stream()
    			.map(item-> new Document()
    					.append("trackId", item.getTrackId())
    					.append("trackName", item.getTrackName())
    					.append("year", item.getYear())).
    			collect(Collectors.toList());
        /*List<Document> documents = data.stream()
            .map(item -> new Document()
                .append("tweetId", item.getTrackId())
                .append("username", item.getUser().getName())
                .append("lang", item.getLang())
                .append("source", item.getSource()))
            .collect(Collectors.toList());*/

    	//collection.
    	//collection.ensureIndex({ "username": 1 }, { unique: true } );
        collection.insertMany(documents);
        //collection.updateMany(    new BasicDBObject("_id", "12"), new UpdateOptions().upsert(true));
        
    }
	@Override
	public Collection<Discog> TracksMungee(Collection<Discog> src) {
		List<Discog> tracksList = Lists.newArrayList();
		
		for(Discog d : src) {
			try {
				JSONObject tracks = d.getSpotify().getJSONObject("tracks");
				if(tracks.getInt("total") != 0) {
					JSONArray items = tracks.getJSONArray("items");
					for(int i=0;i<items.length();i++) {
						Discog track = new Discog();
						track.setAlbum(d.getAlbum());
						track.setTrack_artists(items.getJSONObject(i).getJSONArray("artists"));
						track.setGenre(d.getGenre());
						track.setTrackId(items.getJSONObject(i).getString("id"));
						track.setTrackName(items.getJSONObject(i).getString("name"));
						track.setPopularity(items.getJSONObject(i).getInt("popularity"));
						track.setDuration(items.getJSONObject(i).getLong("duration_ms"));
						tracksList.add(track);	
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		System.out.println("TracksList "+ tracksList.size());
		return tracksList ;
	}
}
