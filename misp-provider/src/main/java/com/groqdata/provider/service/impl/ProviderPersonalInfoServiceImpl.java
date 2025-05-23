package com.groqdata.provider.service.impl;

import java.util.List;
import com.groqdata.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.groqdata.provider.mapper.ProviderPersonalInfoMapper;
import com.groqdata.provider.domain.ProviderPersonalInfo;
import com.groqdata.provider.service.ProviderPersonalInfoService;

/**
 * 服务提供方-个人信息Service业务层处理
 * 
 * @author carfield
 * @date 2025-05-22
 */
@Service
public class ProviderPersonalInfoServiceImpl implements ProviderPersonalInfoService 
{
    @Autowired
    private ProviderPersonalInfoMapper providerPersonalInfoMapper;

    /**
     * 查询服务提供方-个人信息
     * 
     * @param id 服务提供方-个人信息主键
     * @return 服务提供方-个人信息
     */
    @Override
    public ProviderPersonalInfo selectProviderPersonalInfoById(Long id)
    {
        return providerPersonalInfoMapper.selectProviderPersonalInfoById(id);
    }

    /**
     * 查询服务提供方-个人信息列表
     * 
     * @param providerPersonalInfo 服务提供方-个人信息
     * @return 服务提供方-个人信息
     */
    @Override
    public List<ProviderPersonalInfo> selectProviderPersonalInfoList(ProviderPersonalInfo providerPersonalInfo)
    {
        return providerPersonalInfoMapper.selectProviderPersonalInfoList(providerPersonalInfo);
    }

    /**
     * 新增服务提供方-个人信息
     * 
     * @param providerPersonalInfo 服务提供方-个人信息
     * @return 结果
     */
    @Override
    public int insertProviderPersonalInfo(ProviderPersonalInfo providerPersonalInfo)
    {
        providerPersonalInfo.setCreateTime(DateUtils.getNowDate());
        return providerPersonalInfoMapper.insertProviderPersonalInfo(providerPersonalInfo);
    }

    /**
     * 修改服务提供方-个人信息
     * 
     * @param providerPersonalInfo 服务提供方-个人信息
     * @return 结果
     */
    @Override
    public int updateProviderPersonalInfo(ProviderPersonalInfo providerPersonalInfo)
    {
        providerPersonalInfo.setUpdateTime(DateUtils.getNowDate());
        return providerPersonalInfoMapper.updateProviderPersonalInfo(providerPersonalInfo);
    }

    /**
     * 批量删除服务提供方-个人信息
     * 
     * @param ids 需要删除的服务提供方-个人信息主键
     * @return 结果
     */
    @Override
    public int deleteProviderPersonalInfoByIds(Long[] ids)
    {
        return providerPersonalInfoMapper.deleteProviderPersonalInfoByIds(ids);
    }

    /**
     * 删除服务提供方-个人信息信息
     * 
     * @param id 服务提供方-个人信息主键
     * @return 结果
     */
    @Override
    public int deleteProviderPersonalInfoById(Long id)
    {
        return providerPersonalInfoMapper.deleteProviderPersonalInfoById(id);
    }

	@Override
	public ProviderPersonalInfo selectProviderUserInfoByAccount(String account) {
		return providerPersonalInfoMapper.selectProviderUserInfoByAccount(account);
	}
}
