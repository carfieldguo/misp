package com.groqdata.provider.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.groqdata.common.utils.DateHelper;
import com.groqdata.provider.domain.ProviderPersonalInfo;
import com.groqdata.provider.mapper.ProviderPersonalInfoMapper;
import com.groqdata.provider.service.ProviderPersonalInfoService;

/**
 * 个人信息Service业务层处理
 * 
 * @author carfield
 * @date 2025-05-22
 */
@Service
public class ProviderPersonalInfoServiceImpl implements ProviderPersonalInfoService {

	private ProviderPersonalInfoMapper providerPersonalInfoMapper;

	@Autowired
	public void setProviderPersonalInfoMapper(ProviderPersonalInfoMapper providerPersonalInfoMapper) {
		this.providerPersonalInfoMapper = providerPersonalInfoMapper;
	}

	/**
	 * 查询个人信息
	 * 
	 * @param id 个人信息主键
	 * @return 个人信息
	 */
	@Override
	public ProviderPersonalInfo selectProviderPersonalInfoById(Long id) {
		return providerPersonalInfoMapper.selectProviderPersonalInfoById(id);
	}

	/**
	 * 查询个人信息列表
	 * 
	 * @param providerPersonalInfo 个人信息
	 * @return 个人信息
	 */
	@Override
	public List<ProviderPersonalInfo> selectProviderPersonalInfoList(ProviderPersonalInfo providerPersonalInfo) {
		return providerPersonalInfoMapper.selectProviderPersonalInfoList(providerPersonalInfo);
	}

	/**
	 * 新增个人信息
	 * 
	 * @param providerPersonalInfo 个人信息
	 * @return 结果
	 */
	@Override
	public int insertProviderPersonalInfo(ProviderPersonalInfo providerPersonalInfo) {
		providerPersonalInfo.setCreateTime(DateHelper.getNowDate());
		return providerPersonalInfoMapper.insertProviderPersonalInfo(providerPersonalInfo);
	}

	/**
	 * 修改个人信息
	 * 
	 * @param providerPersonalInfo 个人信息
	 * @return 结果
	 */
	@Override
	public int updateProviderPersonalInfo(ProviderPersonalInfo providerPersonalInfo) {
		providerPersonalInfo.setUpdateTime(DateHelper.getNowDate());
		return providerPersonalInfoMapper.updateProviderPersonalInfo(providerPersonalInfo);
	}

	/**
	 * 批量删除个人信息
	 * 
	 * @param ids 需要删除的个人信息主键
	 * @return 结果
	 */
	@Override
	public int deleteProviderPersonalInfoByIds(Long[] ids) {
		return providerPersonalInfoMapper.deleteProviderPersonalInfoByIds(ids);
	}

	/**
	 * 删除个人信息信息
	 * 
	 * @param id 个人信息主键
	 * @return 结果
	 */
	@Override
	public int deleteProviderPersonalInfoById(Long id) {
		return providerPersonalInfoMapper.deleteProviderPersonalInfoById(id);
	}

	@Override
	public ProviderPersonalInfo selectProviderUserInfoByAccount(String account) {
		return providerPersonalInfoMapper.selectProviderUserInfoByAccount(account);
	}
}
