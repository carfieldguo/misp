package com.groqdata.common.exception.file;


/**
 * Excel工作表不存在异常
 */
public class ExcelSheetNotExistException extends Exception {

    public ExcelSheetNotExistException(String message) {
        super(message);
    }

    public ExcelSheetNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}