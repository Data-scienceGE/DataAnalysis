package edu.csula.datascience.models;

import org.json.JSONArray;
import org.json.JSONObject;

public class Discog {
	public String title,country, artist, album;
	public int year;
	public JSONArray style;
	public JSONArray genre;
	public JSONObject spotify;
	public String trackId;
	public String trackName;
	public JSONArray track_artists;
	public int popularity;
	public long duration;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public JSONArray getStyle() {
		return style;
	}
	public void setStyle(JSONArray style) {
		this.style = style;
	}
	public JSONArray getGenre() {
		return genre;
	}
	public void setGenre(JSONArray genre) {
		this.genre = genre;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public JSONObject getSpotify() {
		return spotify;
	}
	public void setSpotify(JSONObject spotify) {
		this.spotify = spotify;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getTrackName() {
		return trackName;
	}
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}
	public JSONArray getTrack_artists() {
		return track_artists;
	}
	public void setTrack_artists(JSONArray track_artists) {
		this.track_artists = track_artists;
	}
	public int getPopularity() {
		return popularity;
	}
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	
}
