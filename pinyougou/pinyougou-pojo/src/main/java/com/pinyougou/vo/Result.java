package com.pinyougou.vo;



import java.io.Serializable;

/**
 * Date:2018/11/24
 * Author:Leon
 * Desc
 * 提示信息类，返回成功或者失败信息
 */
public class Result implements Serializable {
    //状态信息
    private Boolean success;
    //返回的提示信息
    private String message;

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * 返回成功的方法
     * */
    public static Result ok(String message) {
        return new Result(true, message);
    }

    /**
     * 返回失败的方法
     */
    public static Result fail(String message) {
        return new Result(false, message);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
