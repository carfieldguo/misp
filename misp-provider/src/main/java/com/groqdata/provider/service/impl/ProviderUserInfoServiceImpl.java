package com.groqdata.provider.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.groqdata.common.utils.basic.DateHelper;
import com.groqdata.provider.domain.ProviderUserInfo;
import com.groqdata.provider.mapper.ProviderUserInfoMapper;
import com.groqdata.provider.service.ProviderUserInfoService;

/**
 * 账号信息Service业务层处理
 * 
 * @author carfield
 * @date 2025-05-22
 */
@Service
public class ProviderUserInfoServiceImpl implements ProviderUserInfoService {

	private ProviderUserInfoMapper providerUserInfoMapper;

	@Autowired
	public void setProviderUserInfoMapper(ProviderUserInfoMapper providerUserInfoMapper) {
		this.providerUserInfoMapper = providerUserInfoMapper;
	}

	/**
	 * 查询账号信息
	 * 
	 * @param id 账号信息主键
	 * @return 账号信息
	 */
	@Override
	public ProviderUserInfo selectProviderUserInfoById(Long id) {
		return providerUserInfoMapper.selectProviderUserInfoById(id);
	}

	/**
	 * 查询账号信息列表
	 * 
	 * @param providerUserInfo 账号信息
	 * @return 账号信息
	 */
	@Override
	public List<ProviderUserInfo> selectProviderUserInfoList(ProviderUserInfo providerUserInfo) {
		return providerUserInfoMapper.selectProviderUserInfoList(providerUserInfo);
	}

	/**
	 * 新增账号信息
	 * 
	 * @param providerUserInfo 账号信息
	 * @return 结果
	 */
	@Override
	public int insertProviderUserInfo(ProviderUserInfo providerUserInfo) {
		providerUserInfo.setCreateTime(DateHelper.getNowDate());
		return providerUserInfoMapper.insertProviderUserInfo(providerUserInfo);
	}

	/**
	 * 修改账号信息
	 * 
	 * @param providerUserInfo 账号信息
	 * @return 结果
	 */
	@Override
	public int updateProviderUserInfo(ProviderUserInfo providerUserInfo) {
		providerUserInfo.setUpdateTime(DateHelper.getNowDate());
		return providerUserInfoMapper.updateProviderUserInfo(providerUserInfo);
	}

	/**
	 * 批量删除账号信息
	 * 
	 * @param ids 需要删除的账号信息主键
	 * @return 结果
	 */
	@Override
	public int deleteProviderUserInfoByIds(Long[] ids) {
		return providerUserInfoMapper.deleteProviderUserInfoByIds(ids);
	}

	/**
	 * 删除账号信息信息
	 * 
	 * @param id 账号信息主键
	 * @return 结果
	 */
	@Override
	public int deleteProviderUserInfoById(Long id) {
		return providerUserInfoMapper.deleteProviderUserInfoById(id);
	}

	@Override
	public ProviderUserInfo selectProviderUserInfoByAccount(String account) {
		return providerUserInfoMapper.selectProviderUserInfoByAccount(account);
	}
}
