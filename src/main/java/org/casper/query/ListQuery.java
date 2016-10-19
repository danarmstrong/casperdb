package org.casper.query;

import org.casper.exception.CasperException;

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
        query.add(QueryPart.Command.EqField, field, value);
        return this;
    }

    public ListQuery<T> eq(Object value) {
        query.add(QueryPart.Command.Eq, value);
        return this;
    }

    public ListQuery<T> ne(String field, Object value) {
        query.add(QueryPart.Command.NeField, field, value);
        return this;
    }

    public ListQuery<T> ne(Object value) {
        query.add(QueryPart.Command.Ne, value);
        return this;
    }

    public ListQuery<T> lg(String field, Number value) {
        return ne(field, value);
    }

    public ListQuery<T> lg(Number value) {
        return ne(value);
    }

    @Deprecated
    public ListQuery<T> neq(String field, Object value) {
        return ne(field, value);
    }

    @Deprecated
    public ListQuery<T> neq(Object value) {
        return ne(value);
    }

    public ListQuery<T> lt(String field, Number value) {
        query.add(QueryPart.Command.LtField, field, value);
        return this;
    }

    public ListQuery<T> lt(Number value) {
        query.add(QueryPart.Command.Lt, value);
        return this;
    }

    public ListQuery<T> gt(String field, Number value) {
        query.add(QueryPart.Command.GtField, field, value);
        return this;
    }

    public ListQuery<T> gt(Number value) {
        query.add(QueryPart.Command.Gt, value);
        return this;
    }

    public ListQuery<T> like(String field, String value) {
        query.add(QueryPart.Command.LikeField, field, value);
        return this;
    }

    public ListQuery<T> like(String value) {
        query.add(QueryPart.Command.Like, value);
        return this;
    }

    public ListQuery<T> and() {
        query.add(QueryPart.Command.And);
        return this;
    }

    public ListQuery<T> and(String field) {
        query.add(QueryPart.Command.AndField, field, null);
        return this;
    }

    public ListQuery<T> or() {
        query.add(QueryPart.Command.Or);
        return this;
    }

    public ListQuery<T> or(String field) {
        query.add(QueryPart.Command.OrField, field, null);
        return this;
    }

    public ListQuery<T> not() {
        query.add(QueryPart.Command.Not);
        return this;
    }

    public ListQuery<T> where() {
        query.add(QueryPart.Command.Where);
        return this;
    }

    public ListQuery<T> where(String field) {
        query.add(QueryPart.Command.WhereField, field, null);
        return this;
    }

    public ListQuery<T> limit(int max) {
        this.max = max;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <X> List<X> execute() throws CasperException {
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

    private <X> ObjectMatcher<X> buildQuery(X x) throws CasperException {
        ObjectMatcher<X> q = ObjectMatcher.match(x);
        for (QueryPart part : query) {
            switch (part.getCommand()) {
                case And:
                    q.and();
                    break;
                case AndField:
                    q.and(part.getField());
                    break;
                case Or:
                    q.or();
                    break;
                case OrField:
                    q.or(part.getField());
                    break;
                case Not:
                    q.not();
                    break;
                case Where:
                    q.where();
                    break;
                case WhereField:
                    q.where(part.getField());
                    break;
                case Eq:
                    q.eq(part.getValue());
                    break;
                case EqField:
                    q.eq(part.getField(), part.getValue());
                    break;
                case Ne:
                    q.ne(part.getValue());
                    break;
                case NeField:
                    q.eq(part.getField(), part.getValue());
                    break;
                case Lt:
                    q.lt((Number) part.getValue());
                    break;
                case LtField:
                    q.lt(part.getField(), (Number) part.getValue());
                    break;
                case Gt:
                    q.gt((Number) part.getValue());
                    break;
                case GtField:
                    q.gt(part.getField(), (Number) part.getValue());
                    break;
                case Ge:
                    // TODO add GE
                    break;
                case GeField:
                    // TODO add GE_FIELD
                    break;
                case Le:
                    // TODO add LE
                    break;
                case LeField:
                    // TODO add LE_FIELD
                    break;
                case In:
                    //TODO what about arrays?
                    q.in((List<T>) part.getValue());
                    break;
                case InField:
                    //TODO what about arrays?
                    q.in(part.getField(), (List<T>) part.getValue());
                    break;
                case Between:
                    // TODO handle start and end?
                    break;
                case BetweenField:
                    // TODO handle start and end?
                    break;
                case Like:
                    q.like((String) part.getValue());
                    break;
                case LikeField:
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
                case And:
                    sb.append(" and  ");
                    break;
                case AndField:
                    sb.append(" and ").append(part.getField());
                    break;
                case Or:
                    sb.append(" or ");
                    break;
                case OrField:
                    sb.append(" or ").append(part.getField());
                    break;
                case Not:
                    sb.append("!");
                    break;
                case Where:
                    sb.append("");
                    break;
                case WhereField:
                    sb.append(part.getField());
                    break;
                case Eq:
                    sb.append(" = ").append(part.getValue());
                    break;
                case EqField:
                    sb.append(part.getField()).append(" = ").append(part.getValue());
                    break;
                case Ne:
                    sb.append(" != ").append(part.getValue());
                    break;
                case NeField:
                    sb.append(part.getField()).append(" != ").append(part.getValue());
                    break;
                case Like:
                    sb.append(" like ").append(part.getValue());
                    break;
                case LikeField:
                    sb.append(part.getField()).append(" like ").append(part.getValue());
                    break;
            }
        }

        return sb.toString();
    }
}
