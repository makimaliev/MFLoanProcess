package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.manage.model.order.CreditOrderState;
import kg.gov.mf.loan.manage.service.order.CreditOrderStateService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Transactional
@Component
public class JobCreateOrderState implements Job{

    @Autowired
    private CreditOrderStateService stateService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        CreditOrderState state = new CreditOrderState();
        state.setVersion(1);
        state.setName("Dummy State from job " + dateFormat.format(date));

        System.out.println("Insertion of order state at:" + dateFormat.format(date));

        stateService.add(state);
    }
}
