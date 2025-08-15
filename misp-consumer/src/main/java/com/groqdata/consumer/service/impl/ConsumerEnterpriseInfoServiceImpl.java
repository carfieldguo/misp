package com.groqdata.consumer.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.groqdata.common.utils.DateHelper;
import com.groqdata.consumer.domain.ConsumerEnterpriseInfo;
import com.groqdata.consumer.mapper.ConsumerEnterpriseInfoMapper;
import com.groqdata.consumer.service.ConsumerEnterpriseInfoService;

/**
 * 企业信息Service业务层处理
 * 
 * @author carfield
 * @date 2025-05-22
 */
@Service
public class ConsumerEnterpriseInfoServiceImpl implements ConsumerEnterpriseInfoService {

	private ConsumerEnterpriseInfoMapper consumerEnterpriseInfoMapper;

	@Autowired
	public void setConsumerEnterpriseInfoMapper(ConsumerEnterpriseInfoMapper consumerEnterpriseInfoMapper) {
		this.consumerEnterpriseInfoMapper = consumerEnterpriseInfoMapper;
	}

	/**
	 * 查询企业信息
	 * 
	 * @param id 企业信息主键
	 * @return 企业信息
	 */
	@Override
	public ConsumerEnterpriseInfo selectConsumerEnterpriseInfoById(Long id) {
		return consumerEnterpriseInfoMapper.selectConsumerEnterpriseInfoById(id);
	}

	/**
	 * 查询企业信息列表
	 * 
	 * @param consumerEnterpriseInfo 企业信息
	 * @return 企业信息
	 */
	@Override
	public List<ConsumerEnterpriseInfo> selectConsumerEnterpriseInfoList(
		ConsumerEnterpriseInfo consumerEnterpriseInfo) {
		return consumerEnterpriseInfoMapper.selectConsumerEnterpriseInfoList(consumerEnterpriseInfo);
	}

	/**
	 * 新增企业信息
	 * 
	 * @param consumerEnterpriseInfo 企业信息
	 * @return 结果
	 */
	@Override
	public int insertConsumerEnterpriseInfo(ConsumerEnterpriseInfo consumerEnterpriseInfo) {
		consumerEnterpriseInfo.setCreateTime(DateHelper.getNowDate());
		return consumerEnterpriseInfoMapper.insertConsumerEnterpriseInfo(consumerEnterpriseInfo);
	}

	/**
	 * 修改企业信息
	 * 
	 * @param consumerEnterpriseInfo 企业信息
	 * @return 结果
	 */
	@Override
	public int updateConsumerEnterpriseInfo(ConsumerEnterpriseInfo consumerEnterpriseInfo) {
		consumerEnterpriseInfo.setUpdateTime(DateHelper.getNowDate());
		return consumerEnterpriseInfoMapper.updateConsumerEnterpriseInfo(consumerEnterpriseInfo);
	}

	/**
	 * 批量删除企业信息
	 * 
	 * @param ids 需要删除的企业信息主键
	 * @return 结果
	 */
	@Override
	public int deleteConsumerEnterpriseInfoByIds(Long[] ids) {
		return consumerEnterpriseInfoMapper.deleteConsumerEnterpriseInfoByIds(ids);
	}

	/**
	 * 删除企业信息信息
	 * 
	 * @param id 企业信息主键
	 * @return 结果
	 */
	@Override
	public int deleteConsumerEnterpriseInfoById(Long id) {
		return consumerEnterpriseInfoMapper.deleteConsumerEnterpriseInfoById(id);
	}

	@Override
	public ConsumerEnterpriseInfo selectConsumerUserInfoByAccount(String account) {
		return consumerEnterpriseInfoMapper.selectConsumerUserInfoByAccount(account);
	}
}
