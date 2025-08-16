package br.com.alura.idilom.literalura.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthorDto(
        @JsonProperty("name") String name,
        @JsonProperty("birth_year") Integer birthYear,
        @JsonProperty("death_year") Integer deathYear
) {
    @Override
    public String toString() {
        return "Autor: " + name;
    }
}
