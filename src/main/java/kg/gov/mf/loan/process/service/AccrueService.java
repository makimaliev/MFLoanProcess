package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericService;
import kg.gov.mf.loan.process.model.Accrue;

import java.util.Date;

public interface AccrueService extends GenericService<Accrue> {
    Accrue getByOnDateAndLoanId(Date onDate, long loanId);
}
