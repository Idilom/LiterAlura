package br.com.alura.idilom.literalura.repository;

import br.com.alura.idilom.literalura.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findByBirthYear(Integer birthYear);

    List<Author> findByDeathYear(Integer deathYear);

    @Query("SELECT a FROM Author a WHERE a.birthYear <= :ano AND (a.deathYear IS NULL OR a.deathYear >= :ano)")
    List<Author> findAliveInYear(@Param("ano") int ano);

    List<Author> findByNameIgnoreCase(String name);

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books")
    List<Author> findAllWithBooks();

}
