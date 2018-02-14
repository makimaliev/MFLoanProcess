package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDaoImpl;
import kg.gov.mf.loan.process.model.LoanDetailedSummary;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository("loanDetailedSummaryDao")
public class LoanDetailedSummaryDaoImpl extends GenericDaoImpl<LoanDetailedSummary> implements LoanDetailedSummaryDao {

    @Override
    public LoanDetailedSummary getByOnDateAndLoanId(Date onDate, long loanId){
        return (LoanDetailedSummary) getCurrentSession().createQuery("from LoanDetailedSummary where onDate = '" + onDate + "' and loanId = '" + loanId + "'").uniqueResult();
    }

}
