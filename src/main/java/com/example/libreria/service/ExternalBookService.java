package com.example.libreria.service;

import com.example.libreria.dto.ExternalBookDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ExternalBookService {
    // TODO: completar llamada a la API externa (ver bien todo el proyecto...)
    
    private final RestTemplate restTemplate;

    private static final String EXTERNAL_API_URL = "https://my-json-server.typicode.com/Gabriel-Arriola-UTN/libros/books";


    public ExternalBookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public List<ExternalBookDTO> fetchAllBooks() {
        try {

            ResponseEntity<List<ExternalBookDTO>> response = restTemplate.exchange(
                    EXTERNAL_API_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ExternalBookDTO>>() {}
            );
            
            List<ExternalBookDTO> books = response.getBody();
            return books != null ? books : Collections.emptyList();
        } catch (RestClientException e) {
            throw new RuntimeException("Error al obtener libros de la API externa: " + e.getMessage(), e);
        }
    }
    
    public ExternalBookDTO fetchBookById(Long id) {
        try {

            String url = EXTERNAL_API_URL + "/" + id;
            ExternalBookDTO book = restTemplate.getForObject(url, ExternalBookDTO.class);

            return book;
        } catch (RestClientException e) {

            throw new RuntimeException("Error al obtener el libro de la API externa: " + e.getMessage(), e);
        }
    }
}

