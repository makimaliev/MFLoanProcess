package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDaoImpl;
import kg.gov.mf.loan.process.model.JobItem;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository("jobDao")
public class JobItemDaoImpl extends GenericDaoImpl<JobItem> implements JobItemDao{

    @Override
    public JobItem getByName(String name){
        return (JobItem) getCurrentSession().createQuery("from JobItem where name ='"+ name+"'").uniqueResult();
    }

    @Override
    public void runDailyCalculateProcedure(Date date)
    {
        Query query = getCurrentSession().createSQLQuery("CALL runCalculateLoanDetailedSummaryForAllLoans(:inDate)")
                 .setParameter("inDate",date);

        System.out.println(query.uniqueResult());
    }

    @Override
    public void runManualCalculateProcedure(long loanId, Date date)
    {
        Query query = getCurrentSession().createSQLQuery("CALL calculateLoanDetailedSummaryUntilOnDate(:loan_id, :inDate, 1)")
                .setParameter("inDate",date)
                .setParameter("loan_id", loanId);

        query.executeUpdate();
    }
}
