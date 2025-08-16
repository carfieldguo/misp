package com.groqdata.common.exception.mybatis;


import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serial;

/**
 * MyBatis初始化异常类，专门用于表示MyBatis框架在启动初始化阶段发生的异常
 * <p>
 * 包含初始化阶段的关键上下文信息（如配置文件路径、数据源信息、初始化步骤），
 * 便于定位配置错误、资源缺失、依赖冲突等启动级问题
 *
 * @author MyBatis Framework Team
 */
public class MyBatisInitException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 发生错误的配置文件路径（如mybatis-config.xml的路径，可为null）
     */
    private final String configLocation;

    /**
     * 初始化阶段（如"CONFIG_LOAD"、"DATASOURCE_INIT"、"MAPPER_REGISTER"等）
     */
    private final String initPhase;

    /**
     * 关联的数据源标识（如数据源名称，可为null）
     */
    private final String dataSourceId;

    /**
     * 原始异常原因
     */
    private final Throwable cause;

    /**
     * 构造包含完整初始化上下文的异常
     *
     * @param message       异常描述信息
     * @param configLocation 配置文件路径（可为null）
     * @param initPhase     初始化阶段标识
     * @param dataSourceId  数据源标识（可为null）
     * @param cause         原始异常原因（可为null）
     */
    public MyBatisInitException(String message, String configLocation, String initPhase,
                                String dataSourceId, Throwable cause) {
        super(message);
        this.configLocation = configLocation;
        this.initPhase = initPhase;
        this.dataSourceId = dataSourceId;
        this.cause = cause;
    }

    /**
     * 构造只包含异常描述信息的简化异常（用于通用场景）
     *
     * @param message 异常描述信息
     */
    public MyBatisInitException(String message) {
        this(message, null, "SCAN", null, null);
    }

    /**
     * 构造只包含异常描述信息的简化异常（用于通用场景）
     *
     * @param message 异常描述信息
     */
    public MyBatisInitException(String message, Throwable cause) {
        this(message, null, "SCAN", null, cause);
    }

    /**
     * 构造包含核心初始化信息的异常（无数据源标识）
     *
     * @param message       异常描述信息
     * @param configLocation 配置文件路径（可为null）
     * @param initPhase     初始化阶段标识
     * @param cause         原始异常原因（可为null）
     */
    public MyBatisInitException(String message, String configLocation, String initPhase, Throwable cause) {
        this(message, configLocation, initPhase, null, cause);
    }

    /**
     * 构造简化的初始化异常（无配置文件路径和数据源标识）
     *
     * @param message   异常描述信息
     * @param initPhase 初始化阶段标识
     * @param cause     原始异常原因（可为null）
     */
    public MyBatisInitException(String message, String initPhase, Throwable cause) {
        this(message, null, initPhase, null, cause);
    }

    /**
     * 获取配置文件路径
     *
     * @return 配置文件路径，可为null
     */
    public String getConfigLocation() {
        return configLocation;
    }

    /**
     * 获取初始化阶段标识
     *
     * @return 初始化阶段（如"MAPPER_REGISTER"）
     */
    public String getInitPhase() {
        return initPhase;
    }

    /**
     * 获取数据源标识
     *
     * @return 数据源标识，可为null
     */
    public String getDataSourceId() {
        return dataSourceId;
    }

    /**
     * 获取原始异常原因
     *
     * @return 原始异常原因，可为null
     */
    @Override
    public synchronized Throwable getCause() {
        return cause;
    }

    /**
     * 打印异常堆栈信息，包含初始化上下文
     */
    @Override
    public void printStackTrace(PrintStream s) {
        printStackTrace(new PrintWriter(s));
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        s.println("MyBatisInitException: 初始化阶段=" + initPhase
                + ", 配置文件=" + (configLocation != null ? configLocation : "N/A")
                + ", 数据源=" + (dataSourceId != null ? dataSourceId : "N/A"));
        super.printStackTrace(s);
        if (cause != null) {
            s.println("Caused by:");
            cause.printStackTrace(s);
        }
    }


    /**
     * 重写异常消息，包含初始化阶段信息
     */
    @Override
    public String getMessage() {
        return super.getMessage() + "（初始化阶段：" + initPhase + "）";
    }
}