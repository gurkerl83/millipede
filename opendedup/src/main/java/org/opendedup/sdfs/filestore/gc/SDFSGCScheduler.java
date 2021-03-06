package org.opendedup.sdfs.filestore.gc;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opendedup.sdfs.Main;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class SDFSGCScheduler {

	private static Logger log = Logger.getLogger("sdfs");

	Scheduler sched = null;

	public SDFSGCScheduler() {
		try {
			Properties props = new Properties();
			props.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
			props.setProperty("org.quartz.threadPool.threadCount", "1");
			props.setProperty("org.quartz.threadPool.threadPriority", Integer.toString(Thread.MIN_PRIORITY));
			log.info("Scheduling Garbage Collection Jobs for SDFS");
			SchedulerFactory schedFact = new StdSchedulerFactory(props);
			sched = schedFact.getScheduler();
			sched.start();
			
			JobDetail ccjobDetail = new JobDetail("fdisk", null, FDISKJob.class);
			CronTrigger cctrigger = new CronTrigger("fdiskTrigger","group1", Main.fDkiskSchedule);
			sched.scheduleJob(ccjobDetail, cctrigger);
			log.info("Garbage Collection Jobs Scheduled");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Unable to schedule SDFS Garbage Collection", e);
		}
	}

	public void stopSchedules() {
		try {
			sched.unscheduleJob("fdisk", "fdiskTrigger");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

}
