package com.zhouj.rpc.protocol;


/**
 * @author zhouj
 * @since 2020-08-03
 */
public class Response {

    private String requestId;

    /**
     * 响应编码 200 成功 300超时
     */
    private int code;

    private String error;

    private Object result;

    private Long timestamp;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
