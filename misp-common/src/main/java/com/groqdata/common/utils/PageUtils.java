package com.groqdata.common.utils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.groqdata.common.core.page.PageDomain;
import com.groqdata.common.core.page.TableSupport;
import com.groqdata.common.utils.sql.SqlUtil;

/**
 * 分页工具类
 * 
 * @author MISP TEAM
 */
public class PageUtils extends PageHelper {
    private PageUtils() {
        throw new IllegalStateException("工具类不可实例化");
    }
    /**
	 * 设置请求分页数据
	 */
	public static void startPage() {
		PageDomain pageDomain = TableSupport.buildPageRequest();
		Integer pageNum = pageDomain.getPageNum();
		Integer pageSize = pageDomain.getPageSize();
		String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
		Boolean reasonable = pageDomain.getReasonable();
        PageMethod.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
	}

	/**
	 * 清理分页的线程变量
	 */
	public static void clearPage() {
        PageMethod.clearPage();
	}
}
