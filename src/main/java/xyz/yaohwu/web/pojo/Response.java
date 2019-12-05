package xyz.yaohwu.web.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaohwu
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Response implements Serializable {

    /**
     * 响应状态
     */
    private int status;
    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;
    /**
     * 响应实体内容
     */
    private Object data;

    public Response() {
    }

    public Response status(int statusCode) {
        this.status = statusCode;
        return this;
    }

    public Response errorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public Response errorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public Response data(Object obj) {
        this.data = obj;
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private static Response create() {
        return new Response();
    }

    /**
     * 成功的响应,关注结果
     *
     * @param obj 响应结果
     * @return 响应
     */
    public static Response ok(Object obj) {
        return Response.create().data(obj);
    }

    /**
     * 成功的响应,不关注结果
     *
     * @return 响应
     */
    public static Response success() {
        return Response.ok("success");
    }

    /**
     * 成功的响应,关注操作成功条目数
     *
     * @param successNum 操作成功的条目数
     * @return 响应
     */
    public static Response success(int successNum) {
        Map<String, Integer> data = new HashMap<>();
        data.put("count", successNum);
        return Response.ok(data);
    }

    /**
     * 失败的响应
     *
     * @param status    状态码
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @return 响应
     */
    public static Response error(int status, String errorCode, String errorMsg) {
        return Response.create().status(status).errorCode(errorCode).errorMsg(errorMsg);
    }

    /**
     * 失败的响应
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @return 响应
     */
    public static Response error(String errorCode, String errorMsg) {
        return Response.create().errorCode(errorCode).errorMsg(errorMsg);
    }
}
