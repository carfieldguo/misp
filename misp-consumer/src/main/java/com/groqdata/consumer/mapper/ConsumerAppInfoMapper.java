package com.groqdata.consumer.mapper;

import java.util.List;
import com.groqdata.consumer.domain.ConsumerAppInfo;

/**
 * 服务购买方-应用信息Mapper接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ConsumerAppInfoMapper 
{
    /**
     * 查询服务购买方-应用信息
     * 
     * @param id 服务购买方-应用信息主键
     * @return 服务购买方-应用信息
     */
    public ConsumerAppInfo selectConsumerAppInfoById(Long id);

    /**
     * 查询服务购买方-应用信息列表
     * 
     * @param consumerAppInfo 服务购买方-应用信息
     * @return 服务购买方-应用信息集合
     */
    public List<ConsumerAppInfo> selectConsumerAppInfoList(ConsumerAppInfo consumerAppInfo);

    /**
     * 新增服务购买方-应用信息
     * 
     * @param consumerAppInfo 服务购买方-应用信息
     * @return 结果
     */
    public int insertConsumerAppInfo(ConsumerAppInfo consumerAppInfo);

    /**
     * 修改服务购买方-应用信息
     * 
     * @param consumerAppInfo 服务购买方-应用信息
     * @return 结果
     */
    public int updateConsumerAppInfo(ConsumerAppInfo consumerAppInfo);

    /**
     * 删除服务购买方-应用信息
     * 
     * @param id 服务购买方-应用信息主键
     * @return 结果
     */
    public int deleteConsumerAppInfoById(Long id);

    /**
     * 批量删除服务购买方-应用信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteConsumerAppInfoByIds(Long[] ids);
}
