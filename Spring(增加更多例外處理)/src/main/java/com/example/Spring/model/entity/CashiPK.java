package com.example.Spring.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data   //  提供讀寫屬性, 此外還提供了equals()、hashCode()、toString() 方法
public class CashiPK implements Serializable {

    private String id;
    private String accNo;
    private String ccy;
}
