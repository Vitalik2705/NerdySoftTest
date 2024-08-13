package com.example.nerdysoft.service;

import com.example.nerdysoft.book.api.dto.BookDTO;
import com.example.nerdysoft.book.api.mapper.BookMapper;
import com.example.nerdysoft.book.domain.entity.BookEntity;
import com.example.nerdysoft.book.repository.BookRepository;
import com.example.nerdysoft.member.api.dto.MemberDTO;
import com.example.nerdysoft.member.api.mapper.MemberMapper;
import com.example.nerdysoft.member.domain.entity.MemberEntity;
import com.example.nerdysoft.member.domain.service.impl.MemberServiceImpl;
import com.example.nerdysoft.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    private int maxBooksAllowed = 10;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(memberService, "maxBooksAllowed", 10);
    }

    @Test
    void addMember_ShouldSaveAndReturnMember() {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setName("John Doe");

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setName("John Doe");

        when(memberMapper.mapToMemberEntity(memberDTO)).thenReturn(memberEntity);
        when(memberRepository.save(memberEntity)).thenReturn(memberEntity);
        when(memberMapper.mapToMemberDTO(memberEntity)).thenReturn(memberDTO);

        MemberDTO result = memberService.addMember(memberDTO);

        assertEquals("John Doe", result.getName());
        verify(memberRepository, times(1)).save(memberEntity);
    }

    @Test
    void getMembers_ShouldReturnListOfMembers() {
        MemberEntity memberEntity1 = new MemberEntity();
        memberEntity1.setName("John Doe");

        MemberEntity memberEntity2 = new MemberEntity();
        memberEntity2.setName("Jane Doe");

        MemberDTO memberDTO1 = new MemberDTO();
        memberDTO1.setName("John Doe");

        MemberDTO memberDTO2 = new MemberDTO();
        memberDTO2.setName("Jane Doe");

        when(memberRepository.findAll()).thenReturn(Arrays.asList(memberEntity1, memberEntity2));
        when(memberMapper.mapToMemberDTO(memberEntity1)).thenReturn(memberDTO1);
        when(memberMapper.mapToMemberDTO(memberEntity2)).thenReturn(memberDTO2);

        List<MemberDTO> result = memberService.getMembers();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(member -> "John Doe".equals(member.getName())));
        assertTrue(result.stream().anyMatch(member -> "Jane Doe".equals(member.getName())));
    }

    @Test
    void deleteMember_MemberHasBorrowedBooks_ShouldThrowException() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setBorrowedBooks(Collections.singleton(new BookEntity()));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberEntity));

        assertThrows(IllegalStateException.class, () -> memberService.deleteMember(1L));
    }

    @Test
    void deleteMember_MemberHasNoBorrowedBooks_ShouldDelete() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setBorrowedBooks(Collections.emptySet());

        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberEntity));

        memberService.deleteMember(1L);

        verify(memberRepository, times(1)).delete(memberEntity);
    }

    @Test
    void getMemberById_ShouldReturnMemberDTO() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setName("John Doe");

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setName("John Doe");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberEntity));
        when(memberMapper.mapToMemberDTO(memberEntity)).thenReturn(memberDTO);

        MemberDTO result = memberService.getMemberById(1L);

        assertEquals("John Doe", result.getName());
    }

    @Test
    void updateMember_ShouldUpdateAndReturnMemberDTO() {
        // Given
        MemberEntity existingMemberEntity = new MemberEntity();
        existingMemberEntity.setName("Old Name");

        MemberDTO updatedMemberDTO = new MemberDTO();
        updatedMemberDTO.setName("New Name");

        MemberEntity updatedMemberEntity = new MemberEntity();
        updatedMemberEntity.setName("New Name");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMemberEntity));
        when(memberRepository.save(existingMemberEntity)).thenReturn(updatedMemberEntity);
        when(memberMapper.mapToMemberDTO(updatedMemberEntity)).thenReturn(updatedMemberDTO);

        MemberDTO result = memberService.updateMember(1L, updatedMemberDTO);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(memberRepository).save(existingMemberEntity);
    }


    @Test
    void borrowBook_MemberHasNotBorrowedMaxBooks_ShouldBorrowAndReturn() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setBorrowedBooks(new HashSet<>());
        memberEntity.setName("John Doe");

        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle("New Title");
        bookEntity.setAuthor("New Author");
        bookEntity.setAmount(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberEntity));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntity));
        when(memberMapper.mapToMemberDTO(memberEntity)).thenReturn(new MemberDTO());

        MemberDTO result = memberService.borrowBook(1L, 1L);

        assertNotNull(result);
        assertEquals(0, bookEntity.getAmount());
    }

    @Test
    void getBorrowedBooksByMemberName_ShouldReturnBooks() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setBorrowedBooks(new HashSet<>());

        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle("Book Title");

        memberEntity.getBorrowedBooks().add(bookEntity);

        when(memberRepository.findByName("John Doe")).thenReturn(Optional.of(memberEntity));
        when(bookMapper.mapToBookDTOList(memberEntity.getBorrowedBooks())).thenReturn(Collections.singleton(new BookDTO()));

        Set<BookDTO> result = memberService.getBorrowedBooksByMemberName("John Doe");

        assertEquals(1, result.size());
    }
}

