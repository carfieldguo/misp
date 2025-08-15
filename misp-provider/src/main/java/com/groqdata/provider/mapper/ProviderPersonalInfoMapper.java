package com.groqdata.provider.mapper;

import java.util.List;
import com.groqdata.provider.domain.ProviderPersonalInfo;

/**
 * 个人信息Mapper接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ProviderPersonalInfoMapper {
	/**
	 * 查询个人信息
	 * 
	 * @param id 个人信息主键
	 * @return 个人信息
	 */
	public ProviderPersonalInfo selectProviderPersonalInfoById(Long id);

	/**
	 * 查询个人信息列表
	 * 
	 * @param providerPersonalInfo 个人信息
	 * @return 个人信息集合
	 */
	public List<ProviderPersonalInfo> selectProviderPersonalInfoList(ProviderPersonalInfo providerPersonalInfo);

	/**
	 * 新增个人信息
	 * 
	 * @param providerPersonalInfo 个人信息
	 * @return 结果
	 */
	public int insertProviderPersonalInfo(ProviderPersonalInfo providerPersonalInfo);

	/**
	 * 修改个人信息
	 * 
	 * @param providerPersonalInfo 个人信息
	 * @return 结果
	 */
	public int updateProviderPersonalInfo(ProviderPersonalInfo providerPersonalInfo);

	/**
	 * 删除个人信息
	 * 
	 * @param id 个人信息主键
	 * @return 结果
	 */
	public int deleteProviderPersonalInfoById(Long id);

	/**
	 * 批量删除个人信息
	 * 
	 * @param ids 需要删除的数据主键集合
	 * @return 结果
	 */
	public int deleteProviderPersonalInfoByIds(Long[] ids);

	public ProviderPersonalInfo selectProviderUserInfoByAccount(String account);
}
