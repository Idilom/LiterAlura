package br.com.alura.idilom.literalura.models;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "book_languages", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "language")
    private Set<String> languages = new HashSet<>();

    @Column(name = "download_count")
    private int downloadCount;

    public Book() {}

    public Book(String title, List<Author> authors, Set<String> languages, int downloadCount) {
        this.title = title;
        this.authors = authors != null ? authors : new ArrayList<>();
        this.languages = languages != null ? languages : new HashSet<>();
        this.downloadCount = downloadCount;


        for (Author author : this.authors) {
            if (!author.getBooks().contains(this)) {
                author.getBooks().add(this);
            }
        }
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<Author> getAuthors() { return authors; }
    public void setAuthors(List<Author> authors) { this.authors = authors; }

    public Set<String> getLanguages() { return languages; }
    public void setLanguages(Set<String> languages) { this.languages = languages; }

    public int getDownloadCount() { return downloadCount; }
    public void setDownloadCount(int downloadCount) { this.downloadCount = downloadCount; }


    public BookDto toDto() {
        List<AuthorDto> authorDtos = this.authors.stream()
                .map(a -> new AuthorDto(a.getName(), a.getBirthYear(), a.getDeathYear()))
                .toList();

        List<String> languageList = new ArrayList<>(this.languages);

        return new BookDto(
                this.title,
                authorDtos,
                languageList,
                this.downloadCount
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return id != null && id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return title + " - Authors: " + authors.stream().map(Author::getName).toList();
    }
}

