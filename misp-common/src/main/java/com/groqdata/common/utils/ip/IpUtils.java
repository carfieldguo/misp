package com.groqdata.common.utils.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import com.groqdata.common.utils.ServletUtils;
import com.groqdata.common.utils.basic.StringHelper;

import org.apache.commons.lang3.StringUtils;

/**
 * 获取IP方法
 *
 * @author MISP TEAM
 */
public class IpUtils {
	private static final String UNKNOWN = "unknown";
	private static final String LOCALHOST_IP = "127.0.0.1";
	private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

	private IpUtils() {
		throw new IllegalStateException("工具类不可实例化");
	}

	public static final String REGX_0_255 = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
	// 匹配 ip
	public static final String REGX_IP = "((" + REGX_0_255 + "\\.){3}" + REGX_0_255 + ")";
	public static final String REGX_IP_WILDCARD = "(((\\*\\.){3}\\*)|(" + REGX_0_255 + "(\\.\\*){3})|(" + REGX_0_255
			+ "\\." + REGX_0_255 + ")(\\.\\*){2}" + "|((" + REGX_0_255 + "\\.){3}\\*))";
	// 匹配网段
	public static final String REGX_IP_SEG = "(" + REGX_IP + "\\-" + REGX_IP + ")";

	/**
	 * 获取客户端IP
	 *
	 * @return IP地址
	 */
	public static String getIpAddr() {
		return getIpAddr(ServletUtils.getRequest());
	}

	/**
	 * 获取客户端IP
	 *
	 * @param request 请求对象
	 * @return IP地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		if (request == null) {
			return UNKNOWN;
		}
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}

		if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return LOCALHOST_IPV6.equals(ip) ? LOCALHOST_IP : getMultistageReverseProxyIp(ip);
	}

	/**
	 * 检查是否为内部IP地址
	 *
	 * @param ip IP地址
	 * @return 结果
	 */
	public static boolean internalIp(String ip) {
		byte[] addr = textToNumericFormatV4(ip);
		return internalIp(addr) || LOCALHOST_IP.equals(ip);
	}

	/**
	 * 检查是否为内部IP地址
	 *
	 * @param addr byte地址
	 * @return 结果
	 */
	private static boolean internalIp(byte[] addr) {
		if (StringHelper.isNull(addr) || addr.length < 2) {
			return true;
		}
		final byte b0 = addr[0];
		final byte b1 = addr[1];
		// 10.x.x.x/8
		final byte SECTION_1 = 0x0A;
		// 172.16.x.x/12
		final byte SECTION_2 = (byte) 0xAC;
		final byte SECTION_3 = (byte) 0x10;
		final byte SECTION_4 = (byte) 0x1F;
		// 192.168.x.x/16
		final byte SECTION_5 = (byte) 0xC0;
		final byte SECTION_6 = (byte) 0xA8;

		// 10.x.x.x/8
		if (b0 == SECTION_1) {
			return true;
		}
		// 172.16.x.x/12
		if (b0 == SECTION_2 && b1 >= SECTION_3 && b1 <= SECTION_4) {
			return true;
		}
		// 192.168.x.x/16
		return b0 == SECTION_5 && b1 == SECTION_6;
	}

	/**
	 * 将IPv4地址转换成字节
	 *
	 * @param text IPv4地址
	 * @return byte 字节
	 */
	public static byte[] textToNumericFormatV4(String text) {
		if (text == null || text.isEmpty()) {
			return new byte[0];
		}

		String[] elements = text.split("\\.", -1);

		try {
			switch (elements.length) {
				case 1 :
					return parseOnePart(elements[0]);
				case 2 :
					return parseTwoParts(elements[0], elements[1]);
				case 3 :
					return parseThreeParts(elements[0], elements[1], elements[2]);
				case 4 :
					return parseFourParts(elements);
				default :
					return new byte[0];
			}
		} catch (NumberFormatException e) {
			return new byte[0];
		}
	}

	private static byte[] parseOnePart(String part) {
		long l = Long.parseLong(part);
		if (l < 0L || l > 4294967295L) {
			return new byte[0];
		}
		return new byte[]{
			(byte) (int) (l >> 24 & 0xFF),
			(byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF),
			(byte) (int) ((l & 0xFFFF) >> 8 & 0xFF),
			(byte) (int) (l & 0xFF)
		};
	}

	private static byte[] parseTwoParts(String part1, String part2) {
		long l1 = Integer.parseInt(part1);
		if (l1 < 0L || l1 > 255L) {
			return new byte[0];
		}
		long l2 = Integer.parseInt(part2);
		if (l2 < 0L || l2 > 16777215L) {
			return new byte[0];
		}
		return new byte[]{
			(byte) (int) (l1 & 0xFF),
			(byte) (int) (l2 >> 16 & 0xFF),
			(byte) (int) ((l2 & 0xFFFF) >> 8 & 0xFF),
			(byte) (int) (l2 & 0xFF)
		};
	}

