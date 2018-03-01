package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.service.GenericServiceImpl;
import kg.gov.mf.loan.process.model.OnDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("onDateService")
@Transactional
public class OnDateServiceImpl extends GenericServiceImpl<OnDate> implements OnDateService {
}
