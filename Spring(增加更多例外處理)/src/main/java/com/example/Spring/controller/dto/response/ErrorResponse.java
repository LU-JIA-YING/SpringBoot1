package com.example.Spring.controller.dto.response;

import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//  https://hackmd.io/@IDdlPCCwQoeX-9DvmEbLyw/HJIc1cRWo
public class ErrorResponse {

    private List<Map<String, String>> fieldError;

    private String error;

    // 處理 RequestBody 未通過基礎檢核所拋的 MethodArgumentNotValidException
    public ErrorResponse(MethodArgumentNotValidException e) {

        this.fieldError = new ArrayList<>();

        // 因為未通過基礎檢核的欄位可能不只一個
        // 所以需要呼叫 e.getBindingResult().getFieldErrors() 取得不符合基礎檢核的欄位
        // 再放入 fieldError 中

        e.getBindingResult().getFieldErrors().stream().forEach(m -> {
            Map<String, String> fieldMap = new HashMap<>();

            // 欄位名稱
            fieldMap.put("fiels", m.getField());

            // 錯誤類型，例 : NotNull 或是 NotBlank
            fieldMap.put("code", m.getCode());

            // 錯誤訊息，例 : 年齡不可為空
            fieldMap.put("message", m.getDefaultMessage());

            fieldError.add(fieldMap);
        });
    }

    // 處理 Query String 未通過基礎檢核所拋的 ConstraintViolationException
    public ErrorResponse(ConstraintViolationException e) {

        this.fieldError = new ArrayList<>();

        // 因為未通過基礎檢核的欄位可能不只一個
        // 所以需要呼叫 e.getConstraintViolations() 取得不符合基礎檢核的欄位
        // 再放入 fieldError 中

        e.getConstraintViolations().stream().forEach(c -> {

            String fieldName = null;

            for (Path.Node node : c.getPropertyPath()) {
                fieldName = node.getName();
            }

            Map<String, String> map = new HashMap<>();
            // 錯誤類型，例 : NotNull 或是 NotBlank
            map.put("code", c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());

            // 錯誤訊息，例 : 年齡不可為空
            map.put("message", c.getMessage());

            // 欄位名稱
            map.put("field", fieldName);

            fieldError.add(map);
        });
    }

    // 處理 Exception
    public ErrorResponse(Exception e) {
        this.error = e.getMessage();
    }

    public List<Map<String, String>> getFieldError() {
        return fieldError;
    }

    public void setFieldError(List<Map<String, String>> fieldError) {
        this.fieldError = fieldError;
    }

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

}

