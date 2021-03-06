package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDao;
import kg.gov.mf.loan.process.model.JobItem;

import java.util.Date;

public interface JobItemDao extends GenericDao<JobItem> {

    JobItem getByName(String name);
    void runDailyCalculateProcedure(Date date);
    void runManualCalculateProcedure(long loanId, Date date);
    void runFixedCalculateProcedure(Date date);
    void runDailyCalculateProcedureForOneLoan(long loanId, Date date);
    void updateDebtorGroupAndSubGroup();
}
