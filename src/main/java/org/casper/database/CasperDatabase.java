package org.casper.database;

import org.casper.exception.CasperException;
import org.casper.query.ListQuery;
import org.casper.query.QueryBuilder;
import org.casper.query.QueryPart;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CasperDatabase {
    private Map<String, CasperCollection<?>> database;

    public CasperDatabase() {
        database = new HashMap<>();
    }

    public <T> void createCollection(String name) {
        if (!database.containsKey(name))
            database.put(name, new CasperCollection<T>());
    }

    public CasperCollection<?> getCollection(String name) {
        return database.get(name);
    }

    public void dropCollection(String name) {
        database.remove(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T save(String repository, T t) {
        ((CasperCollection<T>)database.get(repository)).add(t);
        return t;
    }

    @SuppressWarnings("unchecked")
    public <T> T remove(String repository, T t) {
        ((CasperCollection<T>)database.get(repository)).remove(t);
        return t;
    }

    public <T> List<T> remove(String repository, List<T> t) {
        CasperCollection<T> records = (CasperCollection<T>) database.get(repository);
        for (T e : t) {
            for (T c : records) {
                if (e.equals(c))
                    records.remove(c);
            }
        }

        return t;
    }

    public <T> List<T> remove(String repository, T[] t) {
        return remove(repository, Arrays.asList(t));
    }

    public <T> T delete(String repository, T t) {
        return remove(repository, t);
    }

    public <T> List<T> delete(String repository, List<T> t) {
        return remove(repository, t);
    }

    public <T> List<T> delete(String repository, T[] t) {
        return remove(repository, Arrays.asList(t));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(String repository) {
        return ((CasperCollection<T>)database.get(repository)).toList();
    }

    public <T> T findOne(String repository, T id) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> find(final QueryBuilder qb) throws CasperException {

        ListQuery q = ListQuery.from(database.get(qb.getRepository()).toList());

        for (QueryPart p : qb) {
            switch (p.getCommand()) {
                case EQ_FIELD:
                    q.eq(p.getField(), p.getValue());
                    break;
                case LIKE_FIELD:
                    q.like(p.getField(), (String) p.getValue());
                    break;
                case AND:
                    q.and();
                    break;
                case OR:
                    q.or();
                    break;
                case NOT:
                    q.not();
                    break;
                case LIMIT:
                    q.limit((Integer) p.getValue());
            }
        }

        return q.execute();
    }

    public <T> T findOne(QueryBuilder qb) throws CasperException {
        qb.add(QueryPart.Command.LIMIT, 1);
        List<T> r = find(qb);
        return r.size() > 0 ? r.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public int count(String repository) {
        return database.get(repository).count();
    }


}
