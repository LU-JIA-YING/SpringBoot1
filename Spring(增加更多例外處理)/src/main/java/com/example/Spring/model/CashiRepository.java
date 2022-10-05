package com.example.Spring.model;

import com.example.Spring.model.entity.Cashi;
import com.example.Spring.model.entity.CashiPK;
import com.example.Spring.model.entity.Mgni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

//  動態查詢 加上 JpaSpecificationExecutor<Cashi>
@Repository
public interface CashiRepository extends JpaRepository<Cashi, CashiPK>, JpaSpecificationExecutor<Cashi> {


//    @Query(value = "select * from CASHI where CASHI_MGNI_ID =?1 and CASHI_ACC_NO =?2 and CASHI_CCY =?3", nativeQuery = true)
//    Cashi findTargetCashi(String mgniId, String accNo, String ccy);

//    @Query(value = "select * from CASHI where CASHI_MGNI_ID =?1", nativeQuery = true)
//    List<Cashi> findCashiListById(String mgniId);

    @Modifying
    @Query(value = "DELETE FROM mgn_cashi WHERE CASHI_MGNI_ID=?1", nativeQuery = true)
    void deleteCashiById(String id);


}
