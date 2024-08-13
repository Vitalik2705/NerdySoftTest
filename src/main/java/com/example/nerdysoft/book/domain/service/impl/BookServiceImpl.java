package com.example.nerdysoft.book.domain.service.impl;

import com.example.nerdysoft.book.api.dto.BookDTO;
import com.example.nerdysoft.book.api.mapper.BookMapper;
import com.example.nerdysoft.book.domain.entity.BookEntity;
import com.example.nerdysoft.book.domain.service.BookService;
import com.example.nerdysoft.book.repository.BookRepository;
import com.example.nerdysoft.member.domain.entity.MemberEntity;
import com.example.nerdysoft.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BookMapper bookMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, MemberRepository memberRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        Optional<BookEntity> existingBook = bookRepository.findByTitleAndAuthor(bookDTO.getTitle(), bookDTO.getAuthor());

        if (existingBook.isPresent()) {
            BookEntity bookEntity = existingBook.get();
            bookEntity.setAmount(bookEntity.getAmount() + bookDTO.getAmount());

            bookEntity = bookRepository.save(bookEntity);

            return bookMapper.mapToBookDTO(bookEntity);
        } else {
            BookEntity newBookEntity = bookMapper.mapToBookEntity(bookDTO);

            newBookEntity = bookRepository.save(newBookEntity);

            return bookMapper.mapToBookDTO(newBookEntity);
        }
    }


    @Override
    public List<BookDTO> getBooks() {
        List<BookEntity> bookEntities = bookRepository.findAll();

        return bookEntities.stream()
                .map(bookMapper::mapToBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO deleteBook(Long bookId) {
        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        boolean isBorrowed = memberRepository.findAll().stream()
                .anyMatch(member -> member.getBorrowedBooks().contains(bookEntity));

        if (isBorrowed) {
            throw new IllegalStateException("Book cannot be deleted because it is currently borrowed");
        }

        bookRepository.delete(bookEntity);

        return bookMapper.mapToBookDTO(bookEntity);
    }

    @Override
    public BookDTO getBookById(Long bookId) {
        Optional<BookEntity> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {
            return bookMapper.mapToBookDTO(optionalBook.get());
        } else {
            throw new EntityNotFoundException("Book not found");
        }
    }

    @Override
    public BookDTO updateBook(Long bookId, BookDTO updatedBook) {
        Optional<BookEntity> optionalBook = bookRepository.findById(bookId);

        if (optionalBook.isPresent()) {
            BookEntity bookEntity = optionalBook.get();

            bookEntity.setTitle(updatedBook.getTitle());
            bookEntity.setAuthor(updatedBook.getAuthor());
            bookEntity.setAmount(updatedBook.getAmount());

            bookEntity = bookRepository.save(bookEntity);

            return bookMapper.mapToBookDTO(bookEntity);
        } else {
            throw new EntityNotFoundException("Book not found");
        }
    }

    @Override
    public List<String> getDistinctBorrowedBookNames() {
        List<MemberEntity> members = memberRepository.findAll();

        return members.stream()
                .flatMap(member -> member.getBorrowedBooks().stream())
                .map(BookEntity::getTitle).distinct().collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> getDistinctBorrowedBookNamesWithCounts() {
        List<MemberEntity> members = memberRepository.findAll();

        Map<String, Integer> borrowedBooksCount = new HashMap<>();

        for (MemberEntity member : members) {
            for (BookEntity book : member.getBorrowedBooks()) {
                borrowedBooksCount.merge(book.getTitle(), 1, Integer::sum);
            }
        }

        return borrowedBooksCount;
    }
}

