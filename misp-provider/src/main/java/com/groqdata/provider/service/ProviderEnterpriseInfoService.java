package com.groqdata.provider.service;

import java.util.List;
import com.groqdata.provider.domain.ProviderEnterpriseInfo;

/**
 * 企业信息Service接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ProviderEnterpriseInfoService {
	/**
	 * 查询企业信息
	 * 
	 * @param id 企业信息主键
	 * @return 企业信息
	 */
	public ProviderEnterpriseInfo selectProviderEnterpriseInfoById(Long id);

	/**
	 * 查询企业信息列表
	 * 
	 * @param providerEnterpriseInfo 企业信息
	 * @return 企业信息集合
	 */
	public List<ProviderEnterpriseInfo> selectProviderEnterpriseInfoList(ProviderEnterpriseInfo providerEnterpriseInfo);

	/**
	 * 新增企业信息
	 * 
	 * @param providerEnterpriseInfo 企业信息
	 * @return 结果
	 */
	public int insertProviderEnterpriseInfo(ProviderEnterpriseInfo providerEnterpriseInfo);

	/**
	 * 修改企业信息
	 * 
	 * @param providerEnterpriseInfo 企业信息
	 * @return 结果
	 */
	public int updateProviderEnterpriseInfo(ProviderEnterpriseInfo providerEnterpriseInfo);

	/**
	 * 批量删除企业信息
	 * 
	 * @param ids 需要删除的企业信息主键集合
	 * @return 结果
	 */
	public int deleteProviderEnterpriseInfoByIds(Long[] ids);

	/**
	 * 删除企业信息信息
	 * 
	 * @param id 企业信息主键
	 * @return 结果
	 */
	public int deleteProviderEnterpriseInfoById(Long id);

	public ProviderEnterpriseInfo selectProviderUserInfoByAccount(String account);
}
