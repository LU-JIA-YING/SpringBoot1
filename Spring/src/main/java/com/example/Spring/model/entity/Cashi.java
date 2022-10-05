package com.example.Spring.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mgn_cashi")
@IdClass(CashiPK.class)
@XmlAccessorType(XmlAccessType.FIELD)//排序
public class Cashi {
    @Id
    @Column(name = "CASHI_MGNI_ID")
    private String id;

    @Id
    @Column(name = "CASHI_ACC_NO")
    private String accNo;

    @Id
    @Column(name = "CASHI_CCY")
    private String ccy;

    @Column(name = "CASHI_AMT")
    private BigDecimal amt;

}
