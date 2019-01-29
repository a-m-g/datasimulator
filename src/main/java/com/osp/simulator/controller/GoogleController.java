package com.osp.simulator.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.osp.simulator.service.GoogleService;

@RestController
@PreAuthorize("hasRole('USER')")
public class GoogleController {
	
	  @Autowired
	  private GoogleService googleService;

	  @RequestMapping(value = "/google/analytics/devguides/reporting/core/dimsmets",method = GET)
	  public ResponseEntity<String> dimsmets(@RequestParam (required = true) String view, @RequestParam  (required = true) String group, 
			  @RequestParam (required = true) String jump) {
		  
		  HttpHeaders responseHeaders = new HttpHeaders();
		  responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		  
		  if(jump != null && jump.equalsIgnoreCase("ga_sessions")) {
			  return new ResponseEntity<String>(googleService.getPayload("ga_sessions"),responseHeaders,HttpStatus.OK);
		  } else if (jump != null && jump.equalsIgnoreCase("ga_percentnewsessions")) {
			  return new ResponseEntity<String>(googleService.getPayload("ga_percentnewsessions"),responseHeaders,HttpStatus.OK);
		  } else if (jump != null && jump.equalsIgnoreCase("ga_sessionduration")) {
			  return new ResponseEntity<String>(googleService.getPayload("ga_sessionduration"),responseHeaders,HttpStatus.OK);
		  } else if (jump != null && jump.equalsIgnoreCase("ga_avgsessionduration")) {
			  return new ResponseEntity<String>(googleService.getPayload("ga_avgsessionduration"),responseHeaders,HttpStatus.OK);
		  } else {
			  return new ResponseEntity<String>("Not found",responseHeaders,HttpStatus.NOT_FOUND);
		  }

	  }
	  
}
