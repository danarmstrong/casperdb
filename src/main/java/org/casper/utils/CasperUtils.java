package org.casper.utils;

import org.casper.exception.CasperException;

import java.lang.reflect.Field;


public class CasperUtils {
    public static <T> int compare(T t, String field, Object value, Mode mode) throws CasperException {
        Object o = getFieldValue(t, field);

        if (!o.getClass().equals(value.getClass())
                && mode != Mode.In && mode != Mode.Between) {
            throw new CasperException("Field and value type mismatch");
        }

        if (o instanceof Number && !value.getClass().isArray()) {
            Double fieldValue = ((Number) o).doubleValue();
            Double targetValue = ((Number) value).doubleValue();

            return fieldValue < targetValue ? -1 : fieldValue > targetValue ? 1 : 0;
        } else if (value.getClass().isArray()) {
            Object[] targetValues = (Object[]) value;
            for (Object v : targetValues) {
                if (o.equals(v))
                    return 0;
            }

            return -1;
        } else if (o instanceof String) {
            if (mode == Mode.IgnoreCase)
                return ((String) o).equalsIgnoreCase((String) value) ? 0 : -1;
            else if (mode == Mode.Regex)
                return ((String) o).matches((String) value) ? 0 : -1;
        }

        return o.equals(value) ? 0 : -1;
    }

    public static <T> int compare(T t, String field, Object value) throws CasperException {
        return compare(t, field, value, Mode.Exact);
    }


    public static <T> Object getFieldValue(T t, String field) throws CasperException {
        try {
            Class<?> cls = t.getClass();
            Field f = cls.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(t);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new CasperException(ex);
        }
    }

    public enum Mode {
        Exact, IgnoreCase, Regex,
        LessThan, GreaterThan,
        LessThanEqual, GreaterThanEqual,
        In, Between
    }
}
