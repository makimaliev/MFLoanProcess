package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDaoImpl;
import kg.gov.mf.loan.process.model.Accrue;
import org.springframework.stereotype.Repository;

@Repository("accrueDao")
public class AccrueDaoImpl extends GenericDaoImpl<Accrue> implements AccrueDao {
}
