package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.manage.model.loan.CreditTerm;
import kg.gov.mf.loan.manage.model.loan.Loan;
import kg.gov.mf.loan.manage.model.loan.Payment;
import kg.gov.mf.loan.manage.model.loan.PaymentSchedule;
import kg.gov.mf.loan.manage.service.loan.CreditTermService;
import kg.gov.mf.loan.manage.service.loan.LoanService;
import kg.gov.mf.loan.manage.service.loan.PaymentScheduleService;
import kg.gov.mf.loan.manage.service.loan.PaymentService;
import kg.gov.mf.loan.manage.util.DateUtils;
import kg.gov.mf.loan.process.model.Accrue;
import kg.gov.mf.loan.process.model.LoanDetailedSummary;
import kg.gov.mf.loan.process.service.AccrueService;
import kg.gov.mf.loan.process.service.LoanDetailedSummaryService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Transactional
@Component
public class CalculateLoanDetailedSummaryJob implements Job {

    @Autowired
    LoanService loanService;

    @Autowired
    PaymentScheduleService scheduleService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    CreditTermService termService;

    @Autowired
    LoanDetailedSummaryService loanDetailedSummaryService;

    @Autowired
    AccrueService accrueService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Date onDate = getOnDateFromJobCaller(context);
        if(onDate == null) throw new NullPointerException(); //Write Custom Exception
        fillLoanDetailSummaryForDate(onDate);
    }

    private void fillLoanDetailSummaryForDate(Date onDate)
    {
        //get loans
        List<Loan> loans = loanService.list();
        for (Loan loan: loans)
        {
            if(loanDetailedSummaryService.getByOnDateAndLoanId(onDate, loan.getId())!=null)
                continue;

            LoanDetailedSummary lastSummary = loanDetailedSummaryService.getLastSummaryByLoanId(loan.getId());
            Accrue accrue = null;

            CreditTerm term = termService.getRecentTermByLoanId(loan.getId());
            if(term == null) throw new NullPointerException(); //Write Custom Exception

            LoanDetailedSummary summary = new LoanDetailedSummary();
            summary.setLoan(loan);
            summary.setOnDate(onDate);

            //Principal
            Double totalPrincipalPayment = 0.0;
            Double principalPaid = 0.0;
            Double totalPrincipalPaid = 0.0;
            Double principalOutstanding = 0.0; //totalDisbursement - totalPrincipalPaid-totalWriteOffPaid
            Double principalWriteOff = 0.0;
            Double totalPrincipalWriteOff = 0.0;

            //Interest
            Double interestPayment = 0.0;
            Double totalDisbursement = 0.0;
            Double collectedInterestDisbursed = 0.0; //sum of all collected interest (ignore dates)
            Double interestPaid = 0.0;
            Double totalCollectedInterestPayment = 0.0;
            Double totalInterestPaid = 0.0;
            Double totalInterestPayment = 0.0;//totalInterestAccrued+totalCollectedInterest

            Double interestOutstanding = 0.0;
            Double interestAccrued = 0.0; //principalOutstanding,creditTerm,from-to date.
            Double interestOverdue = 0.0;
            Double totalInterestAccrued = 0.0; //sum of interest accrued
            Double totalInterestAccruedOnInterestPayment = 0.0;

            //Penalty
            Double totalCollectedPenaltyPayment = 0.0;
            Double collectedPenaltyDisbursed = 0.0; //sum of all collected penalty (ignore dates)
            Double penaltyAccrued = 0.0;
            Double penaltyOutstanding = 0.0;
            Double penaltyOverdue = 0.0;
            Double penaltyPaid = 0.0;
            Double totalPenaltyAccrued = 0.0;
            Double totalPenaltyPaid = 0.0;

            Double principalOverdue = 0.0;
            int daysInPeriod = 0;

            if(lastSummary != null){
                daysInPeriod = DateUtils.getDifferenceDays(onDate, lastSummary.getOnDate());
                accrue = new Accrue();
                accrue.setLoan(loan);
                accrue.setDaysInPeriod(daysInPeriod);
                accrue.setFromDate(DateUtils.subtract(lastSummary.getOnDate(), DateUtils.DAY,1));
                accrue.setToDate(DateUtils.subtract(onDate, DateUtils.DAY,1));
            }


            List<PaymentSchedule> row = scheduleService.getRowsUntilOnDate(onDate);
            if(row != null)
            {
                Set<PaymentSchedule> all = loan.getPaymentSchedules();
                for (PaymentSchedule schedule: all)
                {
                    collectedInterestDisbursed += schedule.getCollectedInterestPayment();
                    collectedPenaltyDisbursed += schedule.getCollectedPenaltyPayment();
                }

                for (int i=0; i < row.size(); i++)
                {
                    totalDisbursement += row.get(i).getDisbursement();
                    totalCollectedInterestPayment += row.get(i).getCollectedInterestPayment();
                    totalCollectedPenaltyPayment += row.get(i).getCollectedPenaltyPayment();
                    totalPrincipalPayment += row.get(i).getPrincipalPayment();
                }

                summary.setPrincipalPayment(row.get(row.size()-1).getPrincipalPayment());
                summary.setCollectedInterestPayment(row.get(row.size()-1).getCollectedInterestPayment());
                summary.setCollectedPenaltyPayment(row.get(row.size()-1).getCollectedPenaltyPayment());
                summary.setDisbursement(row.get(row.size()-1).getDisbursement());

                List<Payment> payments = paymentService.getRowsUntilOnDate(onDate);
                for (Payment payment: payments)
                {
                    totalPrincipalPaid += payment.getPrincipal();
                    totalInterestPaid += payment.getInterest();
                    totalPenaltyPaid += payment.getPenalty();
                }

                Payment paymentDayBeforeOnDate = paymentService.getRowDayBeforeOnDate(onDate);
                if(paymentDayBeforeOnDate != null)
                {
                    principalPaid = paymentDayBeforeOnDate.getPrincipal();
                    interestPaid = paymentDayBeforeOnDate.getInterest();
                    penaltyPaid = paymentDayBeforeOnDate.getPenalty();
                }

                summary.setDaysInPeriod(daysInPeriod);
                summary.setTotalDisbursement(totalDisbursement);
                summary.setTotalCollectedInterestPayment(totalCollectedInterestPayment);
                summary.setTotalCollectedPenaltyPayment(totalCollectedPenaltyPayment);
                summary.setCollectedInterestDisbursed(collectedInterestDisbursed);
                summary.setCollectedPenaltyDisbursed(collectedPenaltyDisbursed);
                summary.setTotalPrincipalPaid(totalPrincipalPaid);
                summary.setTotalPrincipalPayment(totalPrincipalPayment);
                summary.setPrincipalPaid(principalPaid);
                summary.setInterestPaid(interestPaid);
                summary.setPenaltyPaid(penaltyPaid);
                summary.setTotalInterestPaid(totalInterestPaid);
                summary.setTotalPenaltyPaid(totalPenaltyPaid);
                principalOutstanding = totalDisbursement - totalPrincipalPaid;
                principalOverdue = totalPrincipalPayment - totalPrincipalPaid - totalPrincipalWriteOff;
                summary.setPrincipalOverdue(principalOverdue);
                summary.setPrincipalOutstanding(principalOutstanding);
                if(lastSummary != null)
                {
                    interestAccrued = calculateInterestAccrued(lastSummary.getPrincipalOutstanding(), term, daysInPeriod);
                    totalInterestAccrued += interestAccrued + lastSummary.getTotalInterestAccrued();
                    totalInterestAccruedOnInterestPayment = lastSummary.getTotalInterestAccruedOnInterestPayment();

                    if(row.get(row.size()-1).getExpectedDate().compareTo(DateUtils.subtract(onDate, DateUtils.DAY,1)) == 0)
                    {
                        interestPayment = totalInterestAccrued - lastSummary.getTotalInterestAccruedOnInterestPayment();
                        totalInterestAccruedOnInterestPayment = totalInterestAccrued;
                    }

                    totalInterestPayment += interestPayment + lastSummary.getTotalInterestPayment();

                    penaltyAccrued = calculatePenaltyAccrued(lastSummary.getPrincipalOverdue(), lastSummary.getInterestOverdue(), term, daysInPeriod);
                    totalPenaltyAccrued += penaltyAccrued + lastSummary.getTotalPenaltyAccrued();
                }

                else
                {
                    interestAccrued = calculateInterestAccrued(principalOutstanding, term, daysInPeriod);
                    totalInterestAccrued = interestAccrued;
                    totalInterestAccruedOnInterestPayment = interestPayment;

                    penaltyAccrued = calculatePenaltyAccrued(0.0, 0.0,term, daysInPeriod);
                    totalPenaltyAccrued = penaltyAccrued;
                }

                summary.setInterestAccrued(interestAccrued);
                summary.setTotalInterestAccrued(totalInterestAccrued);
                summary.setTotalInterestAccruedOnInterestPayment(totalInterestAccruedOnInterestPayment);
                summary.setInterestPayment(interestPayment);
                summary.setTotalInterestPayment(totalInterestPayment);
                interestOutstanding = totalInterestAccrued + collectedInterestDisbursed - totalInterestPaid;
                summary.setInterestOutstanding(interestOutstanding);
                interestOverdue = totalInterestPayment - totalInterestPaid;
                summary.setInterestOverdue(interestOverdue);
                summary.setPenaltyAccrued(penaltyAccrued);
                summary.setTotalPenaltyAccrued(totalPenaltyAccrued);
                penaltyOverdue = totalPenaltyAccrued + totalCollectedPenaltyPayment - penaltyPaid;
                summary.setPenaltyOverdue(penaltyOverdue);
                penaltyOutstanding = totalPenaltyAccrued + collectedPenaltyDisbursed - penaltyPaid;
                summary.setPenaltyOutstanding(penaltyOutstanding);
                //---------------------------------------------------
                summary.setPrincipalWriteOff(principalWriteOff);
                summary.setTotalPrincipalWriteOff(totalPrincipalWriteOff);
                if(accrue!=null)
                {
                    accrue.setInterestAccrued(interestAccrued);
                    accrue.setPenaltyAccrued(penaltyAccrued);
                    accrue.setPenaltyOnPrincipalOverdue(principalOverdue*term.getPenaltyOnPrincipleOverdueRateValue()/100*daysInPeriod/360);
                    accrue.setPenaltyOnInterestOverdue(interestOverdue*term.getPenaltyOnInterestOverdueRateValue()/100*daysInPeriod/360);
                    accrue.setLastInstPassed(false);
                    accrueService.add(accrue);
                }

            }
            loanDetailedSummaryService.add(summary);
        }
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

    private Double calculateInterestAccrued(Double principalOutstanding, CreditTerm term, int daysInPeriod)
    {
        Double interestRateValue = term.getInterestRateValue();
        return (principalOutstanding*interestRateValue/360)/100*daysInPeriod;
    }

    private Double calculatePenaltyAccrued(Double principalOverdue, Double interestOverdue, CreditTerm term, int daysInPeriod)
    {
        Double penaltyOnPrincipalOverdueRate = term.getPenaltyOnPrincipleOverdueRateValue();
        Double penaltyOnInterestOverdueRate = term.getPenaltyOnInterestOverdueRateValue();
        return (principalOverdue*penaltyOnPrincipalOverdueRate/100*daysInPeriod/360) + (interestOverdue*penaltyOnInterestOverdueRate/100*daysInPeriod/360);
    }
}
