package br.com.alura.idilom.literalura.service;

import br.com.alura.idilom.literalura.models.BookDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GutendexResponse(
        int count,
        String next,
        List<BookDto> results
) {}
