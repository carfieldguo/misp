package com.groqdata.consumer.service.impl;

import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.groqdata.consumer.mapper.ConsumerAppInfoMapper;
import com.groqdata.common.utils.DateHelper;
import com.groqdata.consumer.domain.ConsumerAppInfo;
import com.groqdata.consumer.service.ConsumerAppInfoService;

/**
 * 应用信息Service业务层处理
 * 
 * @author carfield
 * @date 2025-05-22
 */
@Service
public class ConsumerAppInfoServiceImpl implements ConsumerAppInfoService {

	private ConsumerAppInfoMapper consumerAppInfoMapper;

	@Autowired
	public void setConsumerAppInfoMapper(ConsumerAppInfoMapper consumerAppInfoMapper) {
		this.consumerAppInfoMapper = consumerAppInfoMapper;
	}

	/**
	 * 查询应用信息
	 * 
	 * @param id 应用信息主键
	 * @return 应用信息
	 */
	@Override
	public ConsumerAppInfo selectConsumerAppInfoById(Long id) {
		return consumerAppInfoMapper.selectConsumerAppInfoById(id);
	}

	/**
	 * 查询应用信息列表
	 * 
	 * @param consumerAppInfo 应用信息
	 * @return 应用信息
	 */
	@Override
	public List<ConsumerAppInfo> selectConsumerAppInfoList(ConsumerAppInfo consumerAppInfo) {
		return consumerAppInfoMapper.selectConsumerAppInfoList(consumerAppInfo);
	}

	/**
	 * 新增应用信息
	 * 
	 * @param consumerAppInfo 应用信息
	 * @return 结果
	 */
	@Override
	public int insertConsumerAppInfo(ConsumerAppInfo consumerAppInfo) {
		consumerAppInfo.setCreateTime(DateHelper.getNowDate());
		return consumerAppInfoMapper.insertConsumerAppInfo(consumerAppInfo);
	}

	/**
	 * 修改应用信息
	 * 
	 * @param consumerAppInfo 应用信息
	 * @return 结果
	 */
	@Override
	public int updateConsumerAppInfo(ConsumerAppInfo consumerAppInfo) {
		consumerAppInfo.setUpdateTime(DateHelper.getNowDate());
		return consumerAppInfoMapper.updateConsumerAppInfo(consumerAppInfo);
	}

	/**
	 * 批量删除应用信息
	 * 
	 * @param ids 需要删除的应用信息主键
	 * @return 结果
	 */
	@Override
	public int deleteConsumerAppInfoByIds(Long[] ids) {
		return consumerAppInfoMapper.deleteConsumerAppInfoByIds(ids);
	}

	/**
	 * 删除应用信息信息
	 * 
	 * @param id 应用信息主键
	 * @return 结果
	 */
	@Override
	public int deleteConsumerAppInfoById(Long id) {
		return consumerAppInfoMapper.deleteConsumerAppInfoById(id);
	}
}
