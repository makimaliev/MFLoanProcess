package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericServiceImpl;
import kg.gov.mf.loan.process.model.JobsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("jobsLogService")
@Transactional
public class JobsLogServiceImpl extends GenericServiceImpl<JobsLog> implements JobsLogService {
}
