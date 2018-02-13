package kg.gov.mf.loan.process.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Repository
public abstract class GenericDaoImpl<E> implements GenericDao<E> {
	
	@Autowired
    protected SessionFactory sessionFactory;
    protected Class<? extends E> entityClass;

    public GenericDaoImpl() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        entityClass = (Class) pt.getActualTypeArguments()[0];
    }

    protected final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public void add(E entity) {
        getCurrentSession().save(entity);
    }

    public List<E> list() {
        return getCurrentSession().createCriteria(entityClass).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<E> listByParam(String param) {
        return getCurrentSession().createCriteria(entityClass).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.asc(param)).list();
    }

    public E getById(Long id) {
        return (E) getCurrentSession().get(entityClass, id);
    }

    public void update(E entity) {
        getCurrentSession().update(entity);
    }

    public void remove(E entity) {
        getCurrentSession().delete(entity);
    }
	
}
