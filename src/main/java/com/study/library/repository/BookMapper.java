package com.study.library.repository;

import com.study.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {
    public int saveBook(Book book);
    public List<Book> findBooks(
            @Param("startIndex") int startIndex, // 인덱스 번호로 페이지 구분하기 ex) limit x, y -> 인덱스 x 로부터 y 개수만큼
            @Param("count") int count,
            @Param("bookTypeId") int bookTypeId,
            @Param("categoryId") int categoryId,
            @Param("searchTypeId") int searchTypeId,
            @Param("searchText") String searchText
    );
}
