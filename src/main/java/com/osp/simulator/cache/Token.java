package com.osp.simulator.cache;

public class Token {

	//a string representation of the authorization token
	private String userToken;
	//the expiry time of the authorization token
	private long userTokenExpiry;
	//the last time this token was placed in a "live"/"available" state - ie: put in the live token list
	//expressed in epoch-milliseconds
	private long lastlive;
	//the last time this token was used ie: had a API call made against it
	private long lastUsed;
	//the rate at which this token can be placed in a "live" state
	//expressed in milliseconds (in wait time between calls)
	private long rate;
	
	public Token(){		
	}
	
	public Token(String userToken, long expiry, long rate) {
		this.userToken = userToken;
		this.userTokenExpiry = expiry;
		this.lastlive = 0;
		this.lastUsed = 0;
		this.rate = rate;
	}
	
	public String getUserToken() {
		return userToken;
	}
	
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	
	public long getExpiry() {
		return userTokenExpiry;
	}
	
	public void setExpiry(long expiry) {
		this.userTokenExpiry = expiry;
	}
	
	public long getLastlive() {
		return lastlive;
	}
	
	public void setLastlive(long lastlive) {
		this.lastlive = lastlive;
	}
	
	public long getRate() {
		return rate;
	}
	
	public void setRate(long rate) {
		this.rate = rate;
	}

	public long getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(long lastUsed) {
		this.lastUsed = lastUsed;
	}

}
