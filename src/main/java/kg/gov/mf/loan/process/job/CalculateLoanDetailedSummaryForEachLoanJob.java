package kg.gov.mf.loan.process.job;

import kg.gov.mf.loan.admin.sys.model.User;
import kg.gov.mf.loan.admin.sys.service.UserService;
import kg.gov.mf.loan.manage.model.loan.CreditTerm;
import kg.gov.mf.loan.manage.model.loan.Loan;
import kg.gov.mf.loan.manage.model.loan.Payment;
import kg.gov.mf.loan.manage.model.loan.PaymentSchedule;
import kg.gov.mf.loan.manage.model.process.Accrue;
import kg.gov.mf.loan.manage.model.process.LoanDetailedSummary;
import kg.gov.mf.loan.manage.service.loan.CreditTermService;
import kg.gov.mf.loan.manage.service.loan.LoanService;
import kg.gov.mf.loan.manage.service.loan.PaymentScheduleService;
import kg.gov.mf.loan.manage.service.loan.PaymentService;
import kg.gov.mf.loan.manage.service.process.AccrueService;
import kg.gov.mf.loan.manage.service.process.LoanDetailedSummaryService;
import kg.gov.mf.loan.manage.util.DateUtils;
import kg.gov.mf.loan.process.model.JobItem;
import kg.gov.mf.loan.process.model.JobsLog;
import kg.gov.mf.loan.process.service.JobsLogService;
import kg.gov.mf.loan.task.model.Task;
import kg.gov.mf.loan.task.model.TaskPriority;
import kg.gov.mf.loan.task.model.TaskStatus;
import kg.gov.mf.loan.task.service.TaskService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Calendar;

@Transactional
@Component
public class CalculateLoanDetailedSummaryForEachLoanJob implements Job {

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

    @Autowired
    TaskService taskService;

    @Autowired
    UserService userService;

