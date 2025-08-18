package com.groqdata.common.exception;

import java.io.Serial;

/**
 * 全局异常
 *
 * @author MISP TEAM
 */
public class GlobalException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误提示
     */
    private final String message;

    /**
     * 错误明细，内部调试错误
     *
     */
    private final String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public GlobalException() {
        this.message = null;
        this.detailMessage = null;
    }

    public GlobalException(String message) {
        this.message = message;
        this.detailMessage = null;
    }

    public GlobalException(String message, String detailMessage) {
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
