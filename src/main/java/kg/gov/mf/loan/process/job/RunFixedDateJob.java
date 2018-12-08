package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.process.model.FixedDate;
import kg.gov.mf.loan.process.model.JobItem;
import kg.gov.mf.loan.process.repository.FixedDateRepository;
import kg.gov.mf.loan.process.service.JobItemService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Component
public class RunFixedDateJob implements Job {

    @Autowired
    JobItemService jobItemService;

    @Autowired
    FixedDateRepository fixedDateRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobItem job = getJobItemFromJobCaller(context);
        if(job == null) throw new NullPointerException(); //Write Custom Exception
        calculateForToday();
    }

    private void calculateForToday()
    {
        List<FixedDate> fixedDateList = fixedDateRepository.getFixedDateByStatusEquals(1);
        for (FixedDate date: fixedDateList)
        {
            this.jobItemService.runFixedCalculateProcedure(date.getDate());
            date.setStatus(2);
            fixedDateRepository.save(date);
        }
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
