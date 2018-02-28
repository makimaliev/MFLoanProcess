package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDaoImpl;
import kg.gov.mf.loan.process.model.LoanSummary;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository("loanSummaryDao")
public class LoanSummaryDaoImpl extends GenericDaoImpl<LoanSummary> implements LoanSummaryDao {

    @Override
    public LoanSummary getByOnDateAndLoanId(Date onDate, long loanId){
        return (LoanSummary) getCurrentSession().createQuery("from LoanSummary where onDate = '" + onDate + "' and loanId = '" + loanId + "'").uniqueResult();
    }

}
