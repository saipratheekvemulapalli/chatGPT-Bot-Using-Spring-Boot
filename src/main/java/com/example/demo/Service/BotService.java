package com.example.demo.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class BotService {

    private static final Logger logger = LoggerFactory.getLogger(BotService.class);
    private final RestTemplate restTemplate;

    @Value()
    private String apiKey;

    @Value()
    private String apiURL;

    public BotService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callOpenAIWithRetry(String prompt, int retries) {
        for (int i = 0; i < retries; i++) {
            try {
                return callOpenAI(prompt);
            } catch (HttpClientErrorException.TooManyRequests e) {
                logger.warn("Rate limit exceeded, retrying... (attempt {}/{})", i + 1, retries);
                try {
                    Thread.sleep((long) Math.pow(2, i) * 1000); 
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted during backoff", ie);
                }
            } catch (HttpClientErrorException.BadRequest e) {
                logger.error("Bad Request: {}", e.getResponseBodyAsString());
                throw new RuntimeException("Error calling OpenAI API: " + e.getResponseBodyAsString());
            }
        }
        throw new RuntimeException("Exceeded retries for OpenAI API");
    }

//    private String callOpenAI(String prompt) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + apiKey);
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("model", "gpt-3.5-turbo");
//        body.put("messages", List.of(
//            Map.of("role", "user", "content", prompt)
//        ));
//        body.put("max_tokens", 100);
//        body.put("temperature", 0.7);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//        return restTemplate.postForObject(apiURL, entity, String.class);
    }
}
