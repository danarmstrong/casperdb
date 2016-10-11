package org.casper.database;

import java.util.*;

public class CasperCollection<T> implements Iterable<T> {
    private List<T> records;
    private Map<String, Object> indexes;

    public CasperCollection() {
        records = Collections.synchronizedList(new ArrayList<T>());
        indexes = Collections.synchronizedMap(new HashMap<String, Object>());
    }

    public synchronized T add(T t) {
        for (T e : records) {
            if (t.equals(e)) {
                records.remove(e);
                break;
            }
        }

        records.add(t);
        return t;
    }

    public synchronized void remove(T t) {
        for (T e : records) {
            if (t.equals(e)) {
                records.remove(e);
                break;
            }
        }
    }

    public List<T> toList() {
        return records;
    }

    public int count() {
        return records.size();
    }

    @Override
    public Iterator<T> iterator() {
        return records.iterator();
    }
}
