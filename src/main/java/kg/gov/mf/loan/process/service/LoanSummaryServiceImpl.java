package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericServiceImpl;
import kg.gov.mf.loan.process.model.LoanSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("loanSummaryService")
@Transactional
public class LoanSummaryServiceImpl extends GenericServiceImpl<LoanSummary> implements LoanSummaryService {
}
