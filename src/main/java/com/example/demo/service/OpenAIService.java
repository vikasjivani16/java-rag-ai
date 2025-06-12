package com.example.demo.service;

import com.example.demo.pojo.EmbeddingRequest;
import com.example.demo.pojo.EmbeddingResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class OpenAIService {

    private final String OPENAI_API_KEY =
            "";
    private final String OPENAI_EMBEDDING_URL = "https://api.openai.com/v1/embeddings";

    public List<Float> getEmbedding(String text) {
        RestTemplate restTemplate = new RestTemplate();

        EmbeddingRequest request = new EmbeddingRequest();
        request.setModel("text-embedding-3-small"); // Or another model
        request.setInput(Collections.singletonList(text));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        HttpEntity<EmbeddingRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<EmbeddingResponse> response = restTemplate.exchange(
                OPENAI_EMBEDDING_URL,
                HttpMethod.POST,
                entity,
                EmbeddingResponse.class
        );

        System.err.println(response);
        System.err.println(response.getBody().data.get(0).embedding);

        return response.getBody().data.get(0).embedding;
    }
}
