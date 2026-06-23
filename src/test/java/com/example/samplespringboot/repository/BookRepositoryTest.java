package com.example.samplespringboot.repository;

import com.example.samplespringboot.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testSaveBook() {
        Book book = new Book(null, "Test Title", "Test Author", "1234567890", 29.99);
        Book savedBook = bookRepository.save(book);

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Test Title");
        assertThat(savedBook.getAuthor()).isEqualTo("Test Author");
        assertThat(savedBook.getIsbn()).isEqualTo("1234567890");
        assertThat(savedBook.getPrice()).isEqualTo(29.99);
    }

    @Test
    public void testFindBookById() {
        Book book = new Book(null, "Test Title", "Test Author", "1234567890", 29.99);
        Book savedBook = bookRepository.save(book);

        Optional<Book> foundBookOpt = bookRepository.findById(savedBook.getId());

        assertThat(foundBookOpt).isPresent();
        assertThat(foundBookOpt.get().getTitle()).isEqualTo("Test Title");
    }

    @Test
    public void testFindAllBooks() {
        Book book1 = new Book(null, "Book 1", "Author 1", "ISBN1", 10.0);
        Book book2 = new Book(null, "Book 2", "Author 2", "ISBN2", 20.0);
        bookRepository.save(book1);
        bookRepository.save(book2);

        List<Book> books = bookRepository.findAll();

        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle).containsExactlyInAnyOrder("Book 1", "Book 2");
    }

    @Test
    public void testUpdateBook() {
        Book book = new Book(null, "Original Title", "Original Author", "ISBN", 15.0);
        Book savedBook = bookRepository.save(book);

        savedBook.setTitle("Updated Title");
        savedBook.setPrice(19.99);
        Book updatedBook = bookRepository.saveAndFlush(savedBook);

        Optional<Book> foundBookOpt = bookRepository.findById(updatedBook.getId());
        assertThat(foundBookOpt).isPresent();
        assertThat(foundBookOpt.get().getTitle()).isEqualTo("Updated Title");
        assertThat(foundBookOpt.get().getPrice()).isEqualTo(19.99);
    }

    @Test
    public void testDeleteBook() {
        Book book = new Book(null, "Book to Delete", "Author", "ISBN", 5.0);
        Book savedBook = bookRepository.save(book);
        Long id = savedBook.getId();

        bookRepository.deleteById(id);
        bookRepository.flush();

        Optional<Book> foundBookOpt = bookRepository.findById(id);
        assertThat(foundBookOpt).isNotPresent();
    }
}
