package com.example.Spring.service;

import com.example.Spring.controller.dto.request.CashiAccAmt;
import com.example.Spring.controller.dto.request.ClearingMarginRequest;
import com.example.Spring.controller.dto.request.SearchCashiRequest;
import com.example.Spring.controller.dto.request.SearchMgniRequest;
import com.example.Spring.controller.dto.response.MgniResponse;
import com.example.Spring.controller.dto.response.StatusResponse;
import com.example.Spring.model.CashiRepository;
import com.example.Spring.model.MgniRepository;
import com.example.Spring.model.entity.Cashi;
import com.example.Spring.model.entity.Mgni;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)  //  Rollback(多加rollbackFor = Exception.class 可以擋全部的Exception)
public class TransferService {

    @Autowired
    private CashiRepository cashiRepository;
    @Autowired
    private MgniRepository mgniRepository;

    //查詢 API 接收 XML 或 Json 格式(Find All Mgni)
    public MgniResponse getAllMgnMgni() {

        MgniResponse response = new MgniResponse();
        response.setMgniList(mgniRepository.findAll());
//        response.setMgniList(mgniRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))); //  排序(由大到小)

        return response;
    }

//==============================================================================================

    //新增結算保證金and交割結算基金帳戶
    public StatusResponse createClearingMargin(ClearingMarginRequest request) throws Exception {

        //新增一個資料
        Mgni mgni = new Mgni();
        mgni.setId("MGI" + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()));
//        mgni.setTime(LocalDateTime.now());    //  加@CreatedDate自動生成 所以可以不用

        if (mgniRepository.findMgniById(mgni.getId()) != null) {
            throw new Exception("此ID已存在");
        }

        mgni = addMgni(mgni, request);

        //下面做如果clearingAccountList的帳號一樣，金額要加總
        List<CashiAccAmt> clearingAccountList = request.getClearingAccountList();   //  取得的所有list
        List<String> distinctAccNo = clearingAccountList.stream().map(e -> e.getAccNo()).distinct().collect(Collectors.toList()); //  用map去重複

        if (request.getKacType().equals("2") && distinctAccNo.size() > 1) {
            throw new Exception("存入保管專戶別為交割結算基金專戶時，只能存入一筆總金額");
        }

        for (String accNo : distinctAccNo) {
            BigDecimal sumAmt = new BigDecimal(0);  //  存入結算帳戶號碼相同時金額加總
            for (CashiAccAmt clearingAccount : clearingAccountList) {
                if (accNo.equals(clearingAccount.getAccNo())) {
                    sumAmt = sumAmt.add(clearingAccount.getAmt());

                }
            }

            Cashi cashi = new Cashi();
            cashi = addCashi(cashi, sumAmt, accNo, mgni.getId(), mgni.getCcy());
        }

        //回傳
        return new StatusResponse("儲存成功");
    }

//==============================================================================================

    // 更新結算保證金and交割結算基金帳戶
    public StatusResponse updateClearingMargin(String id, ClearingMarginRequest request) throws Exception {


        cashiRepository.deleteCashiById(id);    //  先做刪小表，才不會有大表串聯小表無法篩除問題(刪除小表是 為了解決原本有較多資料，但更改後變少資料)

        Mgni mgni = mgniRepository.findMgniById(id);

//        // 因用原生JPA 他會預防空值問題 所以她回傳是Optional(如果不想做轉型態 就直接下@Query)
//        Optional<Mgni> optionalMgni = mgniRepository.findById(id);
//        Mgni mgni;
//        if(optionalMgni.isPresent()){ //nullPointerException
//            mgni = optionalMgni.get();
//        }

        Mgni updateMgni = addMgni(mgni, request);

        //下面做如果clearingAccountList的帳號一樣，金額要加總
        List<CashiAccAmt> clearingAccountList = request.getClearingAccountList();   //  取得的所有list
        List<String> distinctAccNo = clearingAccountList.stream().map(e -> e.getAccNo()).distinct().collect(Collectors.toList()); //  用map去重複

        if (request.getKacType().equals("2") && distinctAccNo.size() > 1) {
            throw new Exception("存入保管專戶別為交割結算基金專戶時，只能存入一筆總金額");
        }

        for (String accNo : distinctAccNo) {
            BigDecimal sumAmt = new BigDecimal(0);  //  存入結算帳戶號碼相同時金額加總(cashi的Amt)
            for (CashiAccAmt clearingAccount : clearingAccountList) {
                if (accNo.equals(clearingAccount.getAccNo())) {
                    sumAmt = sumAmt.add(clearingAccount.getAmt());

                }
            }

            Cashi cashi = new Cashi();
            cashi = addCashi(cashi, sumAmt, accNo, updateMgni.getId(), updateMgni.getCcy());
        }


        return new StatusResponse("更新成功");
    }

