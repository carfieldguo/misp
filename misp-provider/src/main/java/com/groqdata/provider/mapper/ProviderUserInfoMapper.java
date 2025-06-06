package com.groqdata.provider.mapper;

import java.util.List;
import com.groqdata.provider.domain.ProviderUserInfo;

/**
 * 账号信息Mapper接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ProviderUserInfoMapper 
{
    /**
     * 查询账号信息
     * 
     * @param id 账号信息主键
     * @return 账号信息
     */
    public ProviderUserInfo selectProviderUserInfoById(Long id);

    /**
     * 查询账号信息列表
     * 
     * @param providerUserInfo 账号信息
     * @return 账号信息集合
     */
    public List<ProviderUserInfo> selectProviderUserInfoList(ProviderUserInfo providerUserInfo);

    /**
     * 新增账号信息
     * 
     * @param providerUserInfo 账号信息
     * @return 结果
     */
    public int insertProviderUserInfo(ProviderUserInfo providerUserInfo);

    /**
     * 修改账号信息
     * 
     * @param providerUserInfo 账号信息
     * @return 结果
     */
    public int updateProviderUserInfo(ProviderUserInfo providerUserInfo);

    /**
     * 删除账号信息
     * 
     * @param id 账号信息主键
     * @return 结果
     */
    public int deleteProviderUserInfoById(Long id);

    /**
     * 批量删除账号信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProviderUserInfoByIds(Long[] ids);

	public ProviderUserInfo selectProviderUserInfoByAccount(String account);
}
