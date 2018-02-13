package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.process.model.Accrue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("accrueService")
@Transactional
public class AccrueServiceImpl extends GenericServiceImpl<Accrue> implements AccrueService {
}
