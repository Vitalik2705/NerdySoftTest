package com.example.nerdysoft.member.domain.service.impl;

import com.example.nerdysoft.book.api.dto.BookDTO;
import com.example.nerdysoft.book.api.mapper.BookMapper;
import com.example.nerdysoft.book.domain.entity.BookEntity;
import com.example.nerdysoft.book.repository.BookRepository;
import com.example.nerdysoft.member.api.dto.MemberDTO;
import com.example.nerdysoft.member.api.mapper.MemberMapper;
import com.example.nerdysoft.member.domain.entity.MemberEntity;
import com.example.nerdysoft.member.domain.service.MemberService;
import com.example.nerdysoft.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final MemberMapper memberMapper;
    private final BookMapper bookMapper;

    @Value("${constraints.maxBooksAllowed}")
    private int maxBooksAllowed;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, BookRepository bookRepository, MemberMapper memberMapper, BookMapper bookMapper) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.memberMapper = memberMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    public MemberDTO addMember(MemberDTO memberDTO) {
        MemberEntity memberEntity = memberMapper.mapToMemberEntity(memberDTO);

        if (memberEntity.getMembershipDate() == null) {
            memberEntity.setMembershipDate(LocalDateTime.now());
        }

        memberEntity = memberRepository.save(memberEntity);

        return memberMapper.mapToMemberDTO(memberEntity);
    }

    @Override
    public List<MemberDTO> getMembers() {
        List<MemberEntity> memberEntities = memberRepository.findAll();

        return memberEntities.stream()
                .map(memberMapper::mapToMemberDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMember(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!memberEntity.getBorrowedBooks().isEmpty()) {
            throw new IllegalStateException("Member cannot be deleted because they have borrowed books.");
        }

        memberRepository.delete(memberEntity);
    }


    @Override
    public MemberDTO getMemberById(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return memberMapper.mapToMemberDTO(memberEntity);
    }

    @Override
    public MemberDTO updateMember(Long memberId, MemberDTO updatedMemberDTO) {
        MemberEntity existingMemberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        existingMemberEntity.setName(updatedMemberDTO.getName());

        MemberEntity updatedMemberEntity = memberRepository.save(existingMemberEntity);

        return memberMapper.mapToMemberDTO(updatedMemberEntity);
    }

    public MemberDTO borrowBook(Long memberId, Long bookId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (memberEntity.getBorrowedBooks().size() >= maxBooksAllowed) {
            throw new IllegalStateException("Member has already borrowed the maximum number of books (" + maxBooksAllowed + ")");
        }

        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (bookEntity.getAmount() <= 0) {
            throw new IllegalStateException("Book is not available");
        }

        if (memberEntity.getBorrowedBooks().contains(bookEntity)) {
            throw new IllegalStateException("The member has already borrowed this book");
        }

        bookEntity.setAmount(bookEntity.getAmount() - 1);
        bookRepository.save(bookEntity);

        memberEntity.getBorrowedBooks().add(bookEntity);
        memberRepository.save(memberEntity);

        MemberDTO memberDTO = memberMapper.mapToMemberDTO(memberEntity);

        return memberDTO;
    }

    public MemberDTO returnBook(Long memberId, Long bookId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!memberEntity.getBorrowedBooks().contains(bookEntity)) {
            throw new IllegalStateException("The member has not borrowed this book");
        }

        memberEntity.getBorrowedBooks().remove(bookEntity);
        memberRepository.save(memberEntity);

        bookEntity.setAmount(bookEntity.getAmount() + 1);
        bookRepository.save(bookEntity);

        MemberDTO memberDTO = memberMapper.mapToMemberDTO(memberEntity);
        memberDTO.setBorrowedBooks(bookMapper.mapToBookDTOList(memberEntity.getBorrowedBooks()));
        return memberDTO;
    }

    @Override
    public Set<BookDTO> getBorrowedBooksByMemberName(String memberName) {
        MemberEntity memberEntity = memberRepository.findByName(memberName)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return bookMapper.mapToBookDTOList(memberEntity.getBorrowedBooks());
    }
}

