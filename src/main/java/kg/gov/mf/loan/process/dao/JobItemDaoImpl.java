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
    public void runFixedCalculateProcedure(Date date)
    {
        Query query = getCurrentSession().createSQLQuery("CALL runCalculateLoanDetailedSummaryForAllLoansFixed(:inDate)")
                .setParameter("inDate",date);

        query.executeUpdate();
    }

    @Override
    public void runManualCalculateProcedure(long loanId, Date date)
    {
        Query query = getCurrentSession().createSQLQuery("CALL run_calc_manual_summary_for_loan(:loan_id, :inDate, 'MANUAL')")
                .setParameter("inDate",date)
                .setParameter("loan_id", loanId);

        query.executeUpdate();
    }

    @Override
    public void runDailyCalculateProcedureForOneLoan(long loanId, Date date)
    {
        Query query = getCurrentSession().createSQLQuery("CALL run_calc_manual_summary_for_loan(:loan_id, :inDate, 'SYSTEM')")
                .setParameter("inDate",date)
                .setParameter("loan_id", loanId);

        query.executeUpdate();
    }

    @Override
    public void updateDebtorGroupAndSubGroup()
    {
        Query query = getCurrentSession().createSQLQuery("CALL update_debtor_group_and_sub_group()");

        query.executeUpdate();
    }
}
