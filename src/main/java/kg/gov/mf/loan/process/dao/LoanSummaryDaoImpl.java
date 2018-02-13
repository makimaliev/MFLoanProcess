package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.process.model.LoanSummary;
import org.springframework.stereotype.Repository;

@Repository("loanSummaryDao")
public class LoanSummaryDaoImpl extends GenericDaoImpl<LoanSummary> implements LoanSummaryDao {
}
