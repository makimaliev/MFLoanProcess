package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.manage.model.loan.Loan;
import kg.gov.mf.loan.manage.model.loan.PaymentSchedule;
import kg.gov.mf.loan.manage.service.loan.LoanService;
import kg.gov.mf.loan.process.model.LoanDetailedSummary;
import kg.gov.mf.loan.process.service.LoanDetailedSummaryService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Transactional
@Component
public class CalculateLoanDetailedSummaryJob implements Job {

    @Autowired
    LoanService loanService;

    @Autowired
    LoanDetailedSummaryService loanDetailedSummaryService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Date onDate = new Date(0);

        try {
            SchedulerContext schedulerContext = context.getScheduler().getContext();
            onDate = (Date) schedulerContext.get("onDate");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        //get loans
        List<Loan> loans = loanService.list();
        for (Loan loan: loans
             ) {

            if(loanDetailedSummaryService.getByOnDateAndLoanId(onDate, loan.getId())!=null)
                continue;

            LoanDetailedSummary summary = new LoanDetailedSummary();
            summary.setLoan(loan);
            summary.setOnDate(onDate);

            //TODO: Method for returning last row
            /*
            summary.setDaysInPeriod(getDifferenceDays(onDate, schedule.getExpectedDate()));
            summary.setPrincipalPayment(0.0);
            summary.setInterestPayment(0.0);
            */
            //TODO: Method for returning all rows until some date

            //get payment schedule rows
            Set<PaymentSchedule> paymentScheduleSet = loan.getPaymentSchedules();
            for (PaymentSchedule schedule: paymentScheduleSet) {

                //calculate for earlier dates
                if(schedule.getExpectedDate().before(onDate))
                {

                }
            }

            /*
            summary.setCollectedInterestPayment(schedule.getCollectedInterestPayment());
            summary.setCollectedInterestDisbursed(0.0); //sum of all collected interest (ignore dates)
            summary.setCollectedPenaltyPayment(schedule.getCollectedPenaltyPayment());
            summary.setCollectedPenaltyDisbursed(0.0); //sum of all collected penalty (ignore dates)
            summary.setInterestAccrued(0.0);
            summary.setInterestOutstanding(0.0);
            summary.setInterestOverdue(0.0);
            summary.setInterestPaid(0.0);
            summary.setPenaltyAccrued(0.0);
            summary.setPenaltyOutstanding(0.0);
            summary.setPenaltyOverdue(0.0);
            summary.setPenaltyPaid(0.0);
            summary.setTotalCollectedInterestPayment(0.0);
            summary.setTotalCollectedPenaltyPayment(0.0);
            summary.setTotalInterestAccrued(0.0);
            summary.setTotalInterestPaid(0.0);
            summary.setTotalInterestPayment(0.0);
            summary.setTotalPenaltyAccrued(0.0);
            summary.setTotalPenaltyPaid(0.0);
            summary.setDisbursement(schedule.getDisbursement());
            summary.setTotalDisbursement(schedule.getDisbursement());
            summary.setTotalPrincipalPayment(0.0);
            summary.setPrincipalWriteOff(0.0);
            summary.setTotalPrincipalWriteOff(0.0);
            summary.setPrincipalPaid(0.0);
            summary.setTotalPrincipalPaid(0.0);
            summary.setPrincipalOutstanding(0.0);
            summary.setPrincipalOverdue(0.0);
            loanDetailedSummaryService.add(summary);
            */
        }
    }

    public static int getDifferenceDays(Date d1, Date d2) {
        long diff = Math.abs(d2.getTime() - d1.getTime());
        return (int)TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
