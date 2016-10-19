package org.casper.query;

import org.casper.exception.CasperException;
import org.casper.utils.CasperUtils;

import java.util.List;


/**
 * Allows and object to be matched against a SQL-style query
 *
 * -- ex: Matching an object using preferred syntax
 * <pre>
 * {@code
 *      if (ObjectMatcher.match(o).where("name").eq("Bob").and("age").not().eq(25).isMatch())
 *          // Do something based on matching object
 * }
 * </pre>
 *
 * -- ex: Matching an object using alternative syntax
 * <pre>
 * {@code
 *      if (ObjectMatcher.match(o).eq("name", "Bob").and().ne("age", 25).isMatch())
 *          // Do something based on matching object
 * }
 * </pre>
 *
 * -- ex: Building an ObjectMatcher
 * <pre>
 * {@code
 *      ObjectMatcher<MyObject> om = ObjectMatcher.match(o);
 *      o.where("name").eq("Bob");
 *      o.and("age").not().eq(25);
 *
 *      if (o.isMatch())
 *          // Do something based on matching object
 * }
 * </pre>
 *
 * @author Dan Armstrong
 * @since 1.0
 * @param <T>
 */
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

    public static <T> ObjectMatcher<T> match(T t) {
        return new ObjectMatcher<T>(t);
    }

    @Deprecated
    public static <T> ObjectMatcher<T> from(T t) {
        return match(t);
    }

    public T getSource() {
        return source;
    }

    public Class<?> getSourceClass() {
        return sourceClass;
    }

    public String getField() {
        return field;
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

    public ObjectMatcher<T> ne(String field, Object value) throws CasperException {
        negate = true;
        return eq(field, value);
    }

    public ObjectMatcher<T> ne(Object value) throws CasperException {
        return ne(field, value);
    }

    @Deprecated
    public ObjectMatcher<T> neq(String field, Object value) throws CasperException {
        return ne(field, value);
    }

    @Deprecated
    public ObjectMatcher<T> neq(Object value) throws CasperException {
        return ne(value);
    }

    public ObjectMatcher<T> lg(String field, Number value) throws CasperException {
        return ne(field, value);
    }

    public ObjectMatcher<T> lg(Number value) throws CasperException {
        return ne(value);
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

    public ObjectMatcher<T> like(String field, String value) throws CasperException {

        value = value.replace("\\%", "$$__PERCENT__$$");
        value = value.replace("%", "%$$__WILDCARD__$$%");

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

        return test(field, sb.toString(), CasperUtils.Mode.Regex);
    }

    public ObjectMatcher<T> like(String value) throws CasperException {
        return like(field, value);
    }

    public ObjectMatcher<T> in(String field, Object[] value) throws CasperException {
        return test(field, value, CasperUtils.Mode.In);
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
            if (CasperUtils.compare(source, field, i, CasperUtils.Mode.Between) == 0) {
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
