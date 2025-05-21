package com.groqdata.provider.mapper;

import java.util.List;
import com.groqdata.provider.domain.ProviderUserInfo;

/**
 * 服务提供方-账号信息Mapper接口
 * 
 * @author carfield
 * @date 2025-05-21
 */
public interface ProviderUserInfoMapper 
{
    /**
     * 查询服务提供方-账号信息
     * 
     * @param id 服务提供方-账号信息主键
     * @return 服务提供方-账号信息
     */
    public ProviderUserInfo selectProviderUserInfoById(Long id);

    /**
     * 查询服务提供方-账号信息列表
     * 
     * @param providerUserInfo 服务提供方-账号信息
     * @return 服务提供方-账号信息集合
     */
    public List<ProviderUserInfo> selectProviderUserInfoList(ProviderUserInfo providerUserInfo);

    /**
     * 新增服务提供方-账号信息
     * 
     * @param providerUserInfo 服务提供方-账号信息
     * @return 结果
     */
    public int insertProviderUserInfo(ProviderUserInfo providerUserInfo);

    /**
     * 修改服务提供方-账号信息
     * 
     * @param providerUserInfo 服务提供方-账号信息
     * @return 结果
     */
    public int updateProviderUserInfo(ProviderUserInfo providerUserInfo);

    /**
     * 删除服务提供方-账号信息
     * 
     * @param id 服务提供方-账号信息主键
     * @return 结果
     */
    public int deleteProviderUserInfoById(Long id);

    /**
     * 批量删除服务提供方-账号信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProviderUserInfoByIds(Long[] ids);
}
