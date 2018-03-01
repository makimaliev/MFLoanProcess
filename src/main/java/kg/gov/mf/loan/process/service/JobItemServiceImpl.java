package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericServiceImpl;
import kg.gov.mf.loan.process.model.JobItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("jobService")
@Transactional
public class JobItemServiceImpl extends GenericServiceImpl<JobItem> implements JobItemService {
}
