package com.groqdata.provider.service.impl;

import java.util.List;
import com.groqdata.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.groqdata.provider.mapper.ProviderEnterpriseInfoMapper;
import com.groqdata.provider.domain.ProviderEnterpriseInfo;
import com.groqdata.provider.service.ProviderEnterpriseInfoService;

/**
 * 服务提供方-企业信息Service业务层处理
 * 
 * @author carfield
 * @date 2025-05-22
 */
@Service
public class ProviderEnterpriseInfoServiceImpl implements ProviderEnterpriseInfoService 
{
    @Autowired
    private ProviderEnterpriseInfoMapper providerEnterpriseInfoMapper;

    /**
     * 查询服务提供方-企业信息
     * 
     * @param id 服务提供方-企业信息主键
     * @return 服务提供方-企业信息
     */
    @Override
    public ProviderEnterpriseInfo selectProviderEnterpriseInfoById(Long id)
    {
        return providerEnterpriseInfoMapper.selectProviderEnterpriseInfoById(id);
    }

    /**
     * 查询服务提供方-企业信息列表
     * 
     * @param providerEnterpriseInfo 服务提供方-企业信息
     * @return 服务提供方-企业信息
     */
    @Override
    public List<ProviderEnterpriseInfo> selectProviderEnterpriseInfoList(ProviderEnterpriseInfo providerEnterpriseInfo)
    {
        return providerEnterpriseInfoMapper.selectProviderEnterpriseInfoList(providerEnterpriseInfo);
    }

    /**
     * 新增服务提供方-企业信息
     * 
     * @param providerEnterpriseInfo 服务提供方-企业信息
     * @return 结果
     */
    @Override
    public int insertProviderEnterpriseInfo(ProviderEnterpriseInfo providerEnterpriseInfo)
    {
        providerEnterpriseInfo.setCreateTime(DateUtils.getNowDate());
        return providerEnterpriseInfoMapper.insertProviderEnterpriseInfo(providerEnterpriseInfo);
    }

    /**
     * 修改服务提供方-企业信息
     * 
     * @param providerEnterpriseInfo 服务提供方-企业信息
     * @return 结果
     */
    @Override
    public int updateProviderEnterpriseInfo(ProviderEnterpriseInfo providerEnterpriseInfo)
    {
        providerEnterpriseInfo.setUpdateTime(DateUtils.getNowDate());
        return providerEnterpriseInfoMapper.updateProviderEnterpriseInfo(providerEnterpriseInfo);
    }

    /**
     * 批量删除服务提供方-企业信息
     * 
     * @param ids 需要删除的服务提供方-企业信息主键
     * @return 结果
     */
    @Override
    public int deleteProviderEnterpriseInfoByIds(Long[] ids)
    {
        return providerEnterpriseInfoMapper.deleteProviderEnterpriseInfoByIds(ids);
    }

    /**
     * 删除服务提供方-企业信息信息
     * 
     * @param id 服务提供方-企业信息主键
     * @return 结果
     */
    @Override
    public int deleteProviderEnterpriseInfoById(Long id)
    {
        return providerEnterpriseInfoMapper.deleteProviderEnterpriseInfoById(id);
    }

	@Override
	public ProviderEnterpriseInfo selectProviderUserInfoByAccount(String account) {
		return providerEnterpriseInfoMapper.selectProviderUserInfoByAccount(account);
	}
}
