package com.example.demo.pojo;

import java.util.List;

public class EmbeddingRequest {

    private String model;
    private List<String> input;

    public EmbeddingRequest() {
        super();
    }

    public EmbeddingRequest(String model, List<String> input) {
        super();
        this.model = model;
        this.input = input;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }

}