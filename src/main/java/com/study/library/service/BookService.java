package com.study.library.service;

import com.study.library.dto.RegisterBookReqDto;
import com.study.library.dto.SearchBookReqDto;
import com.study.library.dto.SearchBookRespDto;
import com.study.library.entity.Book;
import com.study.library.repository.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookMapper bookMapper;

    @Transactional(rollbackFor = Exception.class) // 입력시 취소 되면 롤백
    public void saveBook(RegisterBookReqDto registerBookReqDto) {
        bookMapper.saveBook(registerBookReqDto.toEntity());// DB에 넣을 데이터, dto -> entity
    }

    public List<SearchBookRespDto> searchBooks(SearchBookReqDto searchBookReqDto) {
        int startIndex = (searchBookReqDto.getPage() - 1) * searchBookReqDto.getCount();
        List<Book> books = bookMapper.findBooks(
                startIndex,
                searchBookReqDto.getCount(),
                searchBookReqDto.getBookTypeId(),
                searchBookReqDto.getCategoryId(),
                searchBookReqDto.getSearchTypeId(),
                searchBookReqDto.getSearchText()
        );

        return books.stream().map(Book::toSearchBookRespDto).collect(Collectors.toList());
    }
}
