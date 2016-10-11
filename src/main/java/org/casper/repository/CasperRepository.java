package org.casper.repository;

import java.util.List;

public interface CasperRepository<T> {
    T save(T t);
    T remove(T t);
    T delete(T t);

    int count();

    //T findOne(ID id);
    List<T> findAll();
}
