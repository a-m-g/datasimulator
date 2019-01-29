package com.osp.simulator.cache;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TokenPool {

	private static final Logger logger = LoggerFactory.getLogger(TokenPool.class);
	
	//tokens which may be called at the current moment in time.
	private static List<String> liveTokenList = Collections.synchronizedList(new ArrayList<String>());
	//Holds all tokens given out and expires them
	private static Map<String,Token> tokenPool = new ConcurrentHashMap<String,Token>();
	//keeps a running total of calls for calls to the OSP from an account 
	private int accountThrottle = 0;
	
	public TokenPool() {
		
		long timeInterval = 1;	
		
		logger.info("Creating Token Pool : tokenPool : " + tokenPool.size() + 
				" : live tokens : " + liveTokenList.size()); 
		
		Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(timeInterval * 100);
                        
//                        logger.info("WAKEUP : "+ getTimeStampMilliSeconds() + " : tokenPool : " + tokenPool.size() + 
//                				" : live tokens : " + liveTokenList.size()); 
                        refreshTokenPool();
//                        logger.info("REFRESH : "+ getTimeStampMilliSeconds() + " : tokenPool : " + tokenPool.size() + 
//                				" : live tokens : " + liveTokenList.size()); 
                    } catch (InterruptedException ex) {
                    }
                    
                }
            }
        });
        
        t.setDaemon(true);
        t.start();
	}
	
	private static long getTimeStampMilliSeconds() {
		Instant instant = Instant.now();
        return instant.toEpochMilli();
	}
	
	public static boolean isTokenLive(String key) {
		return liveTokenList.contains(key);
	}
	
	public static void consumeLiveToken(String key) {
		if(liveTokenList.contains(key)) {
			Token token = tokenPool.get(key);
			token.setLastUsed(getTimeStampMilliSeconds());
			removeKeyFromTokenList(key);
		}
	}
	
	//Remove any tokens from the pool which are expired
	private void refreshTokenPool() {
        
		for (Map.Entry<String, Token> entry : tokenPool.entrySet()) {	
			Token token = entry.getValue();
			String key = entry.getKey();
			if(token.getExpiry() <= getTimeStampMilliSeconds()) {
				tokenPool.remove(key);
				//check if it is in live list and remove it
				if(liveTokenList.contains(key)) {
					removeKeyFromTokenList(key);
				}
		    //Add the token if it has never been live or if the current time is more than the last use + the rate 
			} else if(token.getLastlive() == 0 || (getTimeStampMilliSeconds() >= token.getLastUsed()+token.getRate())) {
//				logger.info("MAKE-LIVE : " + (token.getLastlive()+token.getRate()) + ", : time : " + getTimeStampMilliSeconds() + ", last used : " + token.getLastUsed());
				if(!liveTokenList.contains(key)) {
					addKeyToTokenList(key);
					token.setLastlive(getTimeStampMilliSeconds());
				}
			}
		}
	}
	
	public static void addTokenToPool(String userToken, long expiry, long rate) {
		//check whether the token is already in the pool
		if(!tokenPool.containsKey(userToken)) {
			tokenPool.put(userToken, new Token(userToken, expiry, rate));
		}
	}
	
	public void removeTokenFromPool(String userToken) {
		tokenPool.remove(userToken);
	}

	public List<String> getTokenList() {
		return liveTokenList;
	}

	public void setTokenList(List<String> tokenList) {
		this.liveTokenList = tokenList;
	}
	
	public void addKeyToTokenList(String key) {
		liveTokenList.add(key);
	}
	
	private static void removeKeyFromTokenList(String key) {
		liveTokenList.remove(key);
	}
}
