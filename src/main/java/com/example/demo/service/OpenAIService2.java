package com.example.demo.service;

import com.example.demo.pojo.EmbeddingRequest;
import com.example.demo.pojo.EmbeddingResponse;
import com.example.demo.repo.TextEmbeddingRepository;
import com.pgvector.PGvector;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
public class OpenAIService2 {

    private final String OPENAI_API_KEY =
            "";
    private final String OPENAI_EMBEDDING_URL = "https://api.openai.com/v1/embeddings";

    private final TextEmbeddingRepository repository;

    public OpenAIService2(TextEmbeddingRepository repository) {
        this.repository = repository;
    }

    public List<Float> getEmbedding(String text) {
        RestTemplate restTemplate = new RestTemplate();

        EmbeddingRequest request = new EmbeddingRequest();
        request.setModel("text-embedding-ada-002"); // model
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

        // System.err.println(response);
        // System.err.println(response.getBody().data.get(0).embedding);

        return response.getBody().data.get(0).embedding;
    }

    public List<Float> getAndSaveEmbedding(String text, String saleOrderId) {
        // Call OpenAI
        List<Float> embedding = getEmbedding(text);

        String url = "jdbc:postgresql://localhost:5433/next_ai";
        String user = "postgres";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to PostgreSQL!");

            PreparedStatement selectStmt = conn.prepareStatement("SELECT * FROM embeddings WHERE object_id = ?");
            selectStmt.setString(1, "SALE_ORDER_" + saleOrderId);
            ResultSet resultSet = selectStmt.executeQuery();

            boolean alredyRecordExist = resultSet.next();
            System.err.println(alredyRecordExist);

            if (alredyRecordExist) {

                String id = resultSet.getString("id");

                System.err.println("Update Recored..!!");
                // PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM embeddings WHERE id = ?");
                // deleteStmt.setString(1, String.valueOf(saleOrderId));
                // deleteStmt.executeUpdate();

                String sql = "UPDATE embeddings SET resource_id = ?, content = ?, embedding = ?, object_id = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, "lw5gj3mnt4f9bwa17b2gh");
                stmt.setString(2, text);
                stmt.setObject(3, new PGvector(embedding));
                stmt.setObject(4, "SALE_ORDER_" + saleOrderId);
                stmt.setObject(5, id);

                stmt.executeUpdate();

            } else {
                System.err.println("Insert Recored..!!");
                String sql = "INSERT INTO embeddings (id, resource_id, content, embedding, object_id) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, generateSecureString(21));
                stmt.setString(2, "lw5gj3mnt4f9bwa17b2gh");
                stmt.setString(3, text);
                stmt.setObject(4, new PGvector(embedding));
                stmt.setObject(5, "SALE_ORDER_" + saleOrderId);
                stmt.executeUpdate();
            }

            

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Save in DB
        // TextEmbedding entity = new TextEmbedding();
        // entity.setOriginalText(text);
        // entity.setEmbedding(embedding);
        // repository.save(entity);

        return embedding;
    }

    public static String generateSecureString(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    public List<Float> generateAndSaveProductObjectEmbedding(String text, String productId) {
        // Call OpenAI
        List<Float> embedding = getEmbedding(text);

        String url = "jdbc:postgresql://localhost:5433/next_ai";
        String user = "postgres";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to PostgreSQL!");

            PreparedStatement selectStmt = conn.prepareStatement("SELECT * FROM embeddings WHERE object_id = ?");
            selectStmt.setString(1, "PRODUCT_" + productId);
            ResultSet resultSet = selectStmt.executeQuery();

            boolean alredyRecordExist = resultSet.next();
            System.err.println(alredyRecordExist);

            if (alredyRecordExist) {

                String id = resultSet.getString("id");

                System.err.println("Update Recored..!!");

                String sql = "UPDATE embeddings SET resource_id = ?, content = ?, embedding = ?, object_id = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, "lw5gj3mnt4f9bwa17b2gh");
                stmt.setString(2, text);
                stmt.setObject(3, new PGvector(embedding));
                stmt.setObject(4, "PRODUCT_" + productId);
                stmt.setObject(5, id);

                stmt.executeUpdate();

            } else {
                System.err.println("Insert Recored..!!");
                String sql = "INSERT INTO embeddings (id, resource_id, content, embedding, object_id) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, generateSecureString(21));
                stmt.setString(2, "lw5gj3mnt4f9bwa17b2gh");
                stmt.setString(3, text);
                stmt.setObject(4, new PGvector(embedding));
                stmt.setObject(5, "PRODUCT_" + productId);
                stmt.executeUpdate();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return embedding;
    }

    public List<Float> generateAndSavePartnerObjectEmbedding(String text, String partnerId) {
        // Call OpenAI
        List<Float> embedding = getEmbedding(text);

        String url = "jdbc:postgresql://localhost:5433/next_ai";
        String user = "postgres";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to PostgreSQL!");

            PreparedStatement selectStmt = conn.prepareStatement("SELECT * FROM embeddings WHERE object_id = ?");
            selectStmt.setString(1, "PARTNER_" + partnerId);
            ResultSet resultSet = selectStmt.executeQuery();

            boolean alredyRecordExist = resultSet.next();
            System.err.println(alredyRecordExist);

            if (alredyRecordExist) {

                String id = resultSet.getString("id");

                System.err.println("Update Recored..!!");

                String sql = "UPDATE embeddings SET resource_id = ?, content = ?, embedding = ?, object_id = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, "lw5gj3mnt4f9bwa17b2gh");
                stmt.setString(2, text);
                stmt.setObject(3, new PGvector(embedding));
                stmt.setObject(4, "PARTNER_" + partnerId);
                stmt.setObject(5, id);

                stmt.executeUpdate();

            } else {
                System.err.println("Insert Recored..!!");
                String sql = "INSERT INTO embeddings (id, resource_id, content, embedding, object_id) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, generateSecureString(21));
                stmt.setString(2, "lw5gj3mnt4f9bwa17b2gh");
                stmt.setString(3, text);
                stmt.setObject(4, new PGvector(embedding));
                stmt.setObject(5, "PARTNER_" + partnerId);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return embedding;
    }

}
