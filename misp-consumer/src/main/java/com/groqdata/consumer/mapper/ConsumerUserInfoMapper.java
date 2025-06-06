package com.groqdata.consumer.mapper;

import java.util.List;
import com.groqdata.consumer.domain.ConsumerUserInfo;

/**
 * 账号信息Mapper接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ConsumerUserInfoMapper 
{
    /**
     * 查询账号信息
     * 
     * @param id 账号信息主键
     * @return 账号信息
     */
    public ConsumerUserInfo selectConsumerUserInfoById(Long id);

    /**
     * 查询账号信息列表
     * 
     * @param consumerUserInfo 账号信息
     * @return 账号信息集合
     */
    public List<ConsumerUserInfo> selectConsumerUserInfoList(ConsumerUserInfo consumerUserInfo);

    /**
     * 新增账号信息
     * 
     * @param consumerUserInfo 账号信息
     * @return 结果
     */
    public int insertConsumerUserInfo(ConsumerUserInfo consumerUserInfo);

    /**
     * 修改账号信息
     * 
     * @param consumerUserInfo 账号信息
     * @return 结果
     */
    public int updateConsumerUserInfo(ConsumerUserInfo consumerUserInfo);

    /**
     * 删除账号信息
     * 
     * @param id 账号信息主键
     * @return 结果
     */
    public int deleteConsumerUserInfoById(Long id);

    /**
     * 批量删除账号信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteConsumerUserInfoByIds(Long[] ids);
}
