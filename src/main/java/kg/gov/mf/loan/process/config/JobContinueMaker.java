package kg.gov.mf.loan.process.config;

import kg.gov.mf.loan.process.model.JobItem;
import kg.gov.mf.loan.process.service.JobItemService;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class JobContinueMaker {

    @Autowired
    JobItemService jobItemService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void init(){
        try{
            List<JobItem> jobItems = jobItemService.list();

            for (JobItem jobItem: jobItems)
            {
                if(jobItem.getActive()==1)
                {
                    Class<?> jobClass= null;
                    try {
                        jobClass = Class.forName(jobItem.getName());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    JobDetail jobDetail = context.getBean(JobDetail.class, jobItem.getName(), jobItem.getName(),jobClass);
                    Trigger cronTrigger = context.getBean(Trigger.class, jobItem.getCronExpression(), jobItem.getName());
                    try {
                        scheduler.getContext().put("jobItem", jobItem);
                        scheduler.scheduleJob(jobDetail, cronTrigger);
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e){

        }
    }
}
