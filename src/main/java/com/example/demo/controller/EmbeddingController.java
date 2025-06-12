package com.example.demo.controller;

import com.example.demo.service.OpenAIService2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/embedding")
public class EmbeddingController {

    private final OpenAIService2 openAIService;

    public EmbeddingController(OpenAIService2 openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping
    public List<Float> getEmbedding(@RequestParam String text) {
        System.err.println("HELLO....!" + text);
        return openAIService.getAndSaveEmbedding(text, 1);
    }
}