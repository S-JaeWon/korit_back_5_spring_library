package com.study.library.service;

import com.study.library.dto.*;
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

    public SearchBookCountRespDto getBookCount(SearchBookReqDto searchBookReqDto) {
        int bookCount = bookMapper.getBookCount(
                searchBookReqDto.getBookTypeId(),
                searchBookReqDto.getCategoryId(),
                searchBookReqDto.getSearchTypeId(),
                searchBookReqDto.getSearchText()
        );
        int maxPageNumber = (int)Math.ceil/*올림*/(((double) bookCount) / searchBookReqDto.getCount()); //()안에 자료형 넣으면 형변환 -> 명시적 형변환
        // 20개의 게시물을 보여주는 게시판에서 61개 조회시, 60 / 20 = 3.~~~ 이므로 올림해서 페이지 4개 생성
        return SearchBookCountRespDto.builder()
                .totalCount(bookCount)
                .maxPageNumber(maxPageNumber)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBooks(List<Integer> bookIds) {
        bookMapper.deleteBooksByBookIds(bookIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBooks(UpdateBookReqDto updateBookReqDto) {
        bookMapper.updateBookByBookId(updateBookReqDto.toEntity());
    }
}
