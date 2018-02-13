package kg.gov.mf.loan.process.dao;

import java.util.List;

public interface GenericDao<E> {

	void add(E entity);
    List<E> list();
    List<E> listByParam(String param);
    E getById(Long id);
    void update(E entity);
    void remove(E entity);
	
}
