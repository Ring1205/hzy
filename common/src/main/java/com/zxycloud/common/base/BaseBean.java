package com.zxycloud.common.base;


/**
 * @author leiming
 * @date 2018/12/20.
 */
public class BaseBean {
    private String code;
    private String message;
    private int totalPages;
    private int totalRecords;

    public boolean isSuccessful() {
        return code.equals("000000");
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public String getCode() {
        return code;
    }
}