	private static byte[] parseThreeParts(String part1, String part2, String part3) {
		long l1 = Integer.parseInt(part1);
		long l2 = Integer.parseInt(part2);
		long l3 = Integer.parseInt(part3);

		if (l1 < 0L || l1 > 255L || l2 < 0L || l2 > 255L || l3 < 0L || l3 > 65535L) {
			return new byte[0];
		}

		return new byte[]{
			(byte) (int) (l1 & 0xFF),
			(byte) (int) (l2 & 0xFF),
			(byte) (int) (l3 >> 8 & 0xFF),
			(byte) (int) (l3 & 0xFF)
		};
	}

	private static byte[] parseFourParts(String[] parts) {
		byte[] bytes = new byte[4];
		for (int i = 0; i < 4; ++i) {
			long l = Integer.parseInt(parts[i]);
			if (l < 0L || l > 255L) {
				return new byte[0];
			}
			bytes[i] = (byte) (int) (l & 0xFF);
		}
		return bytes;
	}

	/**
	 * 获取IP地址
	 *
	 * @return 本地IP地址
	 */
	public static String getHostIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return LOCALHOST_IP;
		}
	}

	/**
	 * 获取主机名
	 *
	 * @return 本地主机名
	 */
	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "未知";
		}
	}

	/**
	 * 从多级反向代理中获得第一个非unknown IP地址
	 *
	 * @param ip 获得的IP地址
	 * @return 第一个非unknown IP地址
	 */
	public static String getMultistageReverseProxyIp(String ip) {
		// 多级反向代理检测
		if (ip != null && ip.indexOf(",") >= 0) {
			final String[] ips = ip.trim().split(",");
			for (String subIp : ips) {
				if (!isUnknown(subIp)) {
					ip = subIp;
					break;
				}
			}
		}
		return StringUtils.substring(ip, 0, 255);
	}

	/**
	 * 检测给定字符串是否为未知，多用于检测HTTP请求相关
	 *
	 * @param checkString 被检测的字符串
	 * @return 是否未知
	 */
	public static boolean isUnknown(String checkString) {
		return StringUtils.isBlank(checkString) || UNKNOWN.equalsIgnoreCase(checkString);
	}

	/**
	 * 是否为IP
	 */
	public static boolean isIP(String ip) {
		return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP);
	}

	/**
	 * 是否为IP，或 *为间隔的通配符地址
	 */
	public static boolean isIpWildCard(String ip) {
		return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP_WILDCARD);
	}

	/**
	 * 检测参数是否在ip通配符里
	 */
	public static boolean ipIsInWildCardNoCheck(String ipWildCard, String ip) {
		String[] s1 = ipWildCard.split("\\.");
		String[] s2 = ip.split("\\.");
		boolean isMatchedSeg = true;
		for (int i = 0; i < s1.length && !s1[i].equals("*"); i++) {
			if (!s1[i].equals(s2[i])) {
				isMatchedSeg = false;
				break;
			}
		}
		return isMatchedSeg;
	}

	/**
	 * 是否为特定格式如:“10.10.10.1-10.10.10.99”的ip段字符串
	 */
	public static boolean isIPSegment(String ipSeg) {
		return StringUtils.isNotBlank(ipSeg) && ipSeg.matches(REGX_IP_SEG);
	}

	/**
	 * 判断ip是否在指定网段中
	 */
	public static boolean ipIsInNetNoCheck(String iparea, String ip) {
		int idx = iparea.indexOf('-');
		String[] sips = iparea.substring(0, idx).split("\\.");
		String[] sipe = iparea.substring(idx + 1).split("\\.");
		String[] sipt = ip.split("\\.");
		long ips = 0L;
		long ipe = 0L;
		long ipt = 0L;
		for (int i = 0; i < 4; ++i) {
			ips = ips << 8 | Integer.parseInt(sips[i]);
			ipe = ipe << 8 | Integer.parseInt(sipe[i]);
			ipt = ipt << 8 | Integer.parseInt(sipt[i]);
		}
		if (ips > ipe) {
			long t = ips;
			ips = ipe;
			ipe = t;
		}
		return ips <= ipt && ipt <= ipe;
	}

	/**
	 * 校验ip是否符合过滤串规则
	 *
	 * @param filter 过滤IP列表,支持后缀'*'通配,支持网段如:`10.10.10.1-10.10.10.99`
	 * @param ip 校验IP地址
	 * @return boolean 结果
	 */
	public static boolean isMatchedIp(String filter, String ip) {
		if (StringUtils.isEmpty(filter) || StringUtils.isEmpty(ip)) {
			return false;
		}

		String[] ips = filter.split(";");
		for (String iStr : ips) {
			if (iStr.equals(ip)
					|| (isIpWildCard(iStr) && ipIsInWildCardNoCheck(iStr, ip))
					|| (isIPSegment(iStr) && ipIsInNetNoCheck(iStr, ip))) {
				return true;
			}
		}
		return false;
	}

}
