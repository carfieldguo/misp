package com.groqdata.consumer.service;

import java.util.List;
import com.groqdata.consumer.domain.ConsumerEnterpriseInfo;

/**
 * 服务购买方-企业信息Service接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ConsumerEnterpriseInfoService 
{
    /**
     * 查询服务购买方-企业信息
     * 
     * @param id 服务购买方-企业信息主键
     * @return 服务购买方-企业信息
     */
    public ConsumerEnterpriseInfo selectConsumerEnterpriseInfoById(Long id);

    /**
     * 查询服务购买方-企业信息列表
     * 
     * @param consumerEnterpriseInfo 服务购买方-企业信息
     * @return 服务购买方-企业信息集合
     */
    public List<ConsumerEnterpriseInfo> selectConsumerEnterpriseInfoList(ConsumerEnterpriseInfo consumerEnterpriseInfo);

    /**
     * 新增服务购买方-企业信息
     * 
     * @param consumerEnterpriseInfo 服务购买方-企业信息
     * @return 结果
     */
    public int insertConsumerEnterpriseInfo(ConsumerEnterpriseInfo consumerEnterpriseInfo);

    /**
     * 修改服务购买方-企业信息
     * 
     * @param consumerEnterpriseInfo 服务购买方-企业信息
     * @return 结果
     */
    public int updateConsumerEnterpriseInfo(ConsumerEnterpriseInfo consumerEnterpriseInfo);

    /**
     * 批量删除服务购买方-企业信息
     * 
     * @param ids 需要删除的服务购买方-企业信息主键集合
     * @return 结果
     */
    public int deleteConsumerEnterpriseInfoByIds(Long[] ids);

    /**
     * 删除服务购买方-企业信息信息
     * 
     * @param id 服务购买方-企业信息主键
     * @return 结果
     */
    public int deleteConsumerEnterpriseInfoById(Long id);

    /**
	 * 根据账号获取服务购买方-企业信息详细信息
	 * 
	 * @param account 账号
	 * @return 服务提供方-账号信息
	 */
	public ConsumerEnterpriseInfo selectConsumerUserInfoByAccount(String account);
}
