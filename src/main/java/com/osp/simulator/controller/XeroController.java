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

import com.osp.simulator.service.XeroService;

import io.swagger.annotations.ApiOperation;

@RestController
@PreAuthorize("hasRole('USER')")
public class XeroController {
	
    @Autowired
	private XeroService xeroService;
	
	@RequestMapping(value = "/xero/api/accounts",method = GET)
	@ApiOperation(value="getAccounts")
	public ResponseEntity<String> getAccounts() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<String>(xeroService.getPayload("xero_accounts"),responseHeaders,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/xero/api/reports",method = GET)
	@ApiOperation(value="getReports")
	public ResponseEntity<String> getTrialBalance(@RequestParam (required = true) String reportName) {
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		if(reportName != null && reportName.equalsIgnoreCase("TrialBalance")) 
			return new ResponseEntity<String>(xeroService.getPayload("xero_trialbalance"),responseHeaders,HttpStatus.OK);
		
		return new ResponseEntity<String>("Not found",responseHeaders,HttpStatus.NOT_FOUND);
	}	
}
