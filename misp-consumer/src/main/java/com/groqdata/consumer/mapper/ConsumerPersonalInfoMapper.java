package com.groqdata.consumer.mapper;

import java.util.List;
import com.groqdata.consumer.domain.ConsumerPersonalInfo;

/**
 * 服务购买方-个人信息Mapper接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ConsumerPersonalInfoMapper 
{
    /**
     * 查询服务购买方-个人信息
     * 
     * @param id 服务购买方-个人信息主键
     * @return 服务购买方-个人信息
     */
    public ConsumerPersonalInfo selectConsumerPersonalInfoById(Long id);

    /**
     * 查询服务购买方-个人信息列表
     * 
     * @param consumerPersonalInfo 服务购买方-个人信息
     * @return 服务购买方-个人信息集合
     */
    public List<ConsumerPersonalInfo> selectConsumerPersonalInfoList(ConsumerPersonalInfo consumerPersonalInfo);

    /**
     * 新增服务购买方-个人信息
     * 
     * @param consumerPersonalInfo 服务购买方-个人信息
     * @return 结果
     */
    public int insertConsumerPersonalInfo(ConsumerPersonalInfo consumerPersonalInfo);

    /**
     * 修改服务购买方-个人信息
     * 
     * @param consumerPersonalInfo 服务购买方-个人信息
     * @return 结果
     */
    public int updateConsumerPersonalInfo(ConsumerPersonalInfo consumerPersonalInfo);

    /**
     * 删除服务购买方-个人信息
     * 
     * @param id 服务购买方-个人信息主键
     * @return 结果
     */
    public int deleteConsumerPersonalInfoById(Long id);

    /**
     * 批量删除服务购买方-个人信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteConsumerPersonalInfoByIds(Long[] ids);

    /**
	 * 根据账号查询服务购买方-个人信息
	 * 
	 * @param account 账号
	 * @return 服务购买方-个人信息
	 */
	public ConsumerPersonalInfo selectConsumerUserInfoByAccount(String account);
}
