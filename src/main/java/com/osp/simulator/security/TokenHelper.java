package com.osp.simulator.security;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.osp.simulator.cache.TokenPool;
import com.osp.simulator.common.TimeProvider;
import com.osp.simulator.model.User;
import com.osp.simulator.security.auth.Scopes;
import com.osp.simulator.security.auth.TokenBasedAuthentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;


/**
 * 
 * @author gibsona
 *
 */

@Component
public class TokenHelper {

    @Value("${app.name}")
    private String APP_NAME;

    @Value("${jwt.secret}")
    public String SECRET;

    @Value("${jwt.expires_in}")
    private int EXPIRES_IN;

    @Value("${jwt.mobile_expires_in}")
    private int MOBILE_EXPIRES_IN;

    @Value("${jwt.header}")
    private String AUTH_HEADER;

//    static final String AUDIENCE_UNKNOWN = "unknown";
//    static final String AUDIENCE_WEB = "web";
//    static final String AUDIENCE_MOBILE = "mobile";
//    static final String AUDIENCE_TABLET = "tablet";
    
    static final long RATE = 500; // 1/2 Second
    
	//static final long EXPIRATIONTIME = 864_000_000; // 10 days
	//static final long EXPIRATIONTIME = 864_00_000; // 1 days
	//static final long EXPIRATIONTIME = 36_00_000; // 1 hour
	static final long TOKEN_EXPIRATIONTIME = 60_0000; // 10 minutes
	static final long REFRESH_EXPIRATIONTIME = 864_00_000; // 1 day
	//static final long EXPIRATIONTIME = 600_000; // 10 minute

    @Autowired
    TimeProvider timeProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public String getUsernameFromToken(String token) throws ExpiredJwtException, UnsupportedJwtException, 
        MalformedJwtException, SignatureException, IllegalArgumentException {
    	
        final Claims claims = this.getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public Date getIssuedAtDateFromToken(String token) throws ExpiredJwtException, UnsupportedJwtException, 
        MalformedJwtException, SignatureException, IllegalArgumentException {
    	
        final Claims claims = this.getAllClaimsFromToken(token);
        return claims.getIssuedAt();
    }

    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            audience = claims.getAudience();
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

//    public String refreshToken(String token) {
//        String refreshedToken;
//        Date now = new Date(System.currentTimeMillis());
//        Date expiry = new Date(now.getTime() + REFRESH_EXPIRATIONTIME); 
//        
//        try {
//            final Claims claims = this.getAllClaimsFromToken(token);
//            claims.setIssuedAt(now);
//            refreshedToken = Jwts.builder()
//                .setClaims(claims)
//                .setExpiration(expiry)
//                .signWith( SIGNATURE_ALGORITHM, SECRET )
//                .compact();
//        } catch (Exception e) {
//            refreshedToken = null;
//        }
//        
//        TokenPool.addTokenToPool(refreshedToken, expiry.getTime(), RATE);
//        
//        return refreshedToken;
//    }
    
	public long getExpiryTime(String token) {
		     
	    try {
		    return Jwts.parser()
			    .setSigningKey(SECRET)
				.parseClaimsJws(token)
				.getBody().getExpiration().getTime();
		} catch (Exception e) {
			return 0;
	    }  
	}

	/**
	 * 
	 * @param username
	 * @return
	 */
    public String generateAccessToken(String username) {
        
        Date now = new Date(System.currentTimeMillis());
        Date expiry = new Date(now.getTime() + TOKEN_EXPIRATIONTIME);
        
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("scope", Scopes.BEARER_TOKEN.authority());
        
        String JWT = Jwts.builder()
                .setIssuer( APP_NAME )
                .setSubject(username)
                .setClaims(claims)
                //.setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith( SIGNATURE_ALGORITHM, SECRET )
                .compact();
        
        TokenPool.addTokenToPool(JWT, expiry.getTime(), RATE);
        return JWT;
    }
    
    public String getAccessTokenFromRefreshToken(String refreshToken) {
    	
    	if (refreshToken != null) {
		    // get username from token
		    String username = getUsernameFromToken(refreshToken);
		    if (username != null) {
		        // get user
		        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		        Claims claims = getAllClaimsFromToken(refreshToken);
		        if (validateToken(refreshToken, userDetails) && claims.containsValue(Scopes.REFRESH_TOKEN.authority())) {
		        	return generateAccessToken(username); 
		        }
		    }
    	}
    	return null;
    	
    }
    
    /**
     * 
     * @param username
     * @return
     */
    public String generateRefreshToken(String username) {
        
        Date now = new Date(System.currentTimeMillis());
        Date expiry = new Date(now.getTime() + TOKEN_EXPIRATIONTIME);
        
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("scope", Scopes.REFRESH_TOKEN.authority());
        
        String JWT = Jwts.builder()
                .setIssuer( APP_NAME )
                .setSubject(username)
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                //.setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith( SIGNATURE_ALGORITHM, SECRET )
                .compact();
        
        TokenPool.addTokenToPool(JWT, expiry.getTime(), RATE);
        
        return JWT;
    }    
    

//    private String generateAudience(Device device) {
//        String audience = AUDIENCE_UNKNOWN;
//        if (device.isNormal()) {
//            audience = AUDIENCE_WEB;
//        } else if (device.isTablet()) {
//            audience = AUDIENCE_TABLET;
//        } else if (device.isMobile()) {
//            audience = AUDIENCE_MOBILE;
//        }
//        return audience;
//    }

    public Claims getAllClaimsFromToken(String token) throws ExpiredJwtException, UnsupportedJwtException, 
        MalformedJwtException, SignatureException, IllegalArgumentException {
    	
        Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        return claims;
    }

//    private Date generateExpirationDate(Device device) {
//        long expiresIn = device.isTablet() || device.isMobile() ? MOBILE_EXPIRES_IN : EXPIRES_IN;
//        return new Date(timeProvider.now().getTime() + expiresIn * 1000);
//    }
//
//    public int getExpiredIn(Device device) {
//        return device.isMobile() || device.isTablet() ? MOBILE_EXPIRES_IN : EXPIRES_IN;
//    }

    public Boolean validateToken(String token, UserDetails userDetails) throws ExpiredJwtException, UnsupportedJwtException, 
        MalformedJwtException, SignatureException, IllegalArgumentException {
        
    	User user = (User) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        return (
                username != null &&
                username.equals(userDetails.getUsername()) &&
                        !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())
        );
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    public String getToken( HttpServletRequest request, String tokenType ) {
        /**
         *  Getting the token from Authentication header
         *  e.g Bearer your_token
         */
        String authHeader = getAuthHeaderFromHeader( request );
        if ( authHeader != null && authHeader.startsWith(tokenType + " ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public String getAuthHeaderFromHeader( HttpServletRequest request ) {
        return request.getHeader(AUTH_HEADER);
    }

}