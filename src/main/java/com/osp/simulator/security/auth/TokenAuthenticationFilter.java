package com.osp.simulator.security.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.osp.simulator.cache.TokenPool;
import com.osp.simulator.security.TokenHelper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * 
 * @author gibsona
 *
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

    private TokenHelper tokenHelper;

    private UserDetailsService userDetailsService;

    public TokenAuthenticationFilter(TokenHelper tokenHelper, UserDetailsService userDetailsService) {
        this.tokenHelper = tokenHelper;
        this.userDetailsService = userDetailsService;
    }


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
    		throws IOException, ServletException {

        String username;
        String authToken = tokenHelper.getToken(request, TokenType.BEARER.getKey());
        boolean abortRequest = false;

        try {
			if (authToken != null) {
			    // get username from token
			    username = tokenHelper.getUsernameFromToken(authToken);
			    
			    Claims claims = tokenHelper.getAllClaimsFromToken(authToken);			    
			    if (username != null && claims.containsValue(Scopes.BEARER_TOKEN.authority())) {
			        // get user
			        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			        if (tokenHelper.validateToken(authToken, userDetails)) {
			            // create authentication
			            TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
			            authentication.setToken(authToken);
			            SecurityContextHolder.getContext().setAuthentication(authentication);
			        }
			    }
			    
			  if(!TokenPool.isTokenLive(authToken)) {
				  logger.debug("Exceeded limit no live token");
				  response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				  response.setContentType("application/json");
				  response.getWriter().write("{\"error\":\"Throttle Exceeded\"}");
				  abortRequest = true;
			  } else {
				  TokenPool.consumeLiveToken(authToken);
			  } 
			}
			
			if(!abortRequest) {
				chain.doFilter(request, response);
			}
		} catch (ExpiredJwtException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Token Expired\"}");
		} catch (UnsupportedJwtException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Unsupported Token\"}");
		} catch (MalformedJwtException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Malformed Token\"}");
		} catch (SignatureException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Invalid Token\"}");
		} catch (UsernameNotFoundException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Invalid User\"}");
		} catch (IllegalArgumentException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Illegal Argument\"}");
		}
        
    }

}