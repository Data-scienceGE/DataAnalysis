package edu.csula.datascience.models;

import java.util.List;

public class Track {
	long trackDuration;
	String trackId;
	int trackSpotifyPopularity;
	String trackName;
	Tweet tweetInfo;    //This should contain all the tweets for the particular track
							//we can identify this by track id from spotify
	String artistName;
	AudioProperties audioProperties;  //here we need to create one more model class based on spotify api response and change the type
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
	
	public Tweet getTweetInfo() {
		return tweetInfo;
	}
	public void setTweetInfo(Tweet tweetInfo) {
		this.tweetInfo = tweetInfo;
	}
	public String getArtistName() {
		return artistName;
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
	
	
	
	
	
}
