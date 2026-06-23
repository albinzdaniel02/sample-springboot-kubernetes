package com.example.samplespringboot.controller;

import com.example.samplespringboot.exception.BookNotFoundException;
import com.example.samplespringboot.model.Book;
import com.example.samplespringboot.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllBooks() throws Exception {
        Book book1 = new Book(1L, "Book One", "Author One", "111111", 10.0);
        Book book2 = new Book(2L, "Book Two", "Author Two", "222222", 20.0);
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(book1, book2));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Book One"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Book Two"));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    public void testGetBookById_Success() throws Exception {
        Book book = new Book(1L, "Book One", "Author One", "111111", 10.0);
        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book One"))
                .andExpect(jsonPath("$.author").value("Author One"))
                .andExpect(jsonPath("$.isbn").value("111111"))
                .andExpect(jsonPath("$.price").value(10.0));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    public void testGetBookById_NotFound() throws Exception {
        when(bookService.getBookById(999L)).thenThrow(new BookNotFoundException("Book not found with id: 999"));

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(999L);
    }

    @Test
    public void testCreateBook() throws Exception {
        Book bookInput = new Book(null, "New Book", "Author", "ISBN", 15.0);
        Book savedBook = new Book(1L, "New Book", "Author", "ISBN", 15.0);
        when(bookService.createBook(any(Book.class))).thenReturn(savedBook);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookInput)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.author").value("Author"))
                .andExpect(jsonPath("$.isbn").value("ISBN"))
                .andExpect(jsonPath("$.price").value(15.0));

        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    public void testUpdateBook_Success() throws Exception {
        Book updateInfo = new Book(null, "Updated Title", "Updated Author", "ISBN", 25.0);
        Book updatedBook = new Book(1L, "Updated Title", "Updated Author", "ISBN", 25.0);
        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.author").value("Updated Author"))
                .andExpect(jsonPath("$.isbn").value("ISBN"))
                .andExpect(jsonPath("$.price").value(25.0));

        verify(bookService, times(1)).updateBook(eq(1L), any(Book.class));
    }

    @Test
    public void testUpdateBook_NotFound() throws Exception {
        Book updateInfo = new Book(null, "Updated Title", "Updated Author", "ISBN", 25.0);
        when(bookService.updateBook(eq(999L), any(Book.class))).thenThrow(new BookNotFoundException("Book not found with id: 999"));

        mockMvc.perform(put("/api/books/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateInfo)))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).updateBook(eq(999L), any(Book.class));
    }

    @Test
    public void testDeleteBook_Success() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    public void testDeleteBook_NotFound() throws Exception {
        doThrow(new BookNotFoundException("Book not found with id: 999")).when(bookService).deleteBook(999L);

        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).deleteBook(999L);
    }
}
