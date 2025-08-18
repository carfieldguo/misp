package com.groqdata.common.exception;

/**
 * 业务异常
 *
 * @author MISP TEAM
 */
public final class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误提示
     */
    private final String message;

    /**
     * 错误明细，内部调试错误
     */
    private final String detailMessage;

    /**
     * 构造函数
     *
     * @param message 错误提示
     */
    public ServiceException(String message) {
        this(message, null, null);
    }

    /**
     * 构造函数
     *
     * @param message 错误提示
     * @param code 错误码
     */
    public ServiceException(String message, Integer code) {
        this(message, code, null);
    }

    /**
     * 构造函数
     *
     * @param message 错误提示
     * @param code 错误码
     * @param detailMessage 详细错误信息
     */
    public ServiceException(String message, Integer code, String detailMessage) {
        super(message);
        this.message = message;
        this.code = code;
        this.detailMessage = detailMessage;
    }

    /**
     * 构造函数
     *
     * @param message 错误提示
     * @param cause 异常原因
     */
    public ServiceException(String message, Throwable cause) {
        this(message, null, null, cause);
    }

    /**
     * 构造函数
     *
     * @param message 错误提示
     * @param code 错误码
     * @param detailMessage 详细错误信息
     * @param cause 异常原因
     */
    public ServiceException(String message, Integer code, String detailMessage, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = code;
        this.detailMessage = detailMessage;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
