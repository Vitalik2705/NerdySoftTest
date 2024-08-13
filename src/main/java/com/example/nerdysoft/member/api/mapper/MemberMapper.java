package com.example.nerdysoft.member.api.mapper;

import com.example.nerdysoft.book.api.dto.BookDTO;
import com.example.nerdysoft.book.domain.entity.BookEntity;
import com.example.nerdysoft.book.repository.BookRepository;
import com.example.nerdysoft.member.api.dto.MemberDTO;
import com.example.nerdysoft.member.domain.entity.MemberEntity;
import com.example.nerdysoft.member.repository.MemberRepository;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
        typeConversionPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        disableSubMappingMethodsGeneration = true,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {MemberRepository.class})
public interface MemberMapper {
    @Mapping(target = "borrowedBooks", source = "borrowedBooks")
    MemberDTO mapToMemberDTO(MemberEntity memberEntity);
    @Mapping(target = "borrowedBooks", ignore = true)
    @Mapping(target = "ID", ignore = true)
    MemberEntity mapToMemberEntity(MemberDTO memberDTO);
    BookDTO mapToBookDTO(BookEntity bookEntity);
    Set<BookDTO> mapToBookDTOList(Collection<BookEntity> bookEntitySet);
}
