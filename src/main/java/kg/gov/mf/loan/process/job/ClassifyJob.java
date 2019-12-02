package kg.gov.mf.loan.process.job;

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
public class ClassifyJob implements Job {

    @Autowired
    JobItemService jobItemService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobItem job = getJobItemFromJobCaller(context);
        if(job == null) throw new NullPointerException(); //Write Custom Exception
        classify();
    }

    private void classify()
    {
        this.jobItemService.updateDebtorGroupAndSubGroup();
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
