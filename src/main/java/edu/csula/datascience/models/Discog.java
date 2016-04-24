package edu.csula.datascience.models;

import org.json.JSONArray;

public class Discog {
	public String title,year,country;
	public JSONArray style;
	public JSONArray genre;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
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
	
	
}
