package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericServiceImpl;
import kg.gov.mf.loan.process.model.LoanDetailedSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("loanDetailedSummaryService")
@Transactional
public class LoanDetailedSummaryServiceImpl extends GenericServiceImpl<LoanDetailedSummary> implements LoanDetailedSummaryService {
}
