package kg.gov.mf.loan.process.service;

import java.util.List;

public interface GenericService<E> {

	void add(E entity);
    List<E> list();
    List<E> listByParam(String param);
    E getById(Long id);
    void update(E entity);
    void remove(E entity);
	
}
