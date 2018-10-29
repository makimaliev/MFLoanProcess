package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.manage.model.classification.Classification;
import kg.gov.mf.loan.manage.model.classification.ClassificationResult;
import kg.gov.mf.loan.manage.repository.classification.ClassificationRepository;
import kg.gov.mf.loan.manage.repository.classification.ClassificationResultRepository;
import kg.gov.mf.loan.manage.util.DateUtils;
import kg.gov.mf.loan.process.model.JobItem;
import kg.gov.mf.loan.process.service.JobItemService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Transactional
@Component
public class ClassificationJob implements Job {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    ClassificationRepository classificationRepository;

    @Autowired
    ClassificationResultRepository crRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobItem job = getJobItemFromJobCaller(context);
        if(job == null) throw new NullPointerException(); //Write Custom Exception
        classifyForToday();
    }

    private void classifyForToday()
    {
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();
        today = DateUtils.subtract(today, DateUtils.DAY,1);

        List<Classification> classificationList = classificationRepository.findAll();

        for (Classification cl: classificationList)
        {
            Query qTemp = entityManager.createNativeQuery(cl.getQuery());

            List<BigInteger> fields = qTemp.getResultList();

            for (BigInteger field: fields)
            {
                Long cId = cl.getId();
                Long entityId = Long.parseLong(field + "");
                ClassificationResult prev = crRepository.findByEntityIdAndAndClassificationId(entityId, cId);
                if(prev == null)
                {
                    ClassificationResult res = new ClassificationResult();
                    res.setClassificationId(cId);
                    res.setEntityId(entityId);
                    res.setDate(today);
                    crRepository.save(res);
                }
            }
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
