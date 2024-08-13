package com.example.nerdysoft.book.repository;

import com.example.nerdysoft.book.domain.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    Optional<BookEntity> findByTitleAndAuthor(String title, String author);
}
