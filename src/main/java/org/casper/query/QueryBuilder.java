package org.casper.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class QueryBuilder implements Iterable<QueryPart> {
    private Type type;
    private String repository;
    private List<QueryPart> parts;

    public QueryBuilder() {
        parts = new ArrayList<>();
    }

    public QueryBuilder(String repository, Type type) {
        this();
        this.repository = repository;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getRepository() {
        return repository;
    }

    public List<QueryPart> getParts() {
        return parts;
    }

    public void add(QueryPart.Command command, String field, Object value) {
        parts.add(new QueryPart(command, field, value));
    }

    public void add(QueryPart.Command command, Object value) {
        parts.add(new QueryPart(command, value));
    }

    public void add(QueryPart.Command command) {
        parts.add(new QueryPart(command));
    }

    public enum Type {
        FIND, REMOVE
    }

    @Override
    public Iterator<QueryPart> iterator() {
        return Collections.unmodifiableList(parts).iterator();
    }
}
