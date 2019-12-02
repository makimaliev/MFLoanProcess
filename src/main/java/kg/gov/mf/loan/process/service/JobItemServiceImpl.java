package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericServiceImpl;
import kg.gov.mf.loan.process.dao.JobItemDao;
import kg.gov.mf.loan.process.model.JobItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service("jobService")
@Transactional
public class JobItemServiceImpl extends GenericServiceImpl<JobItem> implements JobItemService {

    @Autowired
    JobItemDao jobItemDao;

    @Override
    public JobItem getByName(String name)
    {
        return this.jobItemDao.getByName(name);
    }

    @Override
    public void runDailyCalculateProcedure(Date date){
        this.jobItemDao.runDailyCalculateProcedure(date);
    }

    @Override
    public void runManualCalculateProcedure(long loanId, Date date){
        this.jobItemDao.runManualCalculateProcedure(loanId, date);
    }

    @Override
    public void runDailyCalculateProcedureForOneLoan(long loanId, Date date){
        this.jobItemDao.runDailyCalculateProcedureForOneLoan(loanId, date);
    }

    @Override
    public void runFixedCalculateProcedure(Date date)
    {
        this.jobItemDao.runFixedCalculateProcedure(date);
    }

    @Override
    public void updateDebtorGroupAndSubGroup()
    {
        this.jobItemDao.updateDebtorGroupAndSubGroup();
    }
}
