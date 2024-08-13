package com.example.nerdysoft.book.api.mapper;

import com.example.nerdysoft.book.api.dto.BookDTO;
import com.example.nerdysoft.book.domain.entity.BookEntity;
import com.example.nerdysoft.book.repository.BookRepository;
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
        uses = {BookRepository.class})
public interface BookMapper {
    BookDTO mapToBookDTO(BookEntity bookEntity);
    @Mapping(target = "borrowers", ignore = true)
    @Mapping(target = "ID", ignore = true)
    BookEntity mapToBookEntity(BookDTO bookDTO);
    Set<BookDTO> mapToBookDTOList(Collection<BookEntity> bookEntitySet);
}
