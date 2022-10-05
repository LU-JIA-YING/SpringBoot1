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
    @Transactional(rollbackFor = Exception.class)  //  Rollback(多加rollbackFor = Exception.class 可以擋全部的Exception)
    public StatusResponse createClearingMargin(ClearingMarginRequest request) {

        //新增一個資料
        Mgni mgni = new Mgni();
        mgni.setId("MGI" + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()));
//        mgni.setTime(LocalDateTime.now());    //  加@CreatedDate自動生成 所以可以不用
        mgni = addMgni(mgni, request);

        //下面做如果clearingAccountList的帳號一樣，金額要加總
        List<CashiAccAmt> clearingAccountList = request.getClearingAccountList();   //  取得的所有list
        List<String> distinctAccNo = clearingAccountList.stream().map(e -> e.getAccNo()).distinct().collect(Collectors.toList()); //  用map去重複

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
    public StatusResponse updateClearingMargin(String id, ClearingMarginRequest request) {

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

        for (String accNo : distinctAccNo) {
            BigDecimal sumAmt = new BigDecimal(0);  //  存入結算帳戶號碼相同時金額加總
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
    public List<Mgni> searchTargetMgni(SearchMgniRequest request, String page) {

        Specification<Mgni> specification = new Specification<Mgni>() {

            @Override
            public Predicate toPredicate(Root<Mgni> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Path<Object> id = root.get("id");
                Path<Object> kacType = root.get("kacType");
                Path<Object> ccy = root.get("ccy");

                List<Predicate> filteredList = new ArrayList<>();

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
                    LocalDateTime dateTime = LocalDate.parse(request.getDate(),DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay();
                    filteredList.add(cb.between(root.get("time"),dateTime,LocalDateTime.now()));
                }
                Predicate resultList = cb.and(filteredList.toArray((new Predicate[filteredList.size()])));

                return resultList;
            }
        };

        Pageable pageable = PageRequest.of(Integer.parseInt(page) - 1, 2);
        Page<Mgni> mgniList = mgniRepository.findAll(specification, pageable);
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

//==============================================================================================

/*
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class UpdateCashiRequest {
        @NotEmpty
        @Pattern(regexp = "^$|(MGI[0-9]{17})",message = "ID格式請輸入：GMI + 17位數字")
        private String mgniId;
        @NotEmpty
        private String accNo;
        @NotEmpty
        @Pattern(regexp = "^$|(TWD|USD)",message = "請輸入 TWD or USD")
        private String ccy;
        @NotNull
        @DecimalMin(value = "0",inclusive = false,message = "輸入數值需大於 0")
        @Digits(integer = 20,fraction = 4,message = "長度錯誤")
        private BigDecimal amt;
    }
*/

//    public Mgni updateCashi(UpdateCashiRequest request) {
//        Cashi cashi = cashiRepository.findTargetCashi(request.getMgniId(), request.getAccNo(), request.getCcy());
//        cashi.setAmt(request.getAmt());
//        cashiRepository.save(cashi);
//
//        Mgni mgni = mgniRepository.findMgniById(request.getMgniId());
//        mgni.setAmt(countAmt(mgni.getCashiList()));
//        mgniRepository.save(mgni);
//
//        return mgni;
//    }

/*
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class CashiPKRequest {
        @NotEmpty
        @Pattern(regexp = "^$|(MGI[0-9]{17})",message = "ID格式請輸入：GMI + 17位數字")
        private String mgniId;
        @NotEmpty
        private String accNo;
        @NotEmpty
        @Pattern(regexp = "^$|(TWD|USD)",message = "請輸入 TWD or USD")
        private String ccy;
    }
 */

//    public Mgni deleteCashi(CashiPKRequest request) {
//        Cashi cashi = cashiRepository.findTargetCashi(request.getMgniId(), request.getAccNo(), request.getCcy());
//        cashiRepository.delete(cashi);
//        Mgni mgni = mgniRepository.findMgniById(request.getMgniId());
//        mgni.setAmt(countAmt(mgni.getCashiList()));
//        mgniRepository.save(mgni);
//        return mgniRepository.findMgniById(request.getMgniId());
//    }


//    private BigDecimal countAmt(List<Cashi> cashiList) {
//        BigDecimal amt = new BigDecimal(0);
//        for (Cashi cashi : cashiList) {
//            amt = amt.add(cashi.getAmt());
//        }
//        return amt;
//    }

}