    @Autowired
    JobsLogService jobsLogService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobItem job = getJobItemFromJobCaller(context);
        if(job == null) throw new NullPointerException(); //Write Custom Exception
        runForAllLoans();
    }

    private void runForAllLoans()
    {
        int count = 0;
        //get loans
        List<Loan> loans = loanService.list();
        for (Loan temp: loans)
        {
            if(count >= 100)
                break;

            Loan loan = loanService.getById(temp.getId());
            Date regDate = loan.getRegDate();

            List<Date> criticalDates = getCriticalDates(loan, regDate);
            for(Date date: criticalDates)
                fillLoanDetailSummaryForDate(loan, date);

            count++;
        }
    }

    private void fillLoanDetailSummaryForDate(Loan loan, Date onDate)
    {
        try{

            if(loanDetailedSummaryService.getByOnDateAndLoanId(onDate, loan.getId())!=null)
                return;

            LoanDetailedSummary lastSummary = loanDetailedSummaryService.getLastSummaryByLoanIdAndLTEOnDate(loan.getId(), onDate);
            Accrue accrue = null;

            CreditTerm term = termService.getRecentTermByLoanIdAndOnDate(loan.getId(), onDate);
            if(term == null) return;

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

            List<PaymentSchedule> row = scheduleService.getRowsUntilOnDateByLoanId(loan.getId(), onDate);
            if(row != null && row.size() >= 1)
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

                List<Payment> payments = paymentService.getRowsUntilOnDateByLoanId(loan.getId(), onDate);
                for (Payment payment: payments)
                {
                    totalPrincipalPaid += payment.getPrincipal();
                    totalInterestPaid += payment.getInterest();
                    totalPenaltyPaid += payment.getPenalty();
                }

                List<Payment> paymentsDayBeforeOnDate = paymentService.getRowDayBeforeOnDateByLoanId(loan.getId(), onDate);
                if(paymentsDayBeforeOnDate != null)
                {
                    for (Payment payment: paymentsDayBeforeOnDate
                            ) {
                        principalPaid += payment.getPrincipal();
                        interestPaid += payment.getInterest();
                        penaltyPaid += payment.getPenalty();
                    }
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
                    int numberOfDays = (term.getDaysInYearMethod().getId() == 1? 365:360);
                    accrue.setPenaltyOnPrincipalOverdue(principalOverdue*term.getPenaltyOnPrincipleOverdueRateValue()/100*daysInPeriod/numberOfDays);
                    accrue.setPenaltyOnInterestOverdue(interestOverdue*term.getPenaltyOnInterestOverdueRateValue()/100*daysInPeriod/numberOfDays);
                    accrue.setLastInstPassed(false);
                    accrueService.add(accrue);
                }

            }
            loanDetailedSummaryService.add(summary);
            //createTaskIfOverdue(summary, onDate);

        }
        catch(NullPointerException ex)
        {
            Calendar now = Calendar.getInstance();
            Date today = now.getTime();
            JobsLog log = new JobsLog();
            log.setOnDate(today);
            log.setJobName(this.getClass().getSimpleName());
            log.setModel("Loan");
            log.setEntityId(loan.getId());
            log.setClassName(ex.getStackTrace()[0].getClassName() + ":" + ex.getStackTrace()[0].getMethodName() + ":" + ex.getStackTrace()[0].getLineNumber());
            log.setMessage(ex.getMessage());
            jobsLogService.add(log);
        }
    }

    private List<Date> getCriticalDates(Loan loan, Date regDate)
    {
        Set<Date> result = new HashSet<>();
        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTime(regDate);

        //get start of each month
        Calendar curr = start;
        while (curr.compareTo(now) < 0)
        {
            curr.add(Calendar.MONTH, 1);
            curr.set(Calendar.DAY_OF_MONTH,2);
            Date onDate = curr.getTime();
            result.add(onDate);
        }

        Set<Payment> payments = loan.getPayments();
        for (Payment payment: payments
             ) {
            result.add(DateUtils.add(payment.getPaymentDate(), DateUtils.DAY, 1));
        }

        Set<PaymentSchedule> schedules = loan.getPaymentSchedules();
        for(PaymentSchedule schedule : schedules)
        {
            result.add(DateUtils.add(schedule.getExpectedDate(), DateUtils.DAY, 1));
        }

        Set<CreditTerm> terms = loan.getCreditTerms();
        for(CreditTerm term:terms)
        {
            result.add(DateUtils.add(term.getStartDate(), DateUtils.DAY, 1));
        }

        List<Date> dates = new ArrayList<>();
        for(Date date: result)
            dates.add(date);

        Collections.sort(dates);

        return dates;
    }

    private void createTaskIfOverdue(LoanDetailedSummary summary, Date onDate){

        if(summary.getPrincipalOverdue() + summary.getInterestOverdue() > 0.0)
        {
            Loan loan = summary.getLoan();
            if(taskService.getTaskByObjectTypeAndObjectId(Loan.class.getName(), loan.getId()) == null)
            {
                User user = userService.findByUsername("admin");
                Task task = new Task();
                task.setSummary("Loan Overdue Task from Job");
                task.setDescription("Loan Overdue");
                task.setIdentifiedByUserId(user.getId());
                task.setIdentifiedDate(onDate);
                task.setAssignedToUserId(loan.getSupervisorId());
                task.setStatus(TaskStatus.OPEN);
                task.setPriority(TaskPriority.HIGH);
                task.setTargetResolutionDate(DateUtils.add(onDate, DateUtils.DAY,7));
                task.setCreatedOn(onDate);
                task.setCreatedBy(user);
                task.setModifiedOn(onDate);
                task.setModifiedByUserId(user.getId());
                task.setObjectType(Loan.class.getName());
                task.setObjectId(loan.getId());
                taskService.add(task);
            }
        }

    }

    private void removePreviousDate(Loan loan, Date startDate)
    {
        LoanDetailedSummary prevSummary = loanDetailedSummaryService.getByOnDateAndLoanId(startDate, loan.getId());
        if(prevSummary != null)
        {
            List<Payment> paymentDayBeforeOnDate = paymentService.getRowDayBeforeOnDateByLoanId(loan.getId(), startDate);
            if(paymentDayBeforeOnDate == null)
            {
                loanDetailedSummaryService.remove(prevSummary);

                Accrue accrue = accrueService.getByOnDateAndLoanId(DateUtils.subtract(startDate, DateUtils.DAY,1), loan.getId());
                if(accrue != null)
                {
                    accrueService.remove(accrue);
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

    private Double calculateInterestAccrued(Double principalOutstanding, CreditTerm term, int daysInPeriod)
    {
        Double interestRateValue = term.getInterestRateValue();
        int numberOfDays = (term.getDaysInYearMethod().getId() == 1? 365:360);
        return (principalOutstanding*interestRateValue/numberOfDays)/100*daysInPeriod;
    }

    private Double calculatePenaltyAccrued(Double principalOverdue, Double interestOverdue, CreditTerm term, int daysInPeriod)
    {
        Double penaltyOnPrincipalOverdueRate = term.getPenaltyOnPrincipleOverdueRateValue();
        Double penaltyOnInterestOverdueRate = term.getPenaltyOnInterestOverdueRateValue();
        int numberOfDays = (term.getDaysInYearMethod().getId() == 1? 365:360);
        return (principalOverdue*penaltyOnPrincipalOverdueRate/100*daysInPeriod/numberOfDays) + (interestOverdue*penaltyOnInterestOverdueRate/100*daysInPeriod/numberOfDays);
    }
}
