package com.example.demo.repo;

import com.example.demo.entity.TextEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextEmbeddingRepository extends JpaRepository<TextEmbedding, Long> {

}