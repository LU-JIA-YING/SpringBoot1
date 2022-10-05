package com.example.Spring.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClearingMarginRequest {

    @NotBlank
    @Length(max = 7, message = "格式錯誤，請輸入7個數字")
    //    @Pattern(regexp = "^$|[A-Za-z0-9]{0,7}",message = "代碼長度上限為 7")
    private String cmNo;   //  結算會員代號

    @NotBlank
    @Pattern(regexp = "^$|(1|2)", message = "格式錯誤，請輸入1(虛擬帳戶) 或 2(實體帳戶)")
    //  OR ^$|[12]
    //  ^String$ --> 精確匹配 string 字串
    //  https://tw511.com/a/01/32915.html
    private String kacType;    //  存入保管專戶別

    @NotBlank
    @Pattern(regexp = "^$|[0-9]{3}",message = "格式錯誤，代碼長度只能為 3")
    //    @Length(max = 3, message = "格式錯誤")
    private String bankNo; // 存入結算銀行代碼

    @NotBlank
    @Pattern(regexp = "^$|(TWD|USD)", message = "格式錯誤，請輸入 TWD 或 USD")
    private String ccy; //  存入幣別

    @NotBlank
    @Pattern(regexp = "^$|(1|2)", message = "格式錯誤，請輸入 1 或 2")
    private String pvType; //  存入方式

    @NotBlank
    @Length(max = 21, message = "格式錯誤，長度不能大於21")
    @Pattern(regexp = "^[0-9]*$", message = "格式錯誤，請輸入數字")
    //    @Pattern(regexp = "^$|[0-9]{0,21}",message = "代碼長度上限為 21")
    //  https://jimmy0222.pixnet.net/blog/post/36958819
    private String bicaccNo;   //  實體帳號/虛擬帳號

    @NotBlank
    @Pattern(regexp = "^[1-4]*$", message = "格式錯誤，請輸入1~4")
    private String iType;   //  存入類別(開業|續繳|其他|額外分擔金額)(交割結算基金時使用)

    @Valid
    // 不加@Valid 裡面無法擋格式 (收尋:avax.validatio object list => https://stackoverflow.com/questions/28150405/validation-of-a-list-of-objects-in-spring)
    private List<CashiAccAmt> clearingAccountList;  //  存入結算帳戶帳號與存入金額(交割結算基金帳戶只會有一筆總金額)

    @NotBlank
    @Length(max = 120, message = "格式錯誤，長度不能大於120")
    //    @Pattern(regexp = "^$|[A-Za-z\\-u4e00\\u9fa5]{0,120}",message = "代碼長度上限為 120")
    private String ctName; //  聯絡人姓名

    @NotBlank
    @Pattern(regexp = "^[0-9]*$", message = "格式錯誤，請輸入數字") //  ^[0-9]*$ --> 只能輸入數字
    private String ctTel;  //  聯絡人電話

    @NotBlank
    @Length(max = 50, message = "格式錯誤，長度不能大於50")
    private String pReason;    //  存入實體帳號原因

}
