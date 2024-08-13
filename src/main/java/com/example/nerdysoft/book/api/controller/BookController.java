package com.example.nerdysoft.book.api.controller;

import com.example.nerdysoft.book.api.dto.BookDTO;
import com.example.nerdysoft.book.domain.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        BookDTO createdBook = bookService.addBook(bookDTO);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getBooks() {
        List<BookDTO> books = bookService.getBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable("id") Long bookId) {
        BookDTO book = bookService.getBookById(bookId);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable("id") Long bookId, @RequestBody BookDTO updatedBook) {
        BookDTO book = bookService.updateBook(bookId, updatedBook);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookDTO> deleteBook(@PathVariable("id") Long bookId) {
        BookDTO deletedBook = bookService.deleteBook(bookId);
        return new ResponseEntity<>(deletedBook, HttpStatus.OK);
    }

    @GetMapping("/distinct-borrowed-names")
    public ResponseEntity<List<String>> getDistinctBorrowedBookNames() {
        List<String> bookNames = bookService.getDistinctBorrowedBookNames();
        return new ResponseEntity<>(bookNames, HttpStatus.OK);
    }

    @GetMapping("/distinct-borrowed-names-with-counts")
    public ResponseEntity<Map<String, Integer>> getDistinctBorrowedBookNamesWithCounts() {
        Map<String, Integer> bookNamesWithCounts = bookService.getDistinctBorrowedBookNamesWithCounts();
        return new ResponseEntity<>(bookNamesWithCounts, HttpStatus.OK);
    }
}

