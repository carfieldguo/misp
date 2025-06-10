package com.groqdata.consumer.service.impl;

import java.util.List;
import com.groqdata.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.groqdata.consumer.mapper.ConsumerUserInfoMapper;
import com.groqdata.consumer.domain.ConsumerUserInfo;
import com.groqdata.consumer.service.ConsumerUserInfoService;

/**
 * 账号信息Service业务层处理
 * 
 * @author carfield
 * @date 2025-05-22
 */
@Service
public class ConsumerUserInfoServiceImpl implements ConsumerUserInfoService 
{
    
    private ConsumerUserInfoMapper consumerUserInfoMapper;
    
    @Autowired
    public void setConsumerUserInfoMapper(ConsumerUserInfoMapper consumerUserInfoMapper) {
		this.consumerUserInfoMapper = consumerUserInfoMapper;
	}
    

    /**
     * 查询账号信息
     * 
     * @param id 账号信息主键
     * @return 账号信息
     */
    @Override
    public ConsumerUserInfo selectConsumerUserInfoById(Long id)
    {
        return consumerUserInfoMapper.selectConsumerUserInfoById(id);
    }

    /**
     * 查询账号信息列表
     * 
     * @param consumerUserInfo 账号信息
     * @return 账号信息
     */
    @Override
    public List<ConsumerUserInfo> selectConsumerUserInfoList(ConsumerUserInfo consumerUserInfo)
    {
        return consumerUserInfoMapper.selectConsumerUserInfoList(consumerUserInfo);
    }

    /**
     * 新增账号信息
     * 
     * @param consumerUserInfo 账号信息
     * @return 结果
     */
    @Override
    public int insertConsumerUserInfo(ConsumerUserInfo consumerUserInfo)
    {
        consumerUserInfo.setCreateTime(DateUtils.getNowDate());
        return consumerUserInfoMapper.insertConsumerUserInfo(consumerUserInfo);
    }

    /**
     * 修改账号信息
     * 
     * @param consumerUserInfo 账号信息
     * @return 结果
     */
    @Override
    public int updateConsumerUserInfo(ConsumerUserInfo consumerUserInfo)
    {
        consumerUserInfo.setUpdateTime(DateUtils.getNowDate());
        return consumerUserInfoMapper.updateConsumerUserInfo(consumerUserInfo);
    }

    /**
     * 批量删除账号信息
     * 
     * @param ids 需要删除的账号信息主键
     * @return 结果
     */
    @Override
    public int deleteConsumerUserInfoByIds(Long[] ids)
    {
        return consumerUserInfoMapper.deleteConsumerUserInfoByIds(ids);
    }

    /**
     * 删除账号信息信息
     * 
     * @param id 账号信息主键
     * @return 结果
     */
    @Override
    public int deleteConsumerUserInfoById(Long id)
    {
        return consumerUserInfoMapper.deleteConsumerUserInfoById(id);
    }
}
