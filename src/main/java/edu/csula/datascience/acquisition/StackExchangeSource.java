package edu.csula.datascience.acquisition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Status;

import com.google.common.collect.Lists;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.jndi.toolkit.url.Uri;

import edu.csula.datascience.models.Discog;

public class StackExchangeSource implements Source<Discog>{
	int no_Of_Pages=1;
	int page=1;
	int year;
	FileWriter fw;
	//List<JSONObject> list = new ArrayList<JSONObject>();

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		if (page <= no_Of_Pages)
			return true;
		page = 1;
		no_Of_Pages=1;
		
		return false;
	}
	@Override
	public Collection<Discog> next() {
		// TODO Auto-generated method stub
		JsonNode response = null;
		List<Discog> list = Lists.newArrayList();
		
		try {
			response = Unirest.get("https://api.discogs.com/database/search?secret=FtMuFxyFSIyYOyfRCjgWydFDHQcwEdZV&key=iggYrZPkbinxtVKwEuqP&year="+year+"&per_page=50&type=release&country=US&page="+page)
			         .header("Content-Type", "application/json")
			         .header("accept", "application/json")
			         .asJson()
			         .getBody();
			JSONObject obj = response.getObject();
			
			JSONObject pagination = obj.getJSONObject("pagination");
			page = pagination.getInt("page");
			no_Of_Pages = pagination.getInt("pages");
			
			JSONArray results = obj.getJSONArray("results");
			
			for(int i =0; i <results.length();i++){
				Discog d = new Discog();
				
				JSONObject resultObj = results.getJSONObject(i);
				d.setTitle(resultObj.getString("title"));
				d.setGenre(resultObj.getJSONArray("genre"));
				d.setYear(year);
				d.setStyle(resultObj.getJSONArray("style"));
				
				
				/*String title [] = resultObj.getString("title").split("-");
				String artist = title[0].trim();
				String album = title[1].trim();
				
				URI uri = new URI("https", 
	        			"api.spotify.com", 
	        			"/v1/search",
	        			"q=album:\""+album+"\" artist:\""+artist+"\"&type=track&limit=50", null);
	        	
	        	response = Unirest.get(uri.toURL().toString())
				         .header("Content-Type", "application/json")
				         .header("accept", "application/json")
				         .asJson()
				         .getBody();
	        	
	        	d.setSpotify(response.getObject());
				d.setArtist(artist);
				d.setAlbum(album);
				d.setStyle(resultObj.getJSONArray("style"));
				d.setGenre(resultObj.getJSONArray("genre"));
				d.setYear(String.valueOf(year));
*/				
				list.add(d);
				
				System.out.println("Page : "+page+ " " +d.getTitle());
				Thread.sleep(20);		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			page --;
			return null;
		}
	         
	    page++;
		return list;
	}	

	

	public void setYear(int year) {
		this.year = year;
		
	}
	
	public Collection<Discog> getTracks(Collection<Discog> albums) {
		List<Discog> tracks = Lists.newArrayList();
		JsonNode response = null;
		for (Discog a : albums) {
			URI uri;
			Discog albumTracks = new Discog();
			try {
				uri = new URI("https", 
						"api.spotify.com", 
						"/v1/search",
						"q=album:\""+a.getAlbum()+"\" artist:\""+a.getArtist()+"\"&type=track&limit=50", null);
				response = Unirest.get(uri.toURL().toString())
				         .header("Content-Type", "application/json")
				         .header("accept", "application/json")
				         .asJson()
				         .getBody();
				
				albumTracks.setSpotify(response.getObject());
				albumTracks.setGenre(a.getGenre());
				albumTracks.setStyle(a.getStyle());
				albumTracks.setYear(a.getYear());
				albumTracks.setAlbum(a.getAlbum());
				albumTracks.setArtist(a.getArtist());
				tracks.add(albumTracks);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("get tracks" +tracks.size());
		return tracks;
	}



	
	
}

