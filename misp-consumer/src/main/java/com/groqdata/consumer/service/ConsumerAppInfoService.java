package com.groqdata.consumer.service;

import java.util.List;
import com.groqdata.consumer.domain.ConsumerAppInfo;

/**
 * 应用信息Service接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ConsumerAppInfoService 
{
    /**
     * 查询应用信息
     * 
     * @param id 应用信息主键
     * @return 应用信息
     */
    public ConsumerAppInfo selectConsumerAppInfoById(Long id);

    /**
     * 查询应用信息列表
     * 
     * @param consumerAppInfo 应用信息
     * @return 应用信息集合
     */
    public List<ConsumerAppInfo> selectConsumerAppInfoList(ConsumerAppInfo consumerAppInfo);

    /**
     * 新增应用信息
     * 
     * @param consumerAppInfo 应用信息
     * @return 结果
     */
    public int insertConsumerAppInfo(ConsumerAppInfo consumerAppInfo);

    /**
     * 修改应用信息
     * 
     * @param consumerAppInfo 应用信息
     * @return 结果
     */
    public int updateConsumerAppInfo(ConsumerAppInfo consumerAppInfo);

    /**
     * 批量删除应用信息
     * 
     * @param ids 需要删除的应用信息主键集合
     * @return 结果
     */
    public int deleteConsumerAppInfoByIds(Long[] ids);

    /**
     * 删除应用信息信息
     * 
     * @param id 应用信息主键
     * @return 结果
     */
    public int deleteConsumerAppInfoById(Long id);
}
