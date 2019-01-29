package com.osp.simulator.model;

/**
 * 
 * @author gibsona
 *
 */
public class UserTokenState {
	
    private String access_token;
    private Long access_expires_in;
    private String refresh_token;
    private Long refresh_expires_in;

    public UserTokenState() {
        this.access_token = null;
        this.access_expires_in = null;
    }

    public UserTokenState(String access_token, long access_expires_in, String refresh_token, long refresh_expires_in) {
        this.access_token = access_token;
        this.access_expires_in = access_expires_in;
        this.refresh_token = refresh_token;
        this.refresh_expires_in = refresh_expires_in;
    }

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public Long getAccess_expires_in() {
		return access_expires_in;
	}

	public void setAccess_expires_in(Long access_expires_in) {
		this.access_expires_in = access_expires_in;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public Long getRefresh_expires_in() {
		return refresh_expires_in;
	}

	public void setRefresh_expires_in(Long refresh_expires_in) {
		this.refresh_expires_in = refresh_expires_in;
	}
 
}