package br.com.alura.idilom.literalura.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class GutendexService {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public GutendexService(ObjectMapper objectMapper) {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = objectMapper
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public Optional<GutendexResponse> searchBooks(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://gutendex.com/books/?search=" + encodedQuery;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                GutendexResponse gutendexResponse = objectMapper.readValue(response.body(), GutendexResponse.class);
                return Optional.of(gutendexResponse);
            } else {
                System.err.println("Erro na requisição Gutendex: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao acessar Gutendex: " + e.getMessage());
        }
        return Optional.empty();
    }
}
