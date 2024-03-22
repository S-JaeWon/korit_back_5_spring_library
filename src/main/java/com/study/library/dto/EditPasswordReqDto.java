package com.study.library.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class EditPasswordReqDto {
    @NotBlank
    private String oldPassword;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,128}$", message = "영문자와 숫자, 특수문자 조합으로 8~128자리 이어야 합니다.")
    private String newPassword;
    @NotBlank
    private String newPasswordCheck;
}
