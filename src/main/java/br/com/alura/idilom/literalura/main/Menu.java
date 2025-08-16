package br.com.alura.idilom.literalura.main;

import br.com.alura.idilom.literalura.models.Author;
import br.com.alura.idilom.literalura.models.AuthorDto;
import br.com.alura.idilom.literalura.models.Book;
import br.com.alura.idilom.literalura.models.BookDto;
import br.com.alura.idilom.literalura.repository.AuthorRepository;
import br.com.alura.idilom.literalura.repository.BookRepository;
import br.com.alura.idilom.literalura.service.GutendexResponse;
import br.com.alura.idilom.literalura.service.GutendexService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;



@Component
public class Menu {

    private final GutendexService gutendexService;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final Scanner scanner = new Scanner(System.in);

    public Menu(GutendexService gutendexService, BookRepository bookRepository, AuthorRepository authorRepository) {
        this.gutendexService = gutendexService;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void exibir() {
        while (true) {
            System.out.println("============================================================");
            System.out.println("\n" +
                    "\n" +
                    " __         __     ______   ______     ______     ______     __         __  __     ______     ______   \n" +
                    "/\\ \\       /\\ \\   /\\__  _\\ /\\  ___\\   /\\  == \\   /\\  __ \\   /\\ \\       /\\ \\/\\ \\   /\\  == \\   /\\  __ \\  \n" +
                    "\\ \\ \\____  \\ \\ \\  \\/_/\\ \\/ \\ \\  __\\   \\ \\  __<   \\ \\  __ \\  \\ \\ \\____  \\ \\ \\_\\ \\  \\ \\  __<   \\ \\  __ \\ \n" +
                    " \\ \\_____\\  \\ \\_\\    \\ \\_\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\ \\_\\  \\ \\_____\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\ \\_\\\n" +
                    "  \\/_____/   \\/_/     \\/_/   \\/_____/   \\/_/ /_/   \\/_/\\/_/   \\/_____/   \\/_____/   \\/_/ /_/   \\/_/\\/_/\n" +
                    "\n");
            System.out.println("Escolha um número no menu abaixo:");
            System.out.println("------------------------------------------------------------");
            System.out.println("1- Buscar livros pelo título");
            System.out.println("2- Listar livros registrados");
            System.out.println("3- Listar autores registrados");
            System.out.println("4- Listar autores vivos em um determinado ano");
            System.out.println("5- Listar autores nascidos em determinado ano");
            System.out.println("6- Listar autores por ano de sua morte");
            System.out.println("7- Listar livros em um determinado idioma");
            System.out.println("0- Sair");
            System.out.print("Opção: ");

            int opcao;
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida!");
                continue;
            }

            switch (opcao) {
                case 1 -> buscarLivroPorTitulo();
                case 2 -> listarLivros();
                case 3 -> listarAutores();
                case 4 -> listarAutoresVivos();
                case 5 -> listarAutoresNascidos();
                case 6 -> listarAutoresFalecidos();
                case 7 -> listarLivrosPorIdioma();
                case 0 -> {
                    System.out.println("Aplicação Encerrada...");
                    System.exit(0);
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    @Transactional
    private void buscarLivroPorTitulo() {
        System.out.print("Digite o título do livro: ");
        String titulo = scanner.nextLine().trim(); // deixa o URLEncoder cuidar

        if (titulo.isEmpty()) {
            System.out.println("Título inválido!");
            return;
        }

        Optional<GutendexResponse> optionalResponse = gutendexService.searchBooks(titulo);

        optionalResponse.ifPresentOrElse(response -> {
            if (response.results().isEmpty()) {
                System.out.println("Nenhum livro encontrado na API.");
                return;
            }


            BookDto dto = response.results().getFirst();

            System.out.println("------------------------------------------------");
            System.out.println("Título: " + dto.title());

            String autoresFmt = dto.authors().isEmpty()
                    ? "—"
                    : dto.authors().stream().map(a -> {
                StringBuilder sb = new StringBuilder(a.name());
                if (a.birthYear() != null || a.deathYear() != null) {
                    sb.append(" (");
                    if (a.birthYear() != null) {
                        sb.append(a.birthYear());
                        if (a.deathYear() != null) {
                            sb.append(" - ");
                        }
                    }
                    if (a.deathYear() != null) {
                        sb.append(a.deathYear());
                    }
                    sb.append(")");
                }

                return sb.toString();
            }).collect(Collectors.joining(", "));
            System.out.println("Autor: " + autoresFmt);

            String idiomas = (dto.languages() == null || dto.languages().isEmpty())
                    ? "—"
                    : String.join(", ", dto.languages());
            System.out.println("Idioma: " + idiomas);

            long downloads = dto.downloadCount();
            System.out.println("Downloads: " + downloads);
            System.out.println("------------------------------------------------");


            boolean livroExiste = bookRepository.findByTitleIgnoreCase(dto.title())
                    .stream().findFirst().isPresent();
            if (livroExiste) {
                System.out.println("Livro já registrado: " + dto.title());
                return;
            }


            List<Author> autores = dto.authors().stream()
                    .map(aDto -> authorRepository.findByNameIgnoreCase(aDto.name())
                            .stream().findFirst()
                            .orElseGet(() -> new Author(aDto.name(), aDto.birthYear(), aDto.deathYear()))
                    ).collect(Collectors.toList());


            Book livro = new Book(
                    dto.title(),
                    autores,
                    new HashSet<>(dto.languages()), // converte List para Set
                    dto.downloadCount()
            );
            for (Author author : autores) {
                if (!author.getBooks().contains(livro)) {
                    author.getBooks().add(livro);
                }
            }

            authorRepository.saveAll(autores);
            bookRepository.save(livro);
            System.out.println("Livro encontrado e salvo: " + livro.getTitle());

        }, () -> System.out.println("Não foi possível buscar livros na API."));
    }



    @Transactional
    public void listarLivros() {

        List<Book> livros = bookRepository.findAllWithAuthorsAndLanguages();

        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado.");
            return;
        }


        List<BookDto> livrosDto = livros.stream()
                .map(Book::toDto)
                .toList();

        final int livrosPorPagina = 10;
        int totalLivros = livrosDto.size();
        int totalPaginas = (int) Math.ceil((double) totalLivros / livrosPorPagina);

        Scanner scanner = new Scanner(System.in);

        for (int pagina = 0; pagina < totalPaginas; pagina++) {
            int inicio = pagina * livrosPorPagina;
            int fim = Math.min(inicio + livrosPorPagina, totalLivros);

            System.out.println("\n=== Página " + (pagina + 1) + " de " + totalPaginas + " ===");

            livrosDto.subList(inicio, fim).forEach(dto -> System.out.println(dto.toString()));

            if (pagina < totalPaginas - 1) {
                System.out.print("Pressione ENTER para ver a próxima página...");
                scanner.nextLine();
            }
        }
    }

    private void listarAutores() {
        List<Author> autores = authorRepository.findAllWithBooks();

        if (autores.isEmpty()) {
            System.out.println("Nenhum autor registrado.");
            return;
        }

        for (Author a : autores) {
            System.out.println("Autor: " + a.getName());
            System.out.println("Ano de nascimento: " +
                    (a.getBirthYear() != null ? a.getBirthYear() : "Desconhecido"));
            System.out.println("Ano de falecimento: " +
                    (a.getDeathYear() != null ? a.getDeathYear() : "Desconhecido"));

            String livrosFmt = a.getBooks() != null && !a.getBooks().isEmpty()
                    ? a.getBooks().stream()
                    .map(Book::getTitle)
                    .collect(Collectors.joining(", ", "[", "]"))
                    : "[]";
            System.out.println("Livros: " + livrosFmt);
            System.out.println();
        }
    }





    private void listarAutoresVivos() {
        System.out.print("Digite o ano: ");
        int ano = lerAno();
        List<Author> vivos = authorRepository.findAll().stream()
                .filter(a -> a.getBirthYear() != null && a.getBirthYear() <= ano
                        && (a.getDeathYear() == null || a.getDeathYear() >= ano))
                .toList();

        if (vivos.isEmpty()) System.out.println("Nenhum autor vivo nesse ano.");
        else vivos.forEach(a -> System.out.println(a.getName()));
    }

    private void listarAutoresNascidos() {
        System.out.print("Digite o ano: ");
        int ano = lerAno();
        List<Author> nascidos = authorRepository.findAll().stream()
                .filter(a -> a.getBirthYear() != null && a.getBirthYear() == ano)
                .toList();

        if (nascidos.isEmpty()) System.out.println("Nenhum autor nasceu nesse ano.");
        else nascidos.forEach(a -> System.out.println(a.getName()));
    }

    private void listarAutoresFalecidos() {
        System.out.print("Digite o ano: ");
        int ano = lerAno();
        List<Author> falecidos = authorRepository.findAll().stream()
                .filter(a -> a.getDeathYear() != null && a.getDeathYear() == ano)
                .toList();

        if (falecidos.isEmpty()) System.out.println("Nenhum autor faleceu nesse ano.");
        else falecidos.forEach(a -> System.out.println(a.getName()));
    }

    @Transactional
    private void listarLivrosPorIdioma() {
        System.out.print("Digite o idioma: ");
        String idioma = scanner.nextLine().trim().toLowerCase();
        if (idioma.isEmpty()) {
            System.out.println("Idioma inválido!");
            return;
        }

        List<Book> livros = bookRepository.findByLanguageWithAuthors(idioma);
        livros.forEach(b -> System.out.println(b.getTitle() + " - Idiomas: " + b.getLanguages()));



    }

    private int lerAno() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ano inválido! Considerando 0.");
            return 0;
        }
    }
}
