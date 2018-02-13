package kg.gov.mf.loan.process.service;

import kg.gov.mf.loan.manage.dao.GenericDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public abstract class GenericServiceImpl<E> implements GenericService<E> {
	
	@Autowired
    protected GenericDao<E> dao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void add(E entity) {
	    dao.add(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> list() {
    	return dao.list();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<E> listByParam(String param) {
        return dao.listByParam(param);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public E getById(Long id) {
    	return dao.getById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void update(E entity) {
    	dao.update(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void remove(E entity) {
    	dao.remove(entity);
    }
}
