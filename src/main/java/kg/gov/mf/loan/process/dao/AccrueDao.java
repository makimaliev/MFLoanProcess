package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDao;
import kg.gov.mf.loan.process.model.Accrue;

import java.util.Date;

public interface AccrueDao extends GenericDao<Accrue> {
    Accrue getByOnDateAndLoanId(Date onDate, long loanId);
}
