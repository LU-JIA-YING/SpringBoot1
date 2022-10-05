package com.example.Spring.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCashiRequest {

    @Pattern(regexp = "^$|(MGI[0-9]{17})",message = "ID格式請輸入：GMI + 17位數字")
    private String id;

    @Length(max = 7, message = "格式錯誤")
    private String accNo;

    @Pattern(regexp = "^$|(TWD|USD)",message = "請輸入 TWD 或 USD")
    private String ccy;
}
