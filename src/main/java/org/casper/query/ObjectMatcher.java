package org.casper.query;

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

    public ObjectMatcher<T> eq(String field, Object value) {

        if (skipNext) {
            skipNext = false;
            negate = false;
            return this;
        }

        try {
            Object o = getFieldValue(field);
            this.match = o.getClass().equals(value.getClass()) && (negate != o.equals(value));
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            this.match = false;
        }

        negate = false;
        return this;
    }

    public ObjectMatcher<T> eq(Object value) {
        return eq(this.field, value);
    }

    public ObjectMatcher<T> neq(String field, Object value) {
        negate = true;
        return eq(field, value);
    }

    public ObjectMatcher<T> neq(Object value) {
        return neq(field, value);
    }

    public ObjectMatcher<T> like(String field, String value) {
        if (skipNext) {
            skipNext = false;
            negate = false;
            return this;
        }

        try {
            Object o = getFieldValue(field);
            if (!(o instanceof String)) {
                match = false;
                negate = false;
                return this;
            }

            String s = (String)o;
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

            System.out.println("Regex is: " + sb.toString());
            match = (negate != s.matches(sb.toString()));
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            match = false;
        }

        negate = false;
        return this;
    }

    public ObjectMatcher<T> like(String value) {
        return like(field, value);
    }

    private int compare(double a, double b) {
        return a < b ? -1 : a > b ? 1 : 0;
    }

    private ObjectMatcher<T> compare(String field, Number value, boolean lt) {
        if (skipNext) {
            skipNext = false;
            negate = false;
            return this;
        }

        try {
            Object o = getFieldValue(field);
            if (!(o instanceof Number)) {
                match = false;
                negate = false;
                return this;
            }

            Number v = (Number)o;

            int c = compare(v.doubleValue(), value.doubleValue());

            match = (negate != (lt ? c < 0 : c > 0));
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            match = false;
        }

        negate = false;
        return this;
    }

    public ObjectMatcher<T> lt(String field, Number value) {
        return compare(field, value, true);
    }

    public ObjectMatcher<T> lt(Number value) {
        return lt(field, value);
    }

    public ObjectMatcher<T> gt(String field, Number value) {
        return compare(field, value, false);
    }

    public ObjectMatcher<T> gt(Number value) {
        return gt(field, value);
    }

    public ObjectMatcher<T> in(String field, Object[] value) {
        if (skipNext) {
            skipNext = false;
            negate = false;
            return this;
        }

        try {
            Object o = getFieldValue(field);
            for (Object a : value) {
                this.match = o.getClass().equals(value.getClass()) && (negate != o.equals(value));
                if (match)
                    break;
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            this.match = false;
        }

        negate = false;
        return this;
    }

    public ObjectMatcher<T> in(Object[] value) {
        return in(field, value);
    }

    public ObjectMatcher<T> in(String field, List<?> value) {
        return in(field, value.toArray());
    }

    public ObjectMatcher<T> in(List<?> value) {
        return in(field, value);
    }

    public ObjectMatcher<T> between(String field, Number start, Number end) {

        return this;
    }

    public ObjectMatcher<T> between(Number start, Number end) {
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

    private Object getFieldValue(String field) throws NoSuchFieldException, IllegalAccessException {
        Field f = sourceClass.getDeclaredField(field);
        f.setAccessible(true);
        return f.get(source);
    }

}
