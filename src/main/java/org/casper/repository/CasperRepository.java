package org.casper.repository;

import java.util.List;

public interface CasperRepository<T> {
    T save(T t);

    T remove(T t);

    List<T> remove(List<T> t);

    List<T> remove(T[] t);

    T delete(T t);

    List<T> delete(List<T> t);

    List<T> delete(T[] t);

    int count();

    //T findOne(ID id);
    //T removeOne(ID id);
    List<T> findAll();
}
