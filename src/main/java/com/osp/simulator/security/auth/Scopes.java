package com.osp.simulator.security.auth;

public enum Scopes {
	
	 REFRESH_TOKEN,
	 BEARER_TOKEN;
	
	 public String authority() {
	        return this.name();
	}
	 
}
