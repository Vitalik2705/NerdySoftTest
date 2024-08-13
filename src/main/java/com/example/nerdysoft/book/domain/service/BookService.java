package com.example.nerdysoft.book.domain.service;

import com.example.nerdysoft.book.api.dto.BookDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface BookService {
    BookDTO addBook(BookDTO bookDTO);
    List<BookDTO> getBooks();
    BookDTO deleteBook(Long bookId);
    BookDTO getBookById(Long bookId);
    BookDTO updateBook(Long bookId, BookDTO updatedBook);
    List<String> getDistinctBorrowedBookNames();
    Map<String, Integer> getDistinctBorrowedBookNamesWithCounts();
}
