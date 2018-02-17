package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericServiceImpl;
import kg.gov.mf.loan.process.dao.LoanDetailedSummaryDao;
import kg.gov.mf.loan.process.model.LoanDetailedSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service("loanDetailedSummaryService")
@Transactional
public class LoanDetailedSummaryServiceImpl extends GenericServiceImpl<LoanDetailedSummary> implements LoanDetailedSummaryService {

    @Autowired
    LoanDetailedSummaryDao summaryDao;

    @Override
    public LoanDetailedSummary getByOnDateAndLoanId(Date onDate, long loanId){
        return this.summaryDao.getByOnDateAndLoanId(onDate, loanId);
    }

}
