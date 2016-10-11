package org.casper.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ListQuery<T extends Collection<?>> {
    private T source;
    private QueryBuilder query;
    private int max;

    private ListQuery(T source) {
        this.source = source;
        query = new QueryBuilder();
        max = 0;
    }

    public static <T extends Collection<?>> ListQuery<T> from(T source) {
        return new ListQuery<T>(source);
    }

    public ListQuery<T> eq(String field, Object value) {
        query.add(QueryPart.Command.EQ_FIELD, field, value);
        return this;
    }

    public ListQuery<T> eq(Object value) {
        query.add(QueryPart.Command.EQ, value);
        return this;
    }

    public ListQuery<T> neq(String field, Object value) {
        query.add(QueryPart.Command.NEQ_FIELD, field, value);
        return this;
    }

    public ListQuery<T> neq(Object value) {
        query.add(QueryPart.Command.NEQ, value);
        return this;
    }

    public ListQuery<T> like(String field, String value) {
        query.add(QueryPart.Command.LIKE_FIELD, field, value);
        return this;
    }

    public ListQuery<T> like(String value) {
        query.add(QueryPart.Command.LIKE, value);
        return this;
    }

    public ListQuery<T> and() {
        query.add(QueryPart.Command.AND);
        return this;
    }

    public ListQuery<T> and(String field) {
        query.add(QueryPart.Command.AND_FIELD, field, null);
        return this;
    }

    public ListQuery<T> or() {
        query.add(QueryPart.Command.OR);
        return this;
    }

    public ListQuery<T> or(String field) {
        query.add(QueryPart.Command.OR_FIELD, field, null);
        return this;
    }

    public ListQuery<T> not() {
        query.add(QueryPart.Command.NOT);
        return this;
    }

    public ListQuery<T> where() {
        query.add(QueryPart.Command.WHERE);
        return this;
    }

    public ListQuery<T> where(String field) {
        query.add(QueryPart.Command.WHERE_FIELD, field, null);
        return this;
    }

    public ListQuery<T> limit(int max) {
        this.max = max;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <X> List<X> execute() {
        List<X> results = new ArrayList<>();

        for (X x : (Collection<X>) this.source) {
            if (buildQuery(x).isMatch()) {
                results.add(x);
                if (results.size() == max)
                    break;
            }
        }
        return results;
    }

    private <X> ObjectMatcher<X> buildQuery(X x) {
        ObjectMatcher<X> q = ObjectMatcher.from(x);
        for (QueryPart part : query) {
            switch (part.getCommand()) {
                case AND:
                    q.and();
                    break;
                case AND_FIELD:
                    q.and(part.getField());
                    break;
                case OR:
                    q.or();
                    break;
                case OR_FIELD:
                    q.or(part.getField());
                    break;
                case NOT:
                    q.not();
                    break;
                case WHERE:
                    q.where();
                    break;
                case WHERE_FIELD:
                    q.where(part.getField());
                    break;
                case EQ:
                    q.eq(part.getValue());
                    break;
                case EQ_FIELD:
                    q.eq(part.getField(), part.getValue());
                    break;
                case NEQ:
                    q.neq(part.getValue());
                    break;
                case NEQ_FIELD:
                    q.eq(part.getField(), part.getValue());
                    break;
                case LIKE:
                    q.like((String) part.getValue());
                    break;
                case LIKE_FIELD:
                    q.like(part.getField(), (String) part.getValue());
                    break;
            }
        }

        return q;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ").append(source.getClass().getSimpleName())
                .append(" where ");

        for (QueryPart part : query) {
            switch (part.getCommand()) {
                case AND:
                    sb.append(" and  ");
                    break;
                case AND_FIELD:
                    sb.append(" and ").append(part.getField());
                    break;
                case OR:
                    sb.append(" or ");
                    break;
                case OR_FIELD:
                    sb.append(" or ").append(part.getField());
                    break;
                case NOT:
                    sb.append("!");
                    break;
                case WHERE:
                    sb.append("");
                    break;
                case WHERE_FIELD:
                    sb.append(part.getField());
                    break;
                case EQ:
                    sb.append(" = ").append(part.getValue());
                    break;
                case EQ_FIELD:
                    sb.append(part.getField()).append(" = ").append(part.getValue());
                    break;
                case NEQ:
                    sb.append(" != ").append(part.getValue());
                    break;
                case NEQ_FIELD:
                    sb.append(part.getField()).append(" != ").append(part.getValue());
                    break;
                case LIKE:
                    sb.append(" like ").append(part.getValue());
                    break;
                case LIKE_FIELD:
                    sb.append(part.getField()).append(" like ").append(part.getValue());
                    break;
            }
        }

        return sb.toString();
    }
}
