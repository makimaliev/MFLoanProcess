package kg.gov.mf.loan.process.dao;

import kg.gov.mf.loan.process.model.Job;
import org.springframework.stereotype.Repository;

@Repository("jobDao")
public class JobDaoImpl extends GenericDaoImpl<Job> implements JobDao{
}
