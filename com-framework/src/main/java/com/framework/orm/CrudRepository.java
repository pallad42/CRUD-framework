package com.framework.orm;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class CrudRepository<T, ID> {

	private Class<T> persistentClass;

	@SuppressWarnings("unchecked")
	public CrudRepository() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public long count() {
		return CrudMethods.count(persistentClass);
	}
	
	public void delete(T entity) {
		CrudMethods.delete(persistentClass, entity);
	}
	
	public void deleteAll() {
		CrudMethods.deleteAll(persistentClass);
	}

	public void deleteAll(Collection<T> entities) {
		CrudMethods.deleteAll(persistentClass, entities);
	}
	
	public void deleteById(ID id) {
		CrudMethods.deleteById(persistentClass, id);
	}
	
	public boolean existsById(ID id) {
		return CrudMethods.existsById(persistentClass, id);
	}
	
	public Collection<T> findAll() {
		return CrudMethods.findAll(persistentClass);
	}
	
	public Collection<T> findAllById(Collection<ID> ids) {
		return CrudMethods.findAllById(persistentClass, ids);
	}
	
	public T findById(ID id) {
		return CrudMethods.findById(persistentClass, id);
	}
	
	public T save(T entity) {
		return CrudMethods.save(persistentClass, entity);
	}
	
	public Collection<T> saveAll(Collection<T> entities) {
		return CrudMethods.saveAll(persistentClass, entities);
	}
	
}
