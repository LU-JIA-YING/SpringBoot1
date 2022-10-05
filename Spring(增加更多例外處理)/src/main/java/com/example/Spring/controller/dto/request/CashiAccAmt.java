package com.example.Spring.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CashiAccAmt {

    @NotBlank
    @Length(max = 7, message = "格式錯誤")
    private String accNo;   //  存入結算帳戶帳號

    @NotNull    //  型態非String 不能用@NotBlank 跟 @NotEmpty
    @DecimalMin(value = "0", inclusive = false, message = "格式錯誤，輸入數字不能為負數")   //  金額必須大於 0
    @Digits(integer = 20, fraction = 4, message = "格式錯誤，整數位數不能大於20、小數位數不能大於4") // 整數位數20，小數位數4
    //  https://matthung0807.blogspot.com/2018/08/java-bean-validation-constraints-digits.html
    //  https://ithelp.ithome.com.tw/articles/10275699
    private BigDecimal amt; //  單筆存入金額
}
