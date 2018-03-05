package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDaoImpl;
import kg.gov.mf.loan.manage.util.DateUtils;
import kg.gov.mf.loan.process.model.Accrue;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository("accrueDao")
public class AccrueDaoImpl extends GenericDaoImpl<Accrue> implements AccrueDao {
    @Override
    public Accrue getByOnDateAndLoanId(Date onDate, long loanId){
        return (Accrue) getCurrentSession().createQuery("from Accrue where toDate = '" + DateUtils.format(onDate, DateUtils.FORMAT_POSTGRES_DATE) + "' and loanId = '" + loanId + "'").uniqueResult();
    }
}
