package com.example.nerdysoft.book.api.dto;

import lombok.Data;

@Data
public class BookDTO {
    private String title;
    private String author;
    private Long amount;
}
