package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericService;
import kg.gov.mf.loan.process.model.LoanSummary;

import java.util.Date;

public interface LoanSummaryService extends GenericService<LoanSummary> {
    LoanSummary getByOnDateAndLoanId(Date onDate, long loanId);
}
