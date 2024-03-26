package com.study.library.dto;

import com.study.library.entity.Book;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterBookReqDto {
    private String isbn;
    @Min(value = 1, message = "숫자만 입력 가능합니다.") // int 라면 @Min(최솟값) 사용 가능, value = 1 -> 1부터 / Max, size 등
    private int bookTypeId;
    @Min(value = 1, message = "숫자만 입력 가능합니다.")
    private int categoryId;
    @NotBlank(message = "도서명을 입력 해주세요.") // @Notnull->null체크 / @Null->null체크(지만 null이어야함) / @Empty->공백만 체크
    private String bookName;
    @NotBlank(message = "저장명을 입력 해주세요.")
    private String authorName;
    @NotBlank(message = "출판사명을 입력 해주세요.")
    private String publisherName;
    @NotBlank(message = "커버 이미지를 등록 해주세요.")
    private String coverImgUrl;

    public Book toEntity() {
        return Book.builder()
                .bookName(bookName)
                .authorName(authorName)
                .publisherName(publisherName)
                .isbn(isbn)
                .bookTypeId(bookTypeId)
                .categoryId(categoryId)
                .coverImgUrl(coverImgUrl)
                .build();
    }
}