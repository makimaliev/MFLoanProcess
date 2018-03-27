package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.manage.model.loan.CreditTerm;
import kg.gov.mf.loan.manage.model.loan.Loan;
import kg.gov.mf.loan.manage.service.loan.CreditTermService;
import kg.gov.mf.loan.manage.service.loan.LoanService;
import kg.gov.mf.loan.manage.util.DateUtils;
import kg.gov.mf.loan.process.model.JobItem;
import kg.gov.mf.loan.manage.model.process.LoanDetailedSummary;
import kg.gov.mf.loan.manage.model.process.LoanSummary;
import kg.gov.mf.loan.process.model.OnDate;
import kg.gov.mf.loan.manage.service.process.LoanDetailedSummaryService;
import kg.gov.mf.loan.manage.service.process.LoanSummaryService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Transactional
@Component
public class CalculateLoanSummaryJob implements Job {

    @Autowired
    LoanService loanService;

    @Autowired
    CreditTermService termService;

    @Autowired
    LoanDetailedSummaryService loanDetailedSummaryService;

    @Autowired
    LoanSummaryService loanSummaryService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobItem job = getJobItemFromJobCaller(context);
        if(job == null) throw new NullPointerException(); //Write Custom Exception
        Set<OnDate> onDates = job.getOnDates();
        if(onDates == null) throw new NullPointerException(); //Write Custom Exception
        for (OnDate date: onDates)
            fillLoanSummaryForDate(date.getOnDate());
    }

    private void fillLoanSummaryForDate(Date onDate)
    {
        //get loans
        List<Loan> loans = loanService.list();
        for (Loan loan: loans)
        {
            if(loanSummaryService.getByOnDateAndLoanId(onDate, loan.getId()) != null)
                continue;

            CreditTerm term = termService.getRecentTermByLoanIdAndOnDate(loan.getId(), onDate);
            if(term == null) throw new NullPointerException(); //Write Custom Exception

            LoanDetailedSummary lastDetailedSummary = loanDetailedSummaryService.getLastSummaryByLoanIdAndLTEOnDate(loan.getId(), onDate);
            if(lastDetailedSummary != null)
            {
                LoanSummary summary = new LoanSummary();
                summary.setOnDate(onDate);
                summary.setLoan(loan);
                summary.setLoanAmount(loan.getAmount());
                summary.setOutstadingPrincipal(lastDetailedSummary.getPrincipalOutstanding());
                int daysInPeriod = DateUtils.getDifferenceDays(DateUtils.subtract(onDate, DateUtils.DAY, 1), DateUtils.subtract(lastDetailedSummary.getOnDate(), DateUtils.DAY,1));
                summary.setOutstadingInterest(lastDetailedSummary.getInterestOutstanding() + calculateOutstandingInterest(lastDetailedSummary.getPrincipalOutstanding(), term, daysInPeriod));
                summary.setOutstadingPenalty(lastDetailedSummary.getPenaltyOutstanding() + calculateOutstandingPenalty(lastDetailedSummary.getPrincipalOverdue(), lastDetailedSummary.getInterestOverdue(), term, daysInPeriod));
                summary.setOutstadingFee(0.0);
                summary.setOverduePrincipal(lastDetailedSummary.getPrincipalOverdue());
                summary.setOverdueInterest(lastDetailedSummary.getInterestOverdue());
                summary.setOverduePenalty(lastDetailedSummary.getPenaltyOverdue() + calculateOutstandingPenalty(lastDetailedSummary.getPrincipalOverdue(), lastDetailedSummary.getInterestOverdue(), term, daysInPeriod));
                summary.setOverdueFee(0.0);
                summary.setPaidPrincipal(lastDetailedSummary.getTotalPrincipalPaid());
                summary.setPaidInterest(lastDetailedSummary.getTotalInterestPaid());
                summary.setPaidPenalty(lastDetailedSummary.getTotalPenaltyPaid());
                summary.setPaidFee(0.0);
                summary.setTotalDisbursed(lastDetailedSummary.getTotalDisbursement());
                summary.setTotalOutstanding(summary.getOutstadingPrincipal() + summary.getOutstadingInterest() + summary.getOutstadingPenalty());
                summary.setTotalOverdue(summary.getOverduePrincipal() + summary.getOverdueInterest() + summary.getOverduePenalty());
                summary.setTotalPaid(summary.getPaidPrincipal() + summary.getPaidInterest() + summary.getPaidPenalty());
                loanSummaryService.add(summary);
            }
        }
    }

    private Double calculateOutstandingInterest(Double principalOutstanding, CreditTerm term, int daysInPeriod)
    {
        Double interestRateValue = term.getInterestRateValue();
        int numberOfDays = (term.getDaysInYearMethod().getId() == 1? 365:360);
        return (principalOutstanding*interestRateValue/numberOfDays)/100*daysInPeriod;
    }

    private Double calculateOutstandingPenalty(Double principalOverdue, Double interestOverdue, CreditTerm term, int daysInPeriod)
    {
        Double penaltyOnPrincipalOverdueRate = term.getPenaltyOnPrincipleOverdueRateValue();
        Double penaltyOnInterestOverdueRate = term.getPenaltyOnInterestOverdueRateValue();
        int numberOfDays = (term.getDaysInYearMethod().getId() == 1? 365:360);
        return (principalOverdue*penaltyOnPrincipalOverdueRate/100*daysInPeriod/numberOfDays) + (interestOverdue*penaltyOnInterestOverdueRate/100*daysInPeriod/numberOfDays);
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

    private Date getOnDateFromJobCaller(JobExecutionContext context)
    {
        Date onDate = null;

        try
        {
            SchedulerContext schedulerContext = context.getScheduler().getContext();
            onDate = (Date) schedulerContext.get("onDate");

        }
        catch(SchedulerException ex)
        {
            ex.printStackTrace();
        }

        return onDate;
    }
}
