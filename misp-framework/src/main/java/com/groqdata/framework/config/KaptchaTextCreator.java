package com.groqdata.framework.config;

import java.util.concurrent.ThreadLocalRandom;
import com.google.code.kaptcha.text.impl.DefaultTextCreator;

/**
 * 验证码文本生成器
 *
 * @author ruoyi
 */
public class KaptchaTextCreator extends DefaultTextCreator {
	private static final String[] NUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");

	@Override
	public String getText() {
		// 使用ThreadLocalRandom生成验证码，适用于验证码场景的安全性要求
		int result = 0;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int x = random.nextInt(10);
		int y = random.nextInt(10);
		StringBuilder suChinese = new StringBuilder();
		int randomOperands = random.nextInt(3);

		switch (randomOperands) {
			case 0 :
				result = x * y;
				suChinese.append(NUMBERS[x]);
				suChinese.append("*");
				suChinese.append(NUMBERS[y]);
				break;
			case 1 :
				if ((x != 0) && y % x == 0) {
					result = y / x;
					suChinese.append(NUMBERS[y]);
					suChinese.append("/");
					suChinese.append(NUMBERS[x]);
				} else {
					result = x + y;
					suChinese.append(NUMBERS[x]);
					suChinese.append("+");
					suChinese.append(NUMBERS[y]);
				}
				break;
			default :
				if (x >= y) {
					result = x - y;
					suChinese.append(NUMBERS[x]);
					suChinese.append("-");
					suChinese.append(NUMBERS[y]);
				} else {
					result = y - x;
					suChinese.append(NUMBERS[y]);
					suChinese.append("-");
					suChinese.append(NUMBERS[x]);
				}
				break;
		}

		suChinese.append("=?@").append(result);
		return suChinese.toString();
	}
}
