package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericServiceImpl;
import kg.gov.mf.loan.process.dao.LoanSummaryDao;
import kg.gov.mf.loan.process.model.LoanSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service("loanSummaryService")
@Transactional
public class LoanSummaryServiceImpl extends GenericServiceImpl<LoanSummary> implements LoanSummaryService {

    @Autowired
    LoanSummaryDao loanSummaryDao;

    @Override
    public LoanSummary getByOnDateAndLoanId(Date onDate, long loanId)
    {
        return this.loanSummaryDao.getByOnDateAndLoanId(onDate, loanId);
    }
}
