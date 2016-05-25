package edu.csula.datascience.models;

import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBList;

public class Track {
	
	long trackDuration;
	String trackId;
	int trackSpotifyPopularity;
	String trackName;
	Date trackDate;
	//List<Tweet> tweetInfo;
	BasicDBList tweetInfo;
	String artistName;
	AudioProperties audioProperties;  
	public long getTrackDuration() {
		return trackDuration;
	}
	public void setTrackDuration(long trackDuration) {
		this.trackDuration = trackDuration;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	
	public int getTrackSpotifyPopularity() {
		return trackSpotifyPopularity;
	}
	public void setTrackSpotifyPopularity(int trackSpotifyPopularity) {
		this.trackSpotifyPopularity = trackSpotifyPopularity;
	}
	public String getTrackName() {
		return trackName;
	}
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}
	
	/*public List<Tweet> getTweetInfo() {
		return tweetInfo;
	}
	public void setTweetInfo(List<Tweet> tweetInfo) {
		this.tweetInfo = tweetInfo;
	}*/
	
	public String getArtistName() {
		return artistName;
	}
	public BasicDBList getTweetInfo() {
		return tweetInfo;
	}
	public void setTweetInfo(BasicDBList tweetInfo) {
		this.tweetInfo = tweetInfo;
	}
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	public AudioProperties getAudioProperties() {
		return audioProperties;
	}
	public void setAudioProperties(AudioProperties audioProperties) {
		this.audioProperties = audioProperties;
	}
	public Date getTrackDate() {
		return trackDate;
	}
	public void setTrackDate(Date trackDate) {
		this.trackDate = trackDate;
	}
	
	
	
	
	
	
	
}
