package com.group.dto;

import java.io.Serializable;

public class AjaxResult implements Serializable{

    private boolean success;

    private String msg;

    private Object data;

    // 200成功,300参数不完整,500程序错误
    private Integer status;
    
    //静态方法  wxf  2018 3-27
    

   	public static AjaxResult success(Object data){
   		return new AjaxResult(true,"成功",data, 200);
   	}

   	public static AjaxResult success(Object data, String msg){
   		return new AjaxResult(true, msg, data,200);
   	}

   	public static AjaxResult success(String msg){
   		return new AjaxResult(true, msg, null,200);
   	}

       public static AjaxResult fail_500(String msg){
           return new AjaxResult(false, msg, null,500);
       }
       public static AjaxResult fail_300(String msg){
           return new AjaxResult(false, msg, null,300);
       }

    public AjaxResult() {
    }

    public AjaxResult(boolean success, String msg, Object data, Integer status) {
        this.success = success;
        this.msg = msg;
        this.data = data;
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}
