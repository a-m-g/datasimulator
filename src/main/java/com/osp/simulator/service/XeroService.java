package com.osp.simulator.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class XeroService {

	private final Map<String, String> payloads = new HashMap<>();
	
//	public XeroService() {
//		readPayloads();
//	}
	
	@Autowired
	private ResourceLoader resourceLoader;	
	
	public String getPayload(String payload) {
		return payloads.get(payload);
	}
	
	public String convert(InputStream inputStream) throws IOException {
		 
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {	
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
		}
	 
		return stringBuilder.toString();
	}
	
	@PostConstruct
	private void readPayloads() {
		try {
			
			Resource resource = resourceLoader.getResource("file:///opt/project/xeroaccounts.txt");
			String content = convert(resource.getInputStream());			
			payloads.put("xero_accounts", content);
			
			resource = resourceLoader.getResource("file:///opt/project/xerotrialbalance.txt");
			content = convert(resource.getInputStream());			
			payloads.put("xero_trialbalance", content);			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
