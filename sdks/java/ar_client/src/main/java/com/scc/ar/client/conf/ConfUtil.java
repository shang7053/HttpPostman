package com.scc.ar.client.conf;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: SchedulerUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author shangchengcai@voole.com
 * @date 2018年1月12日 下午5:39:24
 * 
 */
public class ConfUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfUtil.class);
	private static final String TRIGGER_NAME = "AR_CLIENT_TRIGGER";
	private static final String TRIGGER_GROUP = "AR_CLIENT_TRIGGER_GROUP";
	private static final String JOB_NAME = "AR_CLIENT_JOB";
	private static final String JOB_GROUP = "AR_CLIENT_JOB_GROUP";
	private static Scheduler scheduler = null;
	private static ConfUtil schedulerUtil;

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author shangchengcai@voole.com
	 * @date 2018年1月12日 下午6:07:52
	 */
	private ConfUtil() {
	}

	static {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
			System.exit(1);
		}
	}

	public static synchronized ConfUtil build() {
		if (null == schedulerUtil) {
			schedulerUtil = new ConfUtil();
		}
		return schedulerUtil;
	}

	public <T extends Job> ConfUtil addJob(Class<T> jobclass, int interval) {
		try {
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(TRIGGER_NAME, TRIGGER_GROUP).startNow()
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).repeatForever())
					.build();
			JobDetail job = JobBuilder.newJob(jobclass).withIdentity(JOB_NAME, JOB_GROUP).build();
			scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
		return this;
	}

	public void start() {
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void close() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
	}

}
