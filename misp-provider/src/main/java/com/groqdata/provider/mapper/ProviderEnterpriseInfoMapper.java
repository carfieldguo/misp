package com.groqdata.provider.mapper;

import java.util.List;
import com.groqdata.provider.domain.ProviderEnterpriseInfo;

/**
 * 服务提供方-企业信息Mapper接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ProviderEnterpriseInfoMapper 
{
    /**
     * 查询服务提供方-企业信息
     * 
     * @param id 服务提供方-企业信息主键
     * @return 服务提供方-企业信息
     */
    public ProviderEnterpriseInfo selectProviderEnterpriseInfoById(Long id);

    /**
     * 查询服务提供方-企业信息列表
     * 
     * @param providerEnterpriseInfo 服务提供方-企业信息
     * @return 服务提供方-企业信息集合
     */
    public List<ProviderEnterpriseInfo> selectProviderEnterpriseInfoList(ProviderEnterpriseInfo providerEnterpriseInfo);

    /**
     * 新增服务提供方-企业信息
     * 
     * @param providerEnterpriseInfo 服务提供方-企业信息
     * @return 结果
     */
    public int insertProviderEnterpriseInfo(ProviderEnterpriseInfo providerEnterpriseInfo);

    /**
     * 修改服务提供方-企业信息
     * 
     * @param providerEnterpriseInfo 服务提供方-企业信息
     * @return 结果
     */
    public int updateProviderEnterpriseInfo(ProviderEnterpriseInfo providerEnterpriseInfo);

    /**
     * 删除服务提供方-企业信息
     * 
     * @param id 服务提供方-企业信息主键
     * @return 结果
     */
    public int deleteProviderEnterpriseInfoById(Long id);

    /**
     * 批量删除服务提供方-企业信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProviderEnterpriseInfoByIds(Long[] ids);

	public ProviderEnterpriseInfo selectProviderUserInfoByAccount(String account);
}
