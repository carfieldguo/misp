package com.groqdata.common.exception.file;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 文件上传异常类，用于封装文件上传过程中发生的各类异常
 * <p>
 * 继承自{@link Exception}，支持异常链传递，可包含原始异常原因
 * 
 * @author MISP TEAM
 */
public class FileUploadException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * 原始异常原因
     */
    private final Throwable cause;

    /**
     * 构造一个无详细消息和原因的FileUploadException
     */
    public FileUploadException() {
        this(null, null);
    }

    /**
     * 构造一个包含详细消息但无原因的FileUploadException
     *
     * @param msg 详细异常消息
     */
    public FileUploadException(final String msg) {
        this(msg, null);
    }

    /**
     * 构造一个包含详细消息和原因的FileUploadException
     *
     * @param msg   详细异常消息
     * @param cause 原始异常原因（可以为null）
     */
    public FileUploadException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    /**
     * 打印异常堆栈跟踪信息到指定的打印流
     * 包括当前异常和原始异常（如果存在）的堆栈信息
     * 同步修饰符与父类保持一致
     *
     * @param stream 打印流对象
     */
    @Override
    public synchronized void printStackTrace(PrintStream stream) {
        super.printStackTrace(stream);
        if (cause != null) {
            stream.println("Caused by:");
            cause.printStackTrace(stream);
        }
    }

    /**
     * 打印异常堆栈跟踪信息到指定的打印写入器
     * 包括当前异常和原始异常（如果存在）的堆栈信息
     * 同步修饰符与父类保持一致
     *
     * @param writer 打印写入器对象
     */
    @Override
    public synchronized void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        if (cause != null) {
            writer.println("Caused by:");
            cause.printStackTrace(writer);
        }
    }

    /**
     * 获取原始异常原因
     *
     * @return 原始异常原因，如果不存在则返回null
     */
    @Override
    public Throwable getCause() {
        return cause;
    }
}
