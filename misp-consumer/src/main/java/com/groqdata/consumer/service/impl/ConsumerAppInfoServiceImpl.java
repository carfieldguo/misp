package com.groqdata.consumer.service.impl;

import java.util.List;
import com.groqdata.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.groqdata.consumer.mapper.ConsumerAppInfoMapper;
import com.groqdata.consumer.domain.ConsumerAppInfo;
import com.groqdata.consumer.service.ConsumerAppInfoService;

/**
 * 服务购买方-应用信息Service业务层处理
 * 
 * @author carfield
 * @date 2025-05-21
 */
@Service
public class ConsumerAppInfoServiceImpl implements ConsumerAppInfoService 
{
    @Autowired
    private ConsumerAppInfoMapper consumerAppInfoMapper;

    /**
     * 查询服务购买方-应用信息
     * 
     * @param id 服务购买方-应用信息主键
     * @return 服务购买方-应用信息
     */
    @Override
    public ConsumerAppInfo selectConsumerAppInfoById(Long id)
    {
        return consumerAppInfoMapper.selectConsumerAppInfoById(id);
    }

    /**
     * 查询服务购买方-应用信息列表
     * 
     * @param consumerAppInfo 服务购买方-应用信息
     * @return 服务购买方-应用信息
     */
    @Override
    public List<ConsumerAppInfo> selectConsumerAppInfoList(ConsumerAppInfo consumerAppInfo)
    {
        return consumerAppInfoMapper.selectConsumerAppInfoList(consumerAppInfo);
    }

    /**
     * 新增服务购买方-应用信息
     * 
     * @param consumerAppInfo 服务购买方-应用信息
     * @return 结果
     */
    @Override
    public int insertConsumerAppInfo(ConsumerAppInfo consumerAppInfo)
    {
        consumerAppInfo.setCreateTime(DateUtils.getNowDate());
        return consumerAppInfoMapper.insertConsumerAppInfo(consumerAppInfo);
    }

    /**
     * 修改服务购买方-应用信息
     * 
     * @param consumerAppInfo 服务购买方-应用信息
     * @return 结果
     */
    @Override
    public int updateConsumerAppInfo(ConsumerAppInfo consumerAppInfo)
    {
        consumerAppInfo.setUpdateTime(DateUtils.getNowDate());
        return consumerAppInfoMapper.updateConsumerAppInfo(consumerAppInfo);
    }

    /**
     * 批量删除服务购买方-应用信息
     * 
     * @param ids 需要删除的服务购买方-应用信息主键
     * @return 结果
     */
    @Override
    public int deleteConsumerAppInfoByIds(Long[] ids)
    {
        return consumerAppInfoMapper.deleteConsumerAppInfoByIds(ids);
    }

    /**
     * 删除服务购买方-应用信息信息
     * 
     * @param id 服务购买方-应用信息主键
     * @return 结果
     */
    @Override
    public int deleteConsumerAppInfoById(Long id)
    {
        return consumerAppInfoMapper.deleteConsumerAppInfoById(id);
    }
}
