package com.example.nerdysoft.member.api.dto;

import com.example.nerdysoft.book.api.dto.BookDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class MemberDTO {
    private String name;
    private LocalDateTime membershipDate;
    private Set<BookDTO> borrowedBooks = new HashSet<>();
}
