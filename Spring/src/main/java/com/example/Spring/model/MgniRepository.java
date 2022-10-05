package com.example.Spring.model;

import com.example.Spring.model.entity.Mgni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

//  動態查詢 加上 JpaSpecificationExecutor<Mgni>
@Repository
public interface MgniRepository extends JpaRepository<Mgni, String>, JpaSpecificationExecutor<Mgni> {

//  因用原生JPA 他會預防空值問題 所以她回傳是Optional(如果不想做轉型態 就直接下@Query)(之前不會有此問題是id是int)
//    Optional<Mgni> findById(String id);

    @Query(value = "select * from MGN_MGNI where MGNI_ID =?1", nativeQuery = true)
    Mgni findMgniById(String id);

}
