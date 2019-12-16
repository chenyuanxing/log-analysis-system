package com.cad.web;

public enum ErrorCode
{
    INVALID_REQ_PARAMS("000400", "请求参数不合法"),
    PERMISSION_DENY("000403","权限不足,操作拒绝"),
    SERVER_EXCEPTION("000500", "服务器异常"),

    INVALID_VALIDATE_CODE("010030","验证码错误"),
    LIMIT_ONE_MINITUE("010040","验证码获取太频繁了"),

    EXPIRED_ACCESSTOKEN("010210","登录失效，请重新登录");

    private String code;

    private String message;

    /** 产品错误码前缀 */
    private ErrorCode(String errCode, String errMsg)
    {
        this.code = errCode;
        this.message = errMsg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}