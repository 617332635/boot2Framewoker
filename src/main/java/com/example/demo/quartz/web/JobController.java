package com.example.demo.quartz.web;

import com.example.demo.quartz.entity.QuartzEntity;
import com.example.demo.quartz.entity.Result;
import com.example.demo.quartz.service.IJobService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/job")
public class JobController {
	private final static Logger LOGGER = LoggerFactory.getLogger(JobController.class);
	

	@Autowired
    private Scheduler scheduler;
    @Autowired
    private IJobService jobService;
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/add")
	public Result save(QuartzEntity quartz){
		LOGGER.info("新增任务");
		try {
	        //如果是修改  展示旧的 任务
	        if(quartz.getOldJobGroup()!=null){
	        	//根据任务名称、任务分组去构建jobkey，使用scheduler删除job
	        	JobKey key = new JobKey(quartz.getOldJobName(),quartz.getOldJobGroup());
	        	scheduler.deleteJob(key);
	        }
	        Class cls = Class.forName(quartz.getJobClassName()) ;
	        cls.newInstance();
	        //构建job信息，使用JobBuilder创建job实例，withIdentity创建对应任务的key
	        JobDetail job = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(),
	        		quartz.getJobGroup())
					//以及任务的描述
	        		.withDescription(quartz.getDescription()).build();
	        // 触发时间点
	        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
	        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger"+quartz.getJobName(), quartz.getJobGroup())
	                .startNow().withSchedule(cronScheduleBuilder).build();	
	        //交由Scheduler安排触发
	        scheduler.scheduleJob(job, trigger);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
		}
		return Result.ok();
	}
	@PostMapping("/list")
	public Result list(QuartzEntity quartz,Integer pageNo,Integer pageSize){
		LOGGER.info("任务列表");
		List<QuartzEntity> list = jobService.listQuartzEntity(quartz, pageNo, pageSize);
		return Result.ok(list);
	}
	@PostMapping("/trigger")
	public  Result trigger(QuartzEntity quartz,HttpServletResponse response) {
		LOGGER.info("触发任务");
		try {
		     JobKey key = new JobKey(quartz.getJobName(),quartz.getJobGroup());
		     scheduler.triggerJob(key);//根据key直接执行任务
		} catch (SchedulerException e) {
			 e.printStackTrace();
			 return Result.error();
		}
		return Result.ok();
	}
	@PostMapping("/pause")
	public  Result pause(QuartzEntity quartz,HttpServletResponse response) {
		LOGGER.info("停止任务");
		try {
		     JobKey key = new JobKey(quartz.getJobName(),quartz.getJobGroup());
		     scheduler.pauseJob(key);
		} catch (SchedulerException e) {
			 e.printStackTrace();
			 return Result.error();
		}
		return Result.ok();
	}
	@PostMapping("/resume")
	public  Result resume(QuartzEntity quartz,HttpServletResponse response) {
		LOGGER.info("恢复任务");
		try {
		     JobKey key = new JobKey(quartz.getJobName(),quartz.getJobGroup());
		     scheduler.resumeJob(key);
		} catch (SchedulerException e) {
			 e.printStackTrace();
			 return Result.error();
		}
		return Result.ok();
	}
	@PostMapping("/remove")
	public  Result remove(QuartzEntity quartz,HttpServletResponse response) {
		LOGGER.info("移除任务");
		try {  
            TriggerKey triggerKey = TriggerKey.triggerKey(quartz.getJobName(), quartz.getJobGroup());  
            // 停止触发器  
            scheduler.pauseTrigger(triggerKey);  
            // 移除触发器  
            scheduler.unscheduleJob(triggerKey);  
            // 删除任务  
            scheduler.deleteJob(JobKey.jobKey(quartz.getJobName(), quartz.getJobGroup()));  
            System.out.println("removeJob:"+JobKey.jobKey(quartz.getJobName()));  
        } catch (Exception e) {  
        	e.printStackTrace();
            return Result.error();
        }  
		return Result.ok();
	}
}
