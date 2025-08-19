package com.groqdata.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数字金额转中文大写工具类
 * 处理范围：支持正负数字，精确到分
 */
public class ChineseNumberConverter {
    private ChineseNumberConverter() {
        throw new IllegalStateException("工具类不可实例化");
    }

	// 小数部分单位（角、分）
	private static final String[] FRACTION_UNITS = {"角", "分" };
	// 数字对应中文
	private static final String[] DIGITS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	// 整数部分单位（元、万、亿）
	private static final String[] INTEGER_GROUP_UNITS = {"元", "万", "亿" };
	// 组内单位（拾、佰、仟）
	private static final String[] GROUP_INNER_UNITS = {"", "拾", "佰", "仟" };

	/**
	 * 将数字金额转换为中文大写
	 *
	 * @param number 数字金额（支持整数和小数，建议使用BigDecimal避免精度问题）
	 * @return 中文大写金额字符串
	 */
	public static String digitUppercase(double number) {
		// 转换为BigDecimal处理，避免double精度问题
		BigDecimal amount = BigDecimal.valueOf(number).abs();
		// 处理负数标记
		String negativeSign = number < 0 ? "负" : "";

		// 拆分整数部分和小数部分（精确到分）
		BigDecimal integerPart = amount.setScale(0, RoundingMode.DOWN);
		BigDecimal fractionPart = amount.subtract(integerPart).multiply(new BigDecimal("100"))
				.setScale(0, RoundingMode.HALF_UP);

		// 构建结果
		StringBuilder result = new StringBuilder(negativeSign);

		// 处理整数部分
		processIntegerPart(integerPart, result);

		// 处理小数部分
		processFractionPart(fractionPart.intValue(), result);

		// 最终处理（确保零元情况正确显示）
		return handleSpecialCases(result.toString());
	}

	/**
	 * 处理整数部分
	 */
    private static void processIntegerPart(BigDecimal integerPart, StringBuilder result) {
        if (integerPart.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        long integer = integerPart.longValue();
        int groupIndex = 0;
        boolean hasNonZero = false;

        while (integer > 0) {
            long group = integer % 10000;
            if (group != 0) {
                String groupStr = convertGroupToChinese(group);
                groupStr += INTEGER_GROUP_UNITS[groupIndex];
                result.insert(0, groupStr);
                hasNonZero = true;
            } else if (hasNonZero) {
                result.insert(0, DIGITS[0]);
            }

            integer /= 10000;
            groupIndex++;
        }
    }

    /**
     * 将四位数以内的数字转换为中文表示
     */
    private static String convertGroupToChinese(long group) {
        StringBuilder groupStr = new StringBuilder();
        boolean needZero = false;

        for (int i = 0; i < 4; i++) {
            int digit = (int) (group % 10);
            if (digit != 0) {
                groupStr.insert(0, DIGITS[digit] + GROUP_INNER_UNITS[i]);
                needZero = false;
            } else {
                if (!groupStr.isEmpty() && !needZero) {
                    groupStr.insert(0, DIGITS[0]);
                    needZero = true;
                }
            }
            group /= 10;
        }

        return groupStr.toString();
    }

	/**
	 * 处理小数部分（角、分）
	 */
	private static void processFractionPart(int fraction, StringBuilder result) {
		int jiao = fraction / 10; // 角
		int fen = fraction % 10; // 分

		// 既无角也无分，加"整"
		if (jiao == 0 && fen == 0) {
			result.append("整");
			return;
		}

		// 处理角
		if (jiao != 0) {
			result.append(DIGITS[jiao]).append(FRACTION_UNITS[0]);
		} else if (!result.isEmpty()) { // 角为零但有整数部分或负数标记，需加零
			result.append(DIGITS[0]);
		}

		// 处理分
		if (fen != 0) {
			result.append(DIGITS[fen]).append(FRACTION_UNITS[1]);
		}
	}

	/**
	 * 处理特殊情况
	 */
	private static String handleSpecialCases(String original) {
		// 整数部分为0的情况
		if (!original.contains("元")) {
			// 纯小数且没有整数部分时补"零元"
			if (original.startsWith("负")) {
				return original.replace("负", "负零元")
						.replaceAll("零+", "零") // 合并连续零
						.replace("零整", "整"); // 特殊情况处理
			} else {
				return "零元" + original
						.replaceAll("零+", "零")
						.replace("零整", "整");
			}
		}

		// 合并连续零
		return original.replaceAll("零+", "零")
				// 处理单位前的零
				.replace("零元", "元")
				.replace("零万", "万")
				.replace("零亿", "亿")
				// 处理末尾零
				.replaceAll("零$", "")
				// 处理只有零的情况
				.replaceAll("^负?零元整$", "零元整");
	}
}
