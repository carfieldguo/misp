package com.groqdata.consumer.mapper;

import java.util.List;
import com.groqdata.consumer.domain.ConsumerEnterpriseInfo;

/**
 * 企业信息Mapper接口
 * 
 * @author carfield
 * @date 2025-05-22
 */
public interface ConsumerEnterpriseInfoMapper 
{
    /**
     * 查询企业信息
     * 
     * @param id 企业信息主键
     * @return 企业信息
     */
    public ConsumerEnterpriseInfo selectConsumerEnterpriseInfoById(Long id);

    /**
     * 查询企业信息列表
     * 
     * @param consumerEnterpriseInfo 企业信息
     * @return 企业信息集合
     */
    public List<ConsumerEnterpriseInfo> selectConsumerEnterpriseInfoList(ConsumerEnterpriseInfo consumerEnterpriseInfo);

    /**
     * 新增企业信息
     * 
     * @param consumerEnterpriseInfo 企业信息
     * @return 结果
     */
    public int insertConsumerEnterpriseInfo(ConsumerEnterpriseInfo consumerEnterpriseInfo);

    /**
     * 修改企业信息
     * 
     * @param consumerEnterpriseInfo 企业信息
     * @return 结果
     */
    public int updateConsumerEnterpriseInfo(ConsumerEnterpriseInfo consumerEnterpriseInfo);

    /**
     * 删除企业信息
     * 
     * @param id 企业信息主键
     * @return 结果
     */
    public int deleteConsumerEnterpriseInfoById(Long id);

    /**
     * 批量删除企业信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteConsumerEnterpriseInfoByIds(Long[] ids);

	public ConsumerEnterpriseInfo selectConsumerUserInfoByAccount(String account);
}
