package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDao;
import kg.gov.mf.loan.process.model.LoanDetailedSummary;

import java.util.Date;

public interface LoanDetailedSummaryDao extends GenericDao<LoanDetailedSummary> {

    LoanDetailedSummary getByOnDateAndLoanId(Date onDate, long loanId);

}
