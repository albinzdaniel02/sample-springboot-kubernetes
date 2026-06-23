package com.example.samplespringboot.service;

import com.example.samplespringboot.exception.BookNotFoundException;
import com.example.samplespringboot.model.Book;
import com.example.samplespringboot.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    public void testGetAllBooks() {
        Book book1 = new Book(1L, "Book One", "Author One", "111111", 10.0);
        Book book2 = new Book(2L, "Book Two", "Author Two", "222222", 20.0);
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<Book> books = bookService.getAllBooks();

        assertThat(books).hasSize(2);
        assertThat(books.get(0).getTitle()).isEqualTo("Book One");
        assertThat(books.get(1).getTitle()).isEqualTo("Book Two");
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    public void testGetBookById_Success() {
        Book book = new Book(1L, "Book One", "Author One", "111111", 10.0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Book One");
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(1L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book not found with id: 1");

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateBook() {
        Book bookInput = new Book(null, "New Book", "Author", "ISBN", 15.0);
        Book savedBook = new Book(1L, "New Book", "Author", "ISBN", 15.0);
        when(bookRepository.save(bookInput)).thenReturn(savedBook);

        Book result = bookService.createBook(bookInput);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("New Book");
        verify(bookRepository, times(1)).save(bookInput);
    }

    @Test
    public void testUpdateBook_Success() {
        Book existingBook = new Book(1L, "Old Title", "Old Author", "ISBN", 15.0);
        Book updateInfo = new Book(null, "Updated Title", "Updated Author", "ISBN", 25.0);
        Book updatedBook = new Book(1L, "Updated Title", "Updated Author", "ISBN", 25.0);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        Book result = bookService.updateBook(1L, updateInfo);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getAuthor()).isEqualTo("Updated Author");
        assertThat(result.getPrice()).isEqualTo(25.0);
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    public void testUpdateBook_NotFound() {
        Book updateInfo = new Book(null, "Updated Title", "Updated Author", "ISBN", 25.0);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(1L, updateInfo))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book not found with id: 1");

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void testDeleteBook_Success() {
        Book existingBook = new Book(1L, "Book to Delete", "Author", "ISBN", 15.0);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        doNothing().when(bookRepository).delete(existingBook);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).delete(existingBook);
    }

    @Test
    public void testDeleteBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(1L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book not found with id: 1");

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).delete(any(Book.class));
    }
}
