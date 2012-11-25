package com.appsolve.worddrag;

import com.appsolve.worddrag.R;

public class LevelDetails {
	private String word;
	private Boolean locked;
	private Integer secs;
	private Integer stars;
	
	public String getWord() {
		return word;
		}
	public void setWord(String word) {
		this.word = word;
		}
	
	public Boolean getLocked() {
		return locked;
		}
	public void setLocked(Boolean locked) {
		this.locked = locked;
		}	
	
	public int getSecs() {
		return secs;
		}
	public void setSecs(int secs) {
		this.secs = secs;
		}	
	
	public int getStars() {
		return stars;
		}
	public void setStars(int stars) {
		this.stars = stars;
		}	
}
