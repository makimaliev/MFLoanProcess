package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericService;
import kg.gov.mf.loan.process.model.LoanDetailedSummary;

import java.util.Date;

public interface LoanDetailedSummaryService extends GenericService<LoanDetailedSummary> {

    LoanDetailedSummary getByOnDateAndLoanId(Date onDate, long loanId);
    LoanDetailedSummary getLastSummaryByLoanId(long loanId);

}
