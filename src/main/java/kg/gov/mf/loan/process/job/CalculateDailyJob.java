package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.manage.util.DateUtils;
import kg.gov.mf.loan.process.model.JobItem;
import kg.gov.mf.loan.process.service.JobItemService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Transactional
@Component
public class CalculateDailyJob implements Job {

    @Autowired
    JobItemService jobItemService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobItem job = getJobItemFromJobCaller(context);
        if(job == null) throw new NullPointerException(); //Write Custom Exception
        calculateForToday();
    }

    private void calculateForToday()
    {
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();
//        today = DateUtils.subtract(today, DateUtils.DAY,1);

        this.jobItemService.runDailyCalculateProcedure(today);
    }

    private JobItem getJobItemFromJobCaller(JobExecutionContext context)
    {
        JobItem job = null;

        try
        {
            SchedulerContext schedulerContext = context.getScheduler().getContext();
            job = (JobItem) schedulerContext.get("jobItem");

        }
        catch(SchedulerException ex)
        {
            ex.printStackTrace();
        }

        return job;
    }

}
