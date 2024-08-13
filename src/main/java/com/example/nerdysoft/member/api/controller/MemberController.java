package com.example.nerdysoft.member.api.controller;

import com.example.nerdysoft.book.api.dto.BookDTO;
import com.example.nerdysoft.member.api.dto.MemberDTO;
import com.example.nerdysoft.member.domain.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberDTO> addMember(@RequestBody MemberDTO memberDTO) {
        MemberDTO createdMember = memberService.addMember(memberDTO);
        return new ResponseEntity<>(createdMember, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getMembers() {
        List<MemberDTO> members = memberService.getMembers();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        MemberDTO member = memberService.getMemberById(id);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO) {
        MemberDTO updatedMember = memberService.updateMember(id, memberDTO);
        return new ResponseEntity<>(updatedMember, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{memberId}/borrow/{bookId}")
    public ResponseEntity<MemberDTO> borrowBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        MemberDTO updatedMember = memberService.borrowBook(memberId, bookId);
        return new ResponseEntity<>(updatedMember, HttpStatus.OK);
    }

    @PostMapping("/{memberId}/return/{bookId}")
    public ResponseEntity<MemberDTO> returnBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        MemberDTO updatedMember = memberService.returnBook(memberId, bookId);
        return new ResponseEntity<>(updatedMember, HttpStatus.OK);
    }

    @GetMapping("/{name}/borrowed-books")
    public ResponseEntity<Set<BookDTO>> getBorrowedBooksByMemberName(@PathVariable String name) {
        Set<BookDTO> borrowedBooks = memberService.getBorrowedBooksByMemberName(name);
        return new ResponseEntity<>(borrowedBooks, HttpStatus.OK);
    }
}

