package br.com.alura.idilom.literalura.repository;

import br.com.alura.idilom.literalura.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleIgnoreCase(String title);

    @Query("SELECT b FROM Book b JOIN b.languages l WHERE LOWER(l) = LOWER(:language)")
    List<Book> findByLanguage(@Param("language") String language);

    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN FETCH b.authors " +
            "LEFT JOIN FETCH b.languages")
    List<Book> findAllWithAuthorsAndLanguages();

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authors LEFT JOIN FETCH b.languages l " +
            "WHERE LOWER(l) = LOWER(:language)")
    List<Book> findByLanguageWithAuthors(@Param("language") String language);
}
