package edu.csula.datascience.acquisition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import edu.csula.datascience.models.AudioProperties;
import edu.csula.datascience.models.Track;

public class TwitterSource2 {
	String query;
	String accessToken = null;
	List<Status> statusCollection = new ArrayList<Status>();
	TwitterCollector collector = new TwitterCollector();
	TwitterSource2(String query) {
		this.query = query;
	}

	FileWriter statusWriter = null;
	
	
	public TwitterStream getStream() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY[0]);
		cb.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET[0]);
		cb.setOAuthAccessToken(Constants.TWITTER_ACCESS_TOKEN[0]);
		cb.setOAuthAccessTokenSecret(Constants.TWITTER_ACCESS_SECRET[0]);
		return new TwitterStreamFactory(cb.build()).getInstance();
	}

	public Collection<Track> fetchSpotify(Collection<Track> cleanedTweets)
			throws URISyntaxException, IOException {

		System.out.println("Spotify has started fetching data");
		FileWriter writer = new FileWriter("./spotify-data.json", true);
		List<JSONArray> spotifyCollection = new ArrayList<JSONArray>();
		List<Track> tracksCollection = new ArrayList<Track>();
		for (Track track : cleanedTweets) {

			String name = track.getTrackName().trim();
			String artist = track.getArtistName().trim();
			System.out.println("Track name:" + name + "\t artist:" + artist);
			URI uri = new URI("https", "api.spotify.com", "/v1/search",
					"q=track:\"" + name + "\" artist:" + artist
							+ "&type=track&limit=20", null);

			JsonNode response = null;
			try {

				response = Unirest.get(uri.toURL().toString())
						.header("Content-Type", "application/json")
						.header("accept", "application/json").asJson()
						.getBody();
				org.json.JSONObject obj = response.getObject();
				System.out.println("******************************"
						+ obj.toString());
				JSONObject tracks = null;
				try {
					tracks = obj.getJSONObject("tracks");
				} catch (Exception e) {
					try {
						Thread.sleep(500);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					continue;
				}
				int total = tracks.getInt("total");
				if (total > 0) {
					JSONArray results = tracks.getJSONArray("items");
					spotifyCollection.add(results);
					JSONObject trackInfo = results.getJSONObject(0);
					String trackId = trackInfo.getString("id");
					long trackDuration = trackInfo.getLong("duration_ms");
					int popularity = trackInfo.getInt("popularity");
					track.setTrackId(trackId);
					track.setTrackDuration(trackDuration);
					track.setTrackSpotifyPopularity(popularity);
					if (spotifyCollection.size() == 1500) {
						writer.flush();
						writer.append(spotifyCollection.toString());

						spotifyCollection = new ArrayList<JSONArray>();

					}
					tracksCollection.add(track);
				}
			} catch (UnirestException e) {

				try {
					Thread.sleep(800);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
		if (spotifyCollection.size() > 0) {

			writer.append(spotifyCollection.toString() + "\n");

		}
		writer.flush();
		writer.close();
		
		// write the data present in spotify collection before sending it to
		// main

		return tracksCollection;
	}

	public String getNewSpotifyToken() {
		String urlForAccessToken = "https://accounts.spotify.com/api/token";
		JsonNode response = null;
		try {
			response = Unirest
					.post(urlForAccessToken)
					.header("Content-Type", "application/x-www-form-urlencoded")
					.body("grant_type=client_credentials&client_id=b6e6a0a1fe5344b195c293ee006efa34&client_secret=5ab2738f61ee40868c9aec68e8077a4c")
					.asJson().getBody();
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		org.json.JSONObject obj = response.getObject();
		System.out.println(obj.toString());
		return obj.getString("access_token");
	}

	public Collection<Track> fetchAudioProperties(Collection<Track> fetchSongs)
			throws IOException {
		FileWriter fw = new FileWriter("./audio-properties.json", true);
		// TODO Auto-generated method stub
		System.out.println("******* Fetching Spotify Properties for :"
				+ fetchSongs.size() + " songs***********");

		ArrayList<Track> fetchedSongs = (ArrayList<Track>) fetchSongs;
		int totalSize = fetchedSongs.size();
		int count = Math.max(totalSize / 100, 1);
		System.out.println("Count Value is:" + count);

		for (int i = 0; i < count; i++) {
			String append = "";
			int j = 100 * i;

			for (; j < (100 * i) + 100 && j < fetchedSongs.size(); j++) {

				Track track = fetchedSongs.get(j);
				append += track.getTrackId();
				if (j == fetchedSongs.size() - 1) {
					break;
				}
				append += ",";
			}
			System.out.println("Appended Id's are:" + append);

			JsonNode response = null;
			String url;
			JSONArray audio_features = null;
			try {
				url = "https://api.spotify.com/v1/audio-features/?ids="
						+ append + "&access_token=" + accessToken;
				response = Unirest.get(url)
						.header("Content-Type", "application/json")
						.header("accept", "application/json").asJson()
						.getBody();
				org.json.JSONObject obj = response.getObject();
				System.out.println(obj.toString());
				audio_features = obj.getJSONArray("audio_features");
			} catch (Exception e) {
				// get the new token and
				System.out.println("got new Token");
				accessToken = getNewSpotifyToken();
				url = "https://api.spotify.com/v1/audio-features/?ids="
						+ append + "&access_token=" + accessToken;
				try {
					response = Unirest.get(url)
							.header("Content-Type", "application/json")
							.header("accept", "application/json").asJson()
							.getBody();
					org.json.JSONObject obj = response.getObject();
					System.out.println(obj.toString());
					audio_features = obj.getJSONArray("audio_features");
				} catch (UnirestException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			for (int s = 0; j < (100 * i) + 100 && j < fetchedSongs.size(); s++, j++) {
				JSONObject audio = audio_features.getJSONObject(s);

				double loudness = audio.getDouble("loudness");
				double liveness = audio.getDouble("liveness");
				double tempo = audio.getDouble("tempo");
				double valence = audio.getDouble("valence");
				double instrumentalness = audio.getDouble("instrumentalness");
				double danceability = audio.getDouble("danceability");
				double speechiness = audio.getDouble("speechiness");
				double mode = audio.getDouble("mode");
				double acousticness = audio.getDouble("acousticness");
				double energy = audio.getDouble("energy");
				Track track = fetchedSongs.get(s);
				AudioProperties ap = new AudioProperties();
				ap.setAcousticness(acousticness);
				ap.setDanceability(danceability);
				ap.setEnergy(energy);
				ap.setInstrumentalness(instrumentalness);
				ap.setLiveness(liveness);
				ap.setLoudness(loudness);
				ap.setMode(mode);
				ap.setSpeechiness(speechiness);
				ap.setTempo(tempo);
				ap.setValence(valence);
				track.setAudioProperties(ap);

			}

		}
		for (Track track : fetchedSongs) {
			JSONObject obj = new JSONObject();
			obj.put(track.getTrackId(), track.getAudioProperties());
			fw.append(obj.toString());
		}
		fw.flush();
		fw.close();
		return fetchSongs;
	}

	public void readTwitterFeed() {
		try{
			statusWriter=new FileWriter("./status.txt",true);
			}catch(IOException e){
				System.out.println(e);
			}
		
		System.out.println("Connected with twitter streaming API");
		System.out.println("Started fetching Tweets");
		TwitterStream stream = this.getStream();
		StatusListener listener = new StatusListener() {

			@Override
			public void onException(Exception e) {
				System.out.println("Exception occured:" + e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onTrackLimitationNotice(int n) {
				System.out.println("Track limitation notice for " + n);
			}

			@Override
			public void onStatus(Status status) {
				// Change the size here.
				if (statusCollection.size() == 10) {
					// send this collection for munging and empty the list
					//save the status in the file first
					try {
						statusWriter.append(statusCollection.toString());
						statusWriter.flush();
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					List<Track> tracks = new ArrayList<Track>();
					tracks = (ArrayList<Track>) collector
							.mungee(statusCollection);
					System.out.println("Cleaned " + tracks.size()
							+ " tracks out of " + statusCollection.size());
					try {
						
						tracks = (ArrayList<Track>) fetchSpotify(tracks);
						System.out.println("Able to fetched Information for: "
								+ tracks.size() + " tracks");
					} catch (URISyntaxException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						System.out
								.println("Started getting Audio Properties of tracks");
						tracks = (ArrayList<Track>) fetchAudioProperties(tracks);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					statusCollection = new ArrayList<Status>();
				}
				statusCollection.add(status);

			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				System.out.println("Stall warning");
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				System.out.println("Scrub geo with:" + arg0 + ":" + arg1);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				System.out.println("Status deletion notice");
			}
		};
		FilterQuery qry = new FilterQuery();
		
		qry.track(query);
		stream.addListener(listener);
		stream.filter(qry);
	}

}
