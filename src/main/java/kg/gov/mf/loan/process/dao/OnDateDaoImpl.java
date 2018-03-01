package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDaoImpl;
import kg.gov.mf.loan.process.model.OnDate;
import org.springframework.stereotype.Repository;

@Repository("onDateDao")
public class OnDateDaoImpl extends GenericDaoImpl<OnDate> implements OnDateDao{
}
