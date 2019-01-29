package com.osp.simulator.service;

import java.io.BufferedReader;
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
public class GoogleService {

	private final Map<String, String> payloads = new HashMap<>();
	
	@Autowired
	private ResourceLoader resourceLoader;
	
//	Replaced by PostConstruct
//	public GoogleService() {
//		readPayloads();
//	}
	
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
	public void readPayloads() {
		try {
										
			Resource resource = resourceLoader.getResource("file:///opt/project/googlesessions.txt");
			String content = convert(resource.getInputStream());
			payloads.put("ga_sessions", content);

			resource = resourceLoader.getResource("file:///opt/project/googlenewsessions.txt");
			content = convert(resource.getInputStream());
			payloads.put("ga_percentnewsessions", content);		
			
			resource = resourceLoader.getResource("file:///opt/project/googlesessionduration.txt");
			content = convert(resource.getInputStream());
			payloads.put("ga_sessionduration", content);	
			
			resource = resourceLoader.getResource("file:///opt/project/googleavgsessionduration.txt");
			content = convert(resource.getInputStream());
			payloads.put("ga_avgsessionduration", content);	
            
		}catch (Exception e) {
			e.printStackTrace();
		}
            
	}
		
}
