
package com.groqdata.quartz.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.groqdata.common.annotation.Log;
import com.groqdata.common.constant.Constants;
import com.groqdata.common.core.controller.BaseController;
import com.groqdata.common.core.domain.AjaxResult;
import com.groqdata.common.core.page.TableDataInfo;
import com.groqdata.common.enums.BusinessType;
import com.groqdata.common.exception.job.TaskException;
import org.apache.commons.lang3.StringUtils;
import com.groqdata.common.utils.poi.ExcelUtil;
import com.groqdata.quartz.domain.SysJob;
import com.groqdata.quartz.service.ISysJobService;
import com.groqdata.quartz.util.CronUtils;
import com.groqdata.quartz.util.ScheduleUtils;

/**
 * 调度任务信息操作处理
 *
 * @author MISP TEAM
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController {
	private static final String CN_ADD_TASK = "新增任务'";
	private static final String CN_EDIT_TASK = "修改任务'";
	private final ISysJobService jobService;

	public SysJobController(ISysJobService jobService) {
		this.jobService = jobService;
	}

	/**
	 * 查询定时任务列表
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:list')")
	@GetMapping("/list")
	public TableDataInfo<SysJob> list(SysJob sysJob) {
		startPage();
		List<SysJob> list = jobService.selectJobList(sysJob);
		return getDataTable(list);
	}

	/**
	 * 导出定时任务列表
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:export')")
	@Log(title = "定时任务", businessType = BusinessType.EXPORT)
	@PostMapping("/export")
	public void export(HttpServletResponse response, SysJob sysJob) {
		List<SysJob> list = jobService.selectJobList(sysJob);
		ExcelUtil<SysJob> util = new ExcelUtil<>(SysJob.class);
		util.exportExcel(response, list, "定时任务");
	}

	/**
	 * 获取定时任务详细信息
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:query')")
	@GetMapping(value = "/{jobId}")
	public AjaxResult getInfo(@PathVariable("jobId") Long jobId) {
		return success(jobService.selectJobById(jobId));
	}

	/**
	 * 新增定时任务
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:add')")
	@Log(title = "定时任务", businessType = BusinessType.INSERT)
	@PostMapping
	public AjaxResult add(@RequestBody SysJob job) throws SchedulerException, TaskException {
		// 验证任务配置
		AjaxResult validateResult = validateJobConfig(job, CN_ADD_TASK);
		if (validateResult.isError()) {
			return validateResult;
		}

		job.setCreateBy(getUsername());
		return toAjax(jobService.insertJob(job));
	}

	/**
	 * 修改定时任务
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:edit')")
	@Log(title = "定时任务", businessType = BusinessType.UPDATE)
	@PutMapping
	public AjaxResult edit(@RequestBody SysJob job) throws SchedulerException, TaskException {
		// 验证任务配置
		AjaxResult validateResult = validateJobConfig(job, CN_EDIT_TASK);
		if (validateResult.isError()) {
			return validateResult;
		}

		job.setUpdateBy(getUsername());
		return toAjax(jobService.updateJob(job));
	}

	/**
	 * 定时任务状态修改
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
	@Log(title = "定时任务", businessType = BusinessType.UPDATE)
	@PutMapping("/changeStatus")
	public AjaxResult changeStatus(@RequestBody SysJob job) throws SchedulerException {
		SysJob newJob = jobService.selectJobById(job.getJobId());
		newJob.setStatus(job.getStatus());
		return toAjax(jobService.changeStatus(newJob));
	}

	/**
	 * 定时任务立即执行一次
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
	@Log(title = "定时任务", businessType = BusinessType.UPDATE)
	@PutMapping("/run")
	public AjaxResult run(@RequestBody SysJob job) throws SchedulerException {
		boolean result = jobService.run(job);
		return result ? success() : error("任务不存在或已过期！");
	}

	/**
	 * 删除定时任务
	 */
	@PreAuthorize("@ss.hasPermi('monitor:job:remove')")
	@Log(title = "定时任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{jobIds}")
	public AjaxResult remove(@PathVariable Long[] jobIds) throws SchedulerException {
		jobService.deleteJobByIds(jobIds);
		return success();
	}

	/**
	 * 验证定时任务配置的公共方法
	 *
	 * @param job 定时任务对象
	 * @param operation 操作类型（新增或修改）
	 * @return 验证结果，如果验证通过返回null，否则返回错误信息
	 */
	private AjaxResult validateJobConfig(SysJob job, String operation) {
		if (!CronUtils.isValid(job.getCronExpression())) {
			return error(operation + job.getJobName() + "'失败，Cron表达式不正确");
		} else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) {
			return error(operation + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
		} else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(),
				Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS)) {
			return error(operation + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
		} else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(),
				Constants.HTTP, Constants.HTTPS)) {
			return error(operation + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
		} else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(),
				Constants.JOB_ERROR_STR.toArray(new String[0]))) {
			return error(operation + job.getJobName() + "'失败，目标字符串存在违规");
		} else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) {
			return error(operation + job.getJobName() + "'失败，目标字符串不在白名单内");
		}

		// 验证通过
		return AjaxResult.success();
	}
}