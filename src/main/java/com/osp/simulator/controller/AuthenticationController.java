package com.osp.simulator.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.osp.simulator.model.User;
import com.osp.simulator.model.UserTokenState;
import com.osp.simulator.security.TokenHelper;
import com.osp.simulator.security.auth.JwtAuthenticationRequest;
import com.osp.simulator.security.auth.TokenType;

/**
 * 
 * @author gibsona
 *
 */
@RestController
public class AuthenticationController {

    @Autowired
    TokenHelper tokenHelper;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

//    @Autowired
//    private CustomUserDetailsService userDetailsService;

//    @Autowired
//    private DeviceProvider deviceProvider;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) 
    		throws AuthenticationException, IOException {

        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        // Inject into security context
        //SecurityContextHolder.getContext().setAuthentication(authentication);

        // token creation
        User user = (User)authentication.getPrincipal();
        String accessToken = tokenHelper.generateAccessToken( user.getUsername());
        //int expiresIn = tokenHelper.getExpiredIn(device);
        String refreshToken = tokenHelper.generateRefreshToken(user.getUsername());
        
        // Return the token
        return ResponseEntity.ok(new UserTokenState(accessToken, tokenHelper.getExpiryTime(accessToken)
        		,refreshToken,tokenHelper.getExpiryTime(refreshToken)));
    }

    
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAuthenticationToken(HttpServletRequest request) {

        String refreshToken = tokenHelper.getToken( request, TokenType.REFRESH.getKey() );
        if (refreshToken != null) {

        	String accessToken = tokenHelper.getAccessTokenFromRefreshToken(refreshToken);
        	
            return ResponseEntity.ok(new UserTokenState(accessToken, tokenHelper.getExpiryTime(accessToken)
            		,refreshToken,tokenHelper.getExpiryTime(refreshToken)));
        } else {
            UserTokenState userTokenState = new UserTokenState();
            return ResponseEntity.accepted().body(userTokenState);
        }
    }

//    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> changePassword(@RequestBody PasswordChanger passwordChanger) {
//        userDetailsService.changePassword(passwordChanger.oldPassword, passwordChanger.newPassword);
//        Map<String, String> result = new HashMap<>();
//        result.put( "result", "success" );
//        return ResponseEntity.accepted().body(result);
//    }

//    static class PasswordChanger {
//        public String oldPassword;
//        public String newPassword;
//    }
        
}