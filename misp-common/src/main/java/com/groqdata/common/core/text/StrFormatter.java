package com.groqdata.common.core.text;

import org.apache.commons.lang3.StringUtils;

import com.groqdata.common.utils.basic.StringHelper;

/**
 * 字符串格式化
 *
 * @author MISP TEAM
 */
public class StrFormatter {
	private StrFormatter() {
		throw new IllegalStateException("工具类不可实例化");
	}

	public static final String EMPTY_JSON = "{}";
	public static final char C_BACKSLASH = '\\';
	public static final char C_DELIM_START = '{';
	public static final char C_DELIM_END = '}';

	/**
	 * 格式化字符串<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
	 * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
	 * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
	 *
	 * @param strPattern 字符串模板
	 * @param argArray   参数列表
	 * @return 结果
	 */
	public static String format(final String strPattern, final Object... argArray) {
		if (StringUtils.isEmpty(strPattern) || StringHelper.isEmpty(argArray)) {
			return strPattern;
		}
		final int strPatternLength = strPattern.length();
		StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

		int handledPosition = 0;
		int argIndex = 0;
		while (argIndex < argArray.length) {
			int delimIndex = strPattern.indexOf(EMPTY_JSON, handledPosition);
			if (delimIndex == -1) {
				if (handledPosition == 0) {
					return strPattern;
				} else {
					sbuf.append(strPattern, handledPosition, strPatternLength);
					return sbuf.toString();
				}
			}

			// 处理转义逻辑
			EscapeResult result = handleEscape(strPattern, sbuf, delimIndex, handledPosition, argArray[argIndex]);
			if (result.isEscaped()) {
				argIndex = result.getArgIndex();
			}
			handledPosition = result.getNextPosition();
			if (!result.isEscaped()) {
				argIndex++;
			}
		}

		sbuf.append(strPattern, handledPosition, strPattern.length());
		return sbuf.toString();
	}

	/**
	 * 处理转义字符逻辑
	 */
	private static EscapeResult handleEscape(String strPattern, StringBuilder sbuf, int delimIndex, int handledPosition,
			Object arg) {
		if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH) {
			if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH) {
				// 双反斜杠，占位符有效
				sbuf.append(strPattern, handledPosition, delimIndex - 1);
				sbuf.append(Convert.utf8Str(arg));
				return new EscapeResult(delimIndex + 2, false, -1);
			} else {
				// 单反斜杠，占位符无效
				sbuf.append(strPattern, handledPosition, delimIndex - 1);
				sbuf.append(C_DELIM_START);
				return new EscapeResult(delimIndex + 1, true, -1);
			}
		} else {
			// 正常占位符
			sbuf.append(strPattern, handledPosition, delimIndex);
			sbuf.append(Convert.utf8Str(arg));
			return new EscapeResult(delimIndex + 2, false, -1);
		}
	}

	/**
	 * 转义处理结果封装类
	 */
	private static class EscapeResult {
		private final int nextPosition;
		private final boolean escaped;
		private final int argIndex;

		public EscapeResult(int nextPosition, boolean escaped, int argIndex) {
			this.nextPosition = nextPosition;
			this.escaped = escaped;
			this.argIndex = argIndex;
		}

		public int getNextPosition() {
			return nextPosition;
		}

		public boolean isEscaped() {
			return escaped;
		}

		public int getArgIndex() {
			return argIndex;
		}
	}
}
