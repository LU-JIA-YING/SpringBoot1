package com.example.Spring.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchMgniRequest {

    @Pattern(regexp = "^$|(MGI[0-9]{17})", message = "ID格式請輸入：GMI + 17位數字")
    private String id;

    @Pattern(regexp = "^$|[12]", message = "請輸入 1 或 2")
    private String kacType;

    @Pattern(regexp = "^$|(TWD|USD)", message = "請輸入 TWD 或 USD")
    private String ccy;

    // \d ==> [0-9]
    // [12]\d ==> 10~29
    @Pattern(regexp = "\\b(\\d{4})(0[1-9]|1[0-2])(0[1-9]|[12]\\d|30|31)\\b", message = "日期格式請輸入：yyyyMMdd")
    private String date;
}
