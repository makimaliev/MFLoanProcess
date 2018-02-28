package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDao;
import kg.gov.mf.loan.process.model.LoanSummary;

import java.util.Date;

public interface LoanSummaryDao extends GenericDao<LoanSummary> {
    LoanSummary getByOnDateAndLoanId(Date onDate, long loanId);
}
