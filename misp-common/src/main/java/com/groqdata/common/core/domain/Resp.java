package com.groqdata.common.core.domain;

import java.io.Serial;
import java.io.Serializable;
import com.groqdata.common.constant.HttpStatus;

/**
 * 响应信息主体
 *
 * @author MISP TEAM
 */
public class Resp<T> implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	/** 成功 */
	public static final int SUCCESS = HttpStatus.SUCCESS;

	/** 失败 */
	public static final int FAIL = HttpStatus.ERROR;

	private int code;

	private String msg;

	private T data;

	public static <T> Resp<T> ok() {
		return restResult(null, SUCCESS, "操作成功");
	}

	public static <T> Resp<T> ok(T data) {
		return restResult(data, SUCCESS, "操作成功");
	}

	public static <T> Resp<T> ok(T data, String msg) {
		return restResult(data, SUCCESS, msg);
	}

	public static <T> Resp<T> error() {
		return restResult(null, FAIL, "操作失败");
	}

	public static <T> Resp<T> error(String msg) {
		return restResult(null, FAIL, msg);
	}

	public static <T> Resp<T> error(T data) {
		return restResult(data, FAIL, "操作失败");
	}

	public static <T> Resp<T> error(T data, String msg) {
		return restResult(data, FAIL, msg);
	}

	public static <T> Resp<T> error(int code, String msg) {
		return restResult(null, code, msg);
	}

	private static <T> Resp<T> restResult(T data, int code, String msg) {
		Resp<T> apiResult = new Resp<>();
		apiResult.setCode(code);
		apiResult.setData(data);
		apiResult.setMsg(msg);
		return apiResult;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static <T> Boolean isError(Resp<T> ret) {
		return !isSuccess(ret);
	}

	public static <T> Boolean isSuccess(Resp<T> ret) {
		return Resp.SUCCESS == ret.getCode();
	}
}