//==============================================================================================

    // 刪除結算保證金and交割結算基金帳戶
    public String deleteClearingMargin(String id) {

        Mgni mgni = mgniRepository.findMgniById(id);

        if (null == mgni) {
            return "沒有此ID";
        }

        mgniRepository.deleteById(id);

        return "刪除成功";
    }

//==============================================================================================

    //  Mgni複雜動態查詢及分頁，排序
    //  https://www.gushiciku.cn/pl/pTm9/zh-tw
    public List<Mgni> searchTargetMgni(SearchMgniRequest request, String page) throws Exception {

        Specification<Mgni> specification = new Specification<Mgni>() { //  創建一匿名類Specification接口

            // root from Mgni ,取得要篩選的列 // CriteriaBuilder 用來設置條件
            @Override
            public Predicate toPredicate(Root<Mgni> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path<Object> id = root.get("id");
                Path<Object> kacType = root.get("kacType");
                Path<Object> ccy = root.get("ccy");

                List<Predicate> filteredList = new ArrayList<>();   //  條件放進List

                if (request.getId() != null) {
                    filteredList.add(cb.equal(id, request.getId()));
                }
                if (request.getKacType() != null) {
                    filteredList.add(cb.equal(kacType, request.getKacType()));
                }
                if (request.getCcy() != null) {
                    filteredList.add(cb.equal(ccy, request.getCcy()));
                }
                if (request.getDate() != null) {
                    LocalDateTime dateTime = LocalDate.parse(request.getDate(), DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay();
                    filteredList.add(cb.between(root.get("time"), dateTime, LocalDateTime.now()));
                }
                Predicate resultList = cb.and(filteredList.toArray((new Predicate[filteredList.size()])));

                return resultList;
            }
        };

        Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, 2);
        Page<Mgni> mgniList = mgniRepository.findAll(specification, pageable);
        checkMgniExist();

        return mgniList.getContent();
    }

//==============================================================================================

    //  Cashi複雜動態查詢及分頁，排序
    public List<Cashi> searchTargetCashi(SearchCashiRequest request) {

        Specification<Cashi> specification = new Specification<Cashi>() {

            @Override
            public Predicate toPredicate(Root<Cashi> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

                List<Predicate> predicates = new ArrayList<>();

                if (request.getId() != null) {
                    predicates.add(builder.equal(root.get("id"), request.getId()));
                }
                if (request.getAccNo() != null) {
                    predicates.add(builder.equal(root.get("accNo"), request.getAccNo()));
                }
                if (request.getCcy() != null) {
                    predicates.add(builder.equal(root.get("ccy"), request.getCcy()));
                }
                query.orderBy(builder.asc(root.get("accNo")));
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        Pageable pageable = PageRequest.of(0, 3);   // 顯示第0頁每頁顯示3條
        Page<Cashi> cashis = cashiRepository.findAll(specification, pageable);
        return cashis.getContent();
    }

//==============================================================================================

    public Mgni addMgni(Mgni mgni, ClearingMarginRequest request) {

        //新增資料:資料是從 request來的
        mgni.setType("1");
        mgni.setCmNo(request.getCmNo());
        mgni.setKacType(request.getKacType());
        mgni.setBankNo(request.getBankNo());
        mgni.setCcy(request.getCcy());
        mgni.setPvType(request.getPvType());
        mgni.setBicaccNo(request.getBicaccNo());

        //  reduce() 運算求和
        //  用於加法的標識值，即BigDecimal.ZERO
        //  通過方法引用BigDecimal::add將兩個BigDecimal相加
        //  https://www.codenong.com/22635945/
        BigDecimal totalAmt = request.getClearingAccountList().stream().map(e -> e.getAmt()).reduce(BigDecimal.ZERO, BigDecimal::add);
        mgni.setAmt(totalAmt);

        mgni.setIType(request.getIType());
        mgni.setPReason(request.getPReason());
        mgni.setCtName(request.getCtName());
        mgni.setCtTel(request.getCtTel());
        mgni.setStatus("0");
//        mgni.setUTime(LocalDateTime.now());   //  加@LastModifiedDate自動生成 所以可以不用

        //儲存進DB
        mgniRepository.save(mgni);

        return mgni;
    }

    public Cashi addCashi(Cashi cashi, BigDecimal amt, String accNo, String id, String ccy) {

        cashi.setId(id);
        cashi.setAccNo(accNo);
        cashi.setCcy(ccy);
        cashi.setAmt(amt);

        cashiRepository.save(cashi);

        return cashi;
    }

    private Boolean checkMgniExist() throws Exception {
        if (mgniRepository.findAll().isEmpty()) {
            throw new Exception("資料庫無符合資料");
        }
        return true;
    }

//==============================================================================================

//    //舊的新增
//    //新增結算保證金and交割結算基金帳戶
//    @Transactional  //  Rollback
//    public StatusResponse createClearingMargin(ClearingMarginRequest request) {
//
//        //新增一個資料
//        Mgni mgnMgni = new Mgni();
//
//        //新增資料:資料是從 request來的
//        mgnMgni.setId("MGI" + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()));
//        mgnMgni.setTime(LocalDateTime.now());
//        mgnMgni.setType("1");
//        mgnMgni.setCmNo(request.getCmNo());
//        mgnMgni.setKacType(request.getKacType());
//        mgnMgni.setBankNo(request.getBankNo());
//        mgnMgni.setCcy(request.getCcy());
//        mgnMgni.setPvType(request.getPvType());
//        mgnMgni.setBicaccNo(request.getBicaccNo());
//
//        //55-68行 做如果clearingAccountList的帳號一樣，金額要加總
//        List<CashiAccAmt> clearingAccountList = request.getClearingAccountList();   //  取得的所有list
//        List<String> distinctAccNo = clearingAccountList.stream().map(e -> e.getAccNo()).distinct().collect(Collectors.toList()); //  用map去重複
//
//        BigDecimal totalAmt = new BigDecimal(0);    //  所有結算帳戶金額加總
//        for (String accNo : distinctAccNo) {
//            BigDecimal sumAmt = new BigDecimal(0);  //  存入結算帳戶號碼相同時金額加總
//            for (CashiAccAmt clearingAccount : clearingAccountList) {
//                if (accNo.equals(clearingAccount.getAccNo())) {
//                    sumAmt = sumAmt.add(clearingAccount.getAmt());
//
//                }
//            }
//
//            Cashi mgnCashi = new Cashi();
//            mgnCashi.setId(mgnMgni.getId());
//            mgnCashi.setAccNo(accNo);
//            mgnCashi.setCcy(request.getCcy());
//            mgnCashi.setAmt(sumAmt);
//
//            totalAmt = totalAmt.add(sumAmt);
//
//            cashiRepository.save(mgnCashi);
//        }
//
//        mgnMgni.setAmt(totalAmt);
//        mgnMgni.setIType(request.getIType());
//        mgnMgni.setPReason(request.getPReason());
//        mgnMgni.setCtName(request.getCtName());
//        mgnMgni.setCtTel(request.getCtTel());
//        mgnMgni.setStatus("0");
//        mgnMgni.setUTime(LocalDateTime.now());
//
//
//        //儲存進DB
//        mgniRepository.save(mgnMgni);
//
//        //回傳
//        return new StatusResponse("儲存成功");
//    }
}
