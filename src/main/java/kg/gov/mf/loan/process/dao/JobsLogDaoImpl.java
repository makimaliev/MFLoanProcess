package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.manage.dao.GenericDaoImpl;
import kg.gov.mf.loan.process.model.JobsLog;
import org.springframework.stereotype.Repository;

@Repository("jobsLogDao")
public class JobsLogDaoImpl extends GenericDaoImpl<JobsLog> implements JobsLogDao {
}
