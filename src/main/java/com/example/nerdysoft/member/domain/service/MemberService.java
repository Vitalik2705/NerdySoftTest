package com.example.nerdysoft.member.domain.service;

import com.example.nerdysoft.book.api.dto.BookDTO;
import com.example.nerdysoft.member.api.dto.MemberDTO;

import java.util.List;
import java.util.Set;

public interface MemberService {
    MemberDTO addMember(MemberDTO memberDTO);
    List<MemberDTO> getMembers();
    void deleteMember(Long memberId);
    MemberDTO getMemberById(Long memberId);
    MemberDTO updateMember(Long memberId, MemberDTO updatedMember);
    MemberDTO borrowBook(Long memberId, Long bookId);
    MemberDTO returnBook(Long memberId, Long bookId);
    Set<BookDTO> getBorrowedBooksByMemberName(String memberName);
}
