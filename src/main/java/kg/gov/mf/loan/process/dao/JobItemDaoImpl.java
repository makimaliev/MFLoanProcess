package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDaoImpl;
import kg.gov.mf.loan.process.model.JobItem;
import org.springframework.stereotype.Repository;

@Repository("jobDao")
public class JobItemDaoImpl extends GenericDaoImpl<JobItem> implements JobItemDao{

    @Override
    public JobItem getByName(String name){
        return (JobItem) getCurrentSession().createQuery("from JobItem where name ='"+ name+"'").uniqueResult();
    }
}
