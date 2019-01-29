package com.osp.simulator.security.auth;

public enum TokenType {

	BEARER("Bearer"),
	REFRESH("Refresh");	
	
	private String key;
	
	TokenType(String key){
		this.key = key;
	}
	
	public String getKey() {
		return this.key;
	}
}
