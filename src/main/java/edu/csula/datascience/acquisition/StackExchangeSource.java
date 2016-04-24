package edu.csula.datascience.acquisition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
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

public class StackExchangeSource implements Source<Status>{
	int no_Of_Pages=1;
	int page=1;
	int year;
	FileWriter fw;
	List<JSONObject> list = new ArrayList<JSONObject>();

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
	public Collection<Status> next() {
		// TODO Auto-generated method stub
		JsonNode response = null;
		try {
			response = Unirest.get("https://api.discogs.com/database/search?secret=FtMuFxyFSIyYOyfRCjgWydFDHQcwEdZV&key=iggYrZPkbinxtVKwEuqP&year="+year+"&per_page=50&type=release&country=US&page="+page)
			         .header("Content-Type", "application/json")
			         .header("accept", "application/json")
			         .asJson()
			         .getBody();
			JSONObject rspns=response.getObject();
			//System.out.println("Response : "+rspns.toString());
	        JSONObject pagination = rspns.getJSONObject("pagination");
	        JSONArray results=rspns.getJSONArray("results");
	        JSONArray arr=new JSONArray();
	        JSONObject res = new JSONObject();
	        
	        for(int i=0;i<results.length();i++){
	        	JSONObject obj = results.getJSONObject(i);
	        	String[] title = obj.getString("title").split("-");
	        	String artitst = title[0].trim();
	        	String album = title[1].trim();
	        	URI uri = new URI("https", 
	        			"api.spotify.com", 
	        			"/v1/search",
	        			"q=album:\""+album+"\" artist:\""+artitst+"\"&type=track&limit=50", null);
/*	        			URL url = uri.toURL();
*/	        	
	        	response = Unirest.get(uri.toURL().toString())
				         .header("Content-Type", "application/json")
				         .header("accept", "application/json")
				         .asJson()
				         .getBody();
	        	System.out.println(response.getObject().toString() + "\n");
	        	arr.put(response.getObject()); 
	        	 
	        	
	        	
	        } 
	        res.put(String.valueOf(page), arr);
	        fw.flush();
	        fw.append(res.toString());
	        fw.flush();
	        page = pagination.getInt("page");
	        no_Of_Pages = pagination.getInt("pages");
	        
		} catch (Exception e ) {
			// TODO Auto-generated catch block
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			page--;
			
			e.printStackTrace();
			return Lists.newArrayList();

			
		}
	         
	         page++;
		return Lists.newArrayList();
	}	


	public void setYear(int year) {
		this.year = year;
		try {
			this.fw = new FileWriter(new File("D:/Courses/Spring 2016/BigData/workspace/data-science/data-collector-"+year+".json"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	
	
}

