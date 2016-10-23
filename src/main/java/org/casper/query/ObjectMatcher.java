package org.casper.query;

import org.casper.exception.CasperException;
import org.casper.utils.CasperUtils;

import java.util.Collection;


/**
 * Allows and object to be matched against a SQL-style query
 *
 * -- ex: Matching an object using preferred syntax
 * <pre>
 * {@code
 *      if (ObjectMatcher.result(o).where("name").eq("Bob").and("age").not().eq(25).isMatch())
 *          // Do something based on matching object
 * }
 * </pre>
 *
 * -- ex: Matching an object using alternative syntax
 * <pre>
 * {@code
 *      if (ObjectMatcher.result(o).eq("name", "Bob").and().ne("age", 25).isMatch())
 *          // Do something based on matching object
 * }
 * </pre>
 *
 * -- ex: Building an ObjectMatcher
 * <pre>
 * {@code
 *      ObjectMatcher<MyObject> om = ObjectMatcher.result(o);
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
    private boolean result;
    private boolean skipNext;
    private boolean negate;
    private T source;
    private Class<?> sourceClass;
    private String field;

    private ObjectMatcher(T source) {
        result = false;
        skipNext = false;
        negate = false;
        this.source = source;
        sourceClass = this.source.getClass();
    }

    /**
     * Sets the source object
     *
     * @param t   the source object
     * @param <T>
     * @return new instance of ObjectMatcher with source object set
     * @since 1.2
     */
    public static <T> ObjectMatcher<T> match(T t) {
        return new ObjectMatcher<>(t);
    }


    /**
     * Sets the source object
     *
     * @param t   the source object
     * @param <T>
     * @return new instance of ObjectMatcher with source object set
     * @since 1.0
     * @deprecated 1.2 see {@link #match}
     */
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
        return result;
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
            this.result = negate != (result < 0);
        } else if (mode == CasperUtils.Mode.GreaterThan) {
            this.result = negate != (result > 0);
        } else if (mode == CasperUtils.Mode.LessThanEqual) {
            this.result = negate != (result <= 0);
        } else if (mode == CasperUtils.Mode.GreaterThanEqual) {
            this.result = negate != (result >= 0);
        } else {
            this.result = negate != (result == 0);
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

    public ObjectMatcher<T> le(String field, Number value) throws CasperException {
        return test(field, value, CasperUtils.Mode.LessThanEqual);
    }

    public ObjectMatcher<T> le(Number value) throws CasperException {
        return le(field, value);
    }

    public ObjectMatcher<T> ge(String field, Number value) throws CasperException {
        return test(field, value, CasperUtils.Mode.GreaterThanEqual);
    }

    public ObjectMatcher<T> ge(Number value) throws CasperException {
        return ge(field, value);
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

    /**
     * Tests whether the value of the specified field is in a group of values
     *
     * @param field the name of the field in the source object to check
     * @param value an array of values to search
     * @return the current instance of the ObjectMatcher with statuses set
     * @throws CasperException
     * @since 1.2
     */
    public ObjectMatcher<T> in(String field, Object[] value) throws CasperException {
        return test(field, value, CasperUtils.Mode.In);
    }

    public ObjectMatcher<T> in(Object[] value) throws CasperException {
        return in(field, value);
    }

    public ObjectMatcher<T> in(String field, Collection<?> value) throws CasperException {
        return in(field, value.toArray());
    }

    public ObjectMatcher<T> in(Collection<?> value) throws CasperException {
        return in(field, value);
    }

    /**
     * Tests whether the value of the specified field is between a specific
     * range of numbers
     *
     * @param field the name of the field in the source object to check
     * @param start the minimum number
     * @param end   the maximum number
     * @return the current instance of the ObjectMatcher with statuses set
     * @throws CasperException
     * @since 1.2
     */
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
                result = !negate;
                negate = false;
                return this;
            }
        }

        result = negate;
        negate = false;
        return this;
    }

    public ObjectMatcher<T> between(Number start, Number end) throws CasperException {
        return between(field, start, end);
    }

    /**
     * Tests whether the value of the specified field is True, False or Unknown
     * this method was inspired by the MySQL IS comparison
     *
     * @param field the name of the field in the source object to check
     * @param value True (truthy) False (falsy) or Unknown (null)
     * @return the current instance of the ObjectMatcher with statuses set
     * @throws CasperException
     * @since 1.2
     */
    public ObjectMatcher<T> is(String field, Is value) throws CasperException {
        if (skipNext) {
            skipNext = false;
            negate = false;
            return this;
        }

        Object fieldValue = CasperUtils.getFieldValue(source, field);

        if (fieldValue == null && value == Is.Unknown) {
            result = !negate;
        } else {
            if (fieldValue instanceof Boolean) {
                if (value == Is.True) {
                    result = negate != ((Boolean) fieldValue);
                } else if (value == Is.False) {
                    result = negate != (!((Boolean) fieldValue));
                }
            } else if (fieldValue instanceof Number) {
                Double d = ((Number) fieldValue).doubleValue();
                if (value == Is.True) {
                    result = negate != (d > 0);
                } else if (value == Is.False) {
                    result = negate != (d <= 0);
                }
            } else if (fieldValue instanceof String) {
                String s = ((String) fieldValue).trim();
                if (value == Is.True) {
                    result = negate != (s.length() > 0);
                } else if (value == Is.False) {
                    result = negate != (s.length() == 0);
                }
            }

            // TODO more types!
        }

        negate = false;
        return this;
    }

    public ObjectMatcher<T> is(Is value) throws CasperException {
        return is(field, value);
    }

    /**
     * Tests whether the value of the specified field is not True, False or Unknown
     * this method was inspired by the MySQL IS NOT comparison
     *
     * @param field the name of the field in the source object to check
     * @param value True (truthy) False (falsy) or Unknown (null)
     * @return the current instance of the ObjectMatcher with statuses set
     * @throws CasperException
     * @since 1.2
     */
    public ObjectMatcher<T> isNot(String field, Is value) throws CasperException {
        negate = true;
        return is(field, value);
    }

    public ObjectMatcher<T> isNot(Is value) throws CasperException {
        return isNot(field, value);
    }

    /**
     * Tests whether the value of the specified field is null
     *
     * @param field the name of the field in the source object to check
     * @return the current instance of the ObjectMatcher with statuses set
     * @throws CasperException
     * @since 1.2
     */
    public ObjectMatcher<T> isNull(String field) throws CasperException {
        if (skipNext) {
            skipNext = false;
            negate = false;
            return this;
        }

        Object o = CasperUtils.getFieldValue(source, field);
        result = negate != (o == null);
        negate = false;
        return this;
    }

    public ObjectMatcher<T> isNull() throws CasperException {
        return isNull(field);
    }

    /**
     * Tests whether the value of the specified field is not null
     *
     * @param field the name of the field in the source object to check
     * @return the current instance of the ObjectMatcher with statuses set
     * @throws CasperException
     * @since 1.2
     */
    public ObjectMatcher<T> isNotNull(String field) throws CasperException {
        negate = true;
        return isNull(field);
    }

    public ObjectMatcher<T> isNotNull() throws CasperException {
        return isNotNull(field);
    }

    public ObjectMatcher<T> not() {
        negate = true;
        return this;
    }

    public ObjectMatcher<T> and() {
        skipNext = !result;
        return this;
    }

    public ObjectMatcher<T> or() {
        skipNext = result;
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

    @Override
    public boolean equals(Object o) {
        // ObjectMatcher.match(t).equals(o);q
        // TODO compare types
        // TODO compare all fields
        return false;
    }

    public enum Is {
        True, False, Unknown
    }
}
