package com.osp.simulator.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.osp.simulator.model.User;
import com.osp.simulator.security.TokenHelper;
import com.osp.simulator.service.UserService;

/**
 * Note there is no security over this API endpoint, because we want to be able to refresh users
 * on the system and be able to get their credentials without logging users on individually
 * @author gibsona
 *
 */
@RestController
@RequestMapping( value = "/user", produces = MediaType.APPLICATION_JSON_VALUE )
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    TokenHelper tokenHelper;

    @RequestMapping( method = GET, value = "/{userId}" )
    //@PreAuthorize("hasRole('ADMIN')")
    public User loadById( @PathVariable Long userId ) {
        return userService.findById( userId );
    }
    
    /**
     * populates test database with new access tokens for test purposes
     * @return
     */
    @RequestMapping( method = GET, value= "/refreshCredentials")
    public ResponseEntity<String> refreshUserCredentials() {
    	
    	HttpHeaders responseHeaders = new HttpHeaders();
    	responseHeaders.setContentType(MediaType.APPLICATION_JSON);
    	
    	List<User> users = userService.findAll();
    	
    	for(User user : users) {
    		user.setToken(tokenHelper.generateAccessToken( user.getUsername()));
            user.setRefreshtoken(tokenHelper.generateRefreshToken(user.getUsername()));
            userService.updateUser(user);
    	}
    	return new ResponseEntity<String>("Refreshed credentials for " + users.size() + " users.", responseHeaders, HttpStatus.OK);
    }

    /**
     * Gets all users
     * @return
     */
    @RequestMapping( method = GET, value= "/all")
    //@PreAuthorize("hasRole('ADMIN')")
    public List<User> loadAll() {
    	List<User> users = userService.findAll();    	
        return users;
    }


    /*
     *  We are not using userService.findByUsername here(we could),
     *  so it is good that we are making sure that the user has role "ROLE_USER"
     *  to access this endpoint.
     */
    @RequestMapping(method = GET, value = "/whoami")
    //@PreAuthorize("hasRole('USER')")
    public User user(Principal user) {
        return this.userService.findByUsername(user.getName());
    }
}
