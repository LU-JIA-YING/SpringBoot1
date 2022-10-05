package com.example.Spring.model.entity;

import com.example.Spring.service.DateAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mgn_mgni")
@XmlAccessorType(XmlAccessType.FIELD)   //  排序
@EntityListeners(AuditingEntityListener.class)  //  相應的字段上添加對應的時間註解 @LastModifiedDate 和 @CreatedDate https://blog.csdn.net/qq_41378597/article/details/103798684
public class Mgni {

    @Id
    @Column(name = "MGNI_ID")
    private String id;

    @Column(name = "MGNI_TIME")
    @CreatedDate
    @XmlJavaTypeAdapter(DateAdapter.class)  //  XML時可以顯示時間(如果不用此方法 型態改String)
    private LocalDateTime time;

    @Column(name = "MGNI_TYPE")
    private String type;

    @Column(name = "MGNI_CM_NO")
    private String cmNo;

    @Column(name = "MGNI_KAC_TYPE")
    private String kacType;

    @Column(name = "MGNI_BANK_NO")
    private String bankNo;

    @Column(name = "MGNI_CCY")
    private String ccy;

    @Column(name = "MGNI_PV_TYPE")
    private String pvType;

    @Column(name = "MGNI_BICACC_NO")
    private String bicaccNo;

    @Column(name = "MGNI_I_TYPE")
    private String iType;

    @Column(name = "MGNI_P_REASON")
    private String pReason;

    @Column(name = "MGNI_AMT")
    private BigDecimal amt;

    @Column(name = "MGNI_CT_NAME")
    private String ctName;

    @Column(name = "MGNI_CT_TEL")
    private String ctTel;

    @Column(name = "MGNI_STATUS")
    private String status;

    @Column(name = "MGNI_U_TIME")
    @LastModifiedDate
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime uTime;

    //  cascade=CascadeType.ALL => 無論儲存、合併、 更新或移除，一併對被參考物件作出對應動作
    //  FetchType.EAGER => 在查詢時立刻載入關聯的物件
    //  https://matthung0807.blogspot.com/2018/06/jpa-onetomanyfetchtypelazyfetchtypeeager.html
    //  mappedBy = "id" VS @JoinColumn => https://www.796t.com/content/1544860112.html
    //  orphanRemoval=true => 當關係被斷開時，多方實體將被刪除 https://blog.csdn.net/liyiming2017/article/details/90613707
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "id", fetch = FetchType.EAGER)
//    @JoinColumn(name = "CASHI_MGNI_ID")
    private List<Cashi> cashiList;

}
