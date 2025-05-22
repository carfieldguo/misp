package com.groqdata.provider.service;

import java.util.List;
import com.groqdata.provider.domain.ProviderPersonalInfo;

/**
 * 服务提供方-个人信息Service接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ProviderPersonalInfoService 
{
    /**
     * 查询服务提供方-个人信息
     * 
     * @param id 服务提供方-个人信息主键
     * @return 服务提供方-个人信息
     */
    public ProviderPersonalInfo selectProviderPersonalInfoById(Long id);

    /**
     * 查询服务提供方-个人信息列表
     * 
     * @param providerPersonalInfo 服务提供方-个人信息
     * @return 服务提供方-个人信息集合
     */
    public List<ProviderPersonalInfo> selectProviderPersonalInfoList(ProviderPersonalInfo providerPersonalInfo);

    /**
     * 新增服务提供方-个人信息
     * 
     * @param providerPersonalInfo 服务提供方-个人信息
     * @return 结果
     */
    public int insertProviderPersonalInfo(ProviderPersonalInfo providerPersonalInfo);

    /**
     * 修改服务提供方-个人信息
     * 
     * @param providerPersonalInfo 服务提供方-个人信息
     * @return 结果
     */
    public int updateProviderPersonalInfo(ProviderPersonalInfo providerPersonalInfo);

    /**
     * 批量删除服务提供方-个人信息
     * 
     * @param ids 需要删除的服务提供方-个人信息主键集合
     * @return 结果
     */
    public int deleteProviderPersonalInfoByIds(Long[] ids);

    /**
     * 删除服务提供方-个人信息信息
     * 
     * @param id 服务提供方-个人信息主键
     * @return 结果
     */
    public int deleteProviderPersonalInfoById(Long id);
}
