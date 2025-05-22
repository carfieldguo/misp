package com.groqdata.consumer.service.impl;

import java.util.List;
import com.groqdata.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.groqdata.consumer.mapper.ConsumerPersonalInfoMapper;
import com.groqdata.consumer.domain.ConsumerPersonalInfo;
import com.groqdata.consumer.service.ConsumerPersonalInfoService;

/**
 * 服务购买方-个人信息Service业务层处理
 * 
 * @author carfield
 * @date 2025-05-22
 */
@Service
public class ConsumerPersonalInfoServiceImpl implements ConsumerPersonalInfoService 
{
    @Autowired
    private ConsumerPersonalInfoMapper consumerPersonalInfoMapper;

    /**
     * 查询服务购买方-个人信息
     * 
     * @param id 服务购买方-个人信息主键
     * @return 服务购买方-个人信息
     */
    @Override
    public ConsumerPersonalInfo selectConsumerPersonalInfoById(Long id)
    {
        return consumerPersonalInfoMapper.selectConsumerPersonalInfoById(id);
    }

    /**
     * 查询服务购买方-个人信息列表
     * 
     * @param consumerPersonalInfo 服务购买方-个人信息
     * @return 服务购买方-个人信息
     */
    @Override
    public List<ConsumerPersonalInfo> selectConsumerPersonalInfoList(ConsumerPersonalInfo consumerPersonalInfo)
    {
        return consumerPersonalInfoMapper.selectConsumerPersonalInfoList(consumerPersonalInfo);
    }

    /**
     * 新增服务购买方-个人信息
     * 
     * @param consumerPersonalInfo 服务购买方-个人信息
     * @return 结果
     */
    @Override
    public int insertConsumerPersonalInfo(ConsumerPersonalInfo consumerPersonalInfo)
    {
        consumerPersonalInfo.setCreateTime(DateUtils.getNowDate());
        return consumerPersonalInfoMapper.insertConsumerPersonalInfo(consumerPersonalInfo);
    }

    /**
     * 修改服务购买方-个人信息
     * 
     * @param consumerPersonalInfo 服务购买方-个人信息
     * @return 结果
     */
    @Override
    public int updateConsumerPersonalInfo(ConsumerPersonalInfo consumerPersonalInfo)
    {
        consumerPersonalInfo.setUpdateTime(DateUtils.getNowDate());
        return consumerPersonalInfoMapper.updateConsumerPersonalInfo(consumerPersonalInfo);
    }

    /**
     * 批量删除服务购买方-个人信息
     * 
     * @param ids 需要删除的服务购买方-个人信息主键
     * @return 结果
     */
    @Override
    public int deleteConsumerPersonalInfoByIds(Long[] ids)
    {
        return consumerPersonalInfoMapper.deleteConsumerPersonalInfoByIds(ids);
    }

    /**
     * 删除服务购买方-个人信息信息
     * 
     * @param id 服务购买方-个人信息主键
     * @return 结果
     */
    @Override
    public int deleteConsumerPersonalInfoById(Long id)
    {
        return consumerPersonalInfoMapper.deleteConsumerPersonalInfoById(id);
    }
}
