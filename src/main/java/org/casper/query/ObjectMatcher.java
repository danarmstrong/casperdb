package org.casper.query;

import org.casper.exception.CasperException;
import org.casper.utils.CasperUtils;

import java.lang.reflect.Field;
import java.util.List;


public class ObjectMatcher<T> {
    private boolean match;
    private boolean skipNext;
    private boolean negate;
    private T source;
    private Class<?> sourceClass;
    private String field;

    private ObjectMatcher(T source) {
        match = false;
        skipNext = false;
        negate = false;
        this.source = source;
        sourceClass = this.source.getClass();
    }

    public static <T> ObjectMatcher<T> from(T t) {
        return new ObjectMatcher<T>(t);
    }

    public T getSource() {
        return source;
    }

    public Class<?> getSourceClass() {
        return sourceClass;
    }

    public boolean isMatch() {
        return match;
    }

    private ObjectMatcher<T> test(String field, Object value, CasperUtils.Mode mode)
        throws CasperException {
        if (skipNext) {
            skipNext = false;
            negate = false;
            return this;
        }

        int result = CasperUtils.compare(source, field, value, mode);

        if (mode == CasperUtils.Mode.LessThan) {
            this.match = negate != (result < 0);
        } else if (mode == CasperUtils.Mode.GreaterThan) {
            this.match = negate != (result > 0);
        } else if (mode == CasperUtils.Mode.LessThanEqual) {
            this.match = negate != (result <= 0);
        } else if (mode == CasperUtils.Mode.GreaterThanEqual) {
            this.match = negate != (result >= 0);
        } else {
            this.match = negate != (result == 0);
        }

        negate = false;
        return this;
    }

    public ObjectMatcher<T> eq(String field, Object value) throws CasperException {
        return test(field, value, CasperUtils.Mode.Exact);
    }

    public ObjectMatcher<T> eq(Object value) throws CasperException {
        return eq(this.field, value);
    }

    public ObjectMatcher<T> neq(String field, Object value) throws CasperException {
        negate = true;
        return eq(field, value);
    }

    public ObjectMatcher<T> neq(Object value) throws CasperException {
        return neq(field, value);
    }

    public ObjectMatcher<T> like(String field, String value) throws CasperException {

        value = value.replace("\\%", "$$__PERCENT__$$");
        value = value.replace("%", "$$__WILDCARD__$$%");
        String[] parts = value.split("%");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            switch (part) {
                case "$$__WILDCARD__$$":
                    sb.append("(.*)");
                    break;
                case "$$__PERCENT__$$":
                    sb.append("%");
                    break;
                default:
                    sb.append(part);
            }
        }

        return test(field, value, CasperUtils.Mode.Regex);
    }

    public ObjectMatcher<T> like(String value) throws CasperException {
        return like(field, value);
    }


    public ObjectMatcher<T> lt(String field, Number value) throws CasperException {
        return test(field, value, CasperUtils.Mode.LessThan);
    }

    public ObjectMatcher<T> lt(Number value) throws CasperException {
        return lt(field, value);
    }

    public ObjectMatcher<T> gt(String field, Number value) throws CasperException {
        return test(field, value, CasperUtils.Mode.GreaterThan);
    }

    public ObjectMatcher<T> gt(Number value) throws CasperException {
        return gt(field, value);
    }

    public ObjectMatcher<T> in(String field, Object[] value) throws CasperException {
        return test(field, value, CasperUtils.Mode.Exact);
    }

    public ObjectMatcher<T> in(Object[] value) throws CasperException {
        return in(field, value);
    }

    public ObjectMatcher<T> in(String field, List<?> value) throws CasperException {
        return in(field, value.toArray());
    }

    public ObjectMatcher<T> in(List<?> value) throws CasperException {
        return in(field, value);
    }

    public ObjectMatcher<T> between(String field, Number start, Number end) throws CasperException {
        if (skipNext) {
            skipNext = false;
            negate = false;
            return this;
        }

        Double s, e;

        if (start.doubleValue() > end.doubleValue()) {
            s = end.doubleValue();
            e = start.doubleValue();
        } else if (start.doubleValue() < end.doubleValue()) {
            s = start.doubleValue();
            e = end.doubleValue();
        } else {
            return test(field, start, CasperUtils.Mode.Exact);
        }

        for (Double i = s; i <= e; ++i) {
            if (CasperUtils.compare(source, field, i) == 0) {
                this.match = !negate;
                negate = false;
                return this;
            }
        }

        this.match = negate;
        negate = false;
        return this;
    }

    public ObjectMatcher<T> between(Number start, Number end) throws CasperException {
        return between(field, start, end);
    }

    public ObjectMatcher<T> not() {
        negate = true;
        return this;
    }

    public ObjectMatcher<T> and() {
        skipNext = !match;
        return this;
    }

    public ObjectMatcher<T> or() {
        skipNext = match;
        return this;
    }

    public ObjectMatcher<T> where() {
        return this;
    }

    public ObjectMatcher<T> where(String field) {
        this.field = field;
        return this;
    }

    public ObjectMatcher<T> and(String field) {
        this.field = field;
        return and();
    }

    public ObjectMatcher<T> or(String field) {
        this.field = field;
        return or();
    }
}
