package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericService;
import kg.gov.mf.loan.process.model.JobItem;

import java.util.Date;

public interface JobItemService extends GenericService<JobItem> {

    JobItem getByName(String name);
    void runDailyCalculateProcedure(Date date);
    void runManualCalculateProcedure(long loanId, Date date);
    void runFixedCalculateProcedure(Date date);
    void runDailyCalculateProcedureForOneLoan(long loanId, Date date);

    void updateDebtorGroupAndSubGroup();
}
