package com.example.nerdysoft.service;

import com.example.nerdysoft.book.api.dto.BookDTO;
import com.example.nerdysoft.book.api.mapper.BookMapper;
import com.example.nerdysoft.book.domain.entity.BookEntity;
import com.example.nerdysoft.book.domain.service.impl.BookServiceImpl;
import com.example.nerdysoft.book.repository.BookRepository;
import com.example.nerdysoft.member.domain.entity.MemberEntity;
import com.example.nerdysoft.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addBook_BookExists_ShouldIncreaseAmount() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Title");
        bookDTO.setAuthor("Author");
        bookDTO.setAmount(1L);

        BookEntity existingBookEntity = new BookEntity();
        existingBookEntity.setTitle("Title");
        existingBookEntity.setAuthor("Author");
        existingBookEntity.setAmount(5L);

        when(bookRepository.findByTitleAndAuthor("Title", "Author"))
                .thenReturn(Optional.of(existingBookEntity));
        when(bookMapper.mapToBookEntity(bookDTO)).thenReturn(existingBookEntity);
        when(bookRepository.save(existingBookEntity)).thenReturn(existingBookEntity);
        when(bookMapper.mapToBookDTO(existingBookEntity)).thenReturn(bookDTO);

        BookDTO result = bookService.addBook(bookDTO);

        assertEquals(6, existingBookEntity.getAmount());
        verify(bookRepository, times(1)).save(existingBookEntity);
    }

    @Test
    void addBook_BookDoesNotExist_ShouldCreateNewBook() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("New Title");
        bookDTO.setAuthor("New Author");
        bookDTO.setAmount(1L);

        BookEntity newBookEntity = new BookEntity();
        newBookEntity.setTitle("New Title");
        newBookEntity.setAuthor("New Author");
        newBookEntity.setAmount(1L);

        when(bookRepository.findByTitleAndAuthor("New Title", "New Author"))
                .thenReturn(Optional.empty());
        when(bookMapper.mapToBookEntity(bookDTO)).thenReturn(newBookEntity);
        when(bookRepository.save(newBookEntity)).thenReturn(newBookEntity);
        when(bookMapper.mapToBookDTO(newBookEntity)).thenReturn(bookDTO);

        BookDTO result = bookService.addBook(bookDTO);

        assertEquals("New Title", result.getTitle());
        verify(bookRepository, times(1)).save(newBookEntity);
    }

    @Test
    void deleteBook_BookIsBorrowed_ShouldThrowException() {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setID(1L);

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setBorrowedBooks(Collections.singleton(bookEntity));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntity));
        when(memberRepository.findAll()).thenReturn(Collections.singletonList(memberEntity));

        assertThrows(IllegalStateException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    void deleteBook_BookIsNotBorrowed_ShouldDelete() {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setID(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntity));
        when(memberRepository.findAll()).thenReturn(Collections.emptyList());
        when(bookMapper.mapToBookDTO(bookEntity)).thenReturn(new BookDTO());

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(bookEntity);
    }

    @Test
    void getDistinctBorrowedBookNames_ShouldReturnDistinctTitles() {
        BookEntity book1 = new BookEntity();
        book1.setTitle("Title1");

        BookEntity book2 = new BookEntity();
        book2.setTitle("Title2");

        MemberEntity member1 = new MemberEntity();
        member1.setBorrowedBooks(new HashSet<>(Arrays.asList(book1, book2)));

        MemberEntity member2 = new MemberEntity();
        member2.setBorrowedBooks(Collections.singleton(book1));

        when(memberRepository.findAll()).thenReturn(Arrays.asList(member1, member2));

        List<String> result = bookService.getDistinctBorrowedBookNames();

        assertTrue(result.contains("Title1"));
        assertTrue(result.contains("Title2"));
        assertEquals(2, result.size());
    }

    @Test
    void getDistinctBorrowedBookNamesWithCounts_ShouldReturnBookCounts() {
        BookEntity book1 = new BookEntity();
        book1.setTitle("Title1");

        BookEntity book2 = new BookEntity();
        book2.setTitle("Title2");

        MemberEntity member1 = new MemberEntity();
        member1.setBorrowedBooks(new HashSet<>(Arrays.asList(book1, book2)));

        MemberEntity member2 = new MemberEntity();
        member2.setBorrowedBooks(Collections.singleton(book1));

        when(memberRepository.findAll()).thenReturn(Arrays.asList(member1, member2));

        Map<String, Integer> result = bookService.getDistinctBorrowedBookNamesWithCounts();

        assertEquals(2, result.size());
        assertEquals(2, result.get("Title1"));
        assertEquals(1, result.get("Title2"));
    }
}
