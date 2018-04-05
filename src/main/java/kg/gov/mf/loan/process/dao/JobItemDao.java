package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDao;
import kg.gov.mf.loan.process.model.JobItem;

public interface JobItemDao extends GenericDao<JobItem> {

    JobItem getByName(String name);

}
