package br.com.alura.idilom.literalura.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookDto(
        String title,
        List<AuthorDto> authors,
        List<String> languages,
        @JsonProperty("download_count") int downloadCount
) {  @Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Título: ").append(title).append("\n");
    sb.append("Autor(es): \n");
    for (AuthorDto autor : authors) {
        sb.append("  - ").append(autor.name()).append("\n");
    }
    sb.append("Idioma(s): ").append(String.join(", ", languages)).append("\n");
    sb.append("Downloads: ").append(downloadCount).append("\n");
    sb.append("----------------------------------------");
    return sb.toString();
}

}
