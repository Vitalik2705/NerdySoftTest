package com.example.nerdysoft.book.domain.entity;

import com.example.nerdysoft.member.domain.entity.MemberEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @NotBlank(message = "Title is required")
    @Size(min = 3, message = "Title must be at least 3 characters long")
    @Pattern(regexp = "^[A-Z][a-zA-Z]*(\\s[a-zA-Z]*)*$", message = "Title must start with a capital letter, followed by any combination of letters and spaces")
    private String title;

    @NotBlank(message = "Author name is required")
    @Pattern(regexp = "^[A-Z][a-z]+\\s[A-Z][a-z]+$", message = "Author must contain two capitalized words (e.g., 'Paulo Coelho')")
    private String author;

    @PositiveOrZero
    private Long amount;

    @ManyToMany(mappedBy = "borrowedBooks")
    private Set<MemberEntity> borrowers = new HashSet<>();
}
