package org.casper.query;

import org.casper.exception.CasperException;
import org.casper.model.TestObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class ObjectMatcherTest {
    private TestObject testObject;
    private ObjectMatcher<TestObject> matcher;

    @Before
    public void setUp() throws Exception {
        testObject = new TestObject(1, "John", 30, "Fighting clowns 100% of the time", 'm', 'a');
        matcher = ObjectMatcher.match(testObject);
    }

    @Test
    public void match() throws Exception {
        assertNotNull(matcher.getSource());
    }

    @Test
    public void getSource() throws Exception {
        assertNotNull(matcher.getSource());
    }

    @Test
    public void getSourceClass() throws Exception {
        assertEquals(matcher.getSourceClass(), testObject.getClass());
    }

    @Test
    public void isMatch() throws Exception {
        matcher.eq("id", 1);
        assertTrue(matcher.isMatch());
        matcher.eq("id", 2);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void eq() throws Exception {

        try {
            matcher.eq(1);
        } catch (CasperException ex) {
            assertEquals(ex.getMessage(), "Field is null");
        }

        matcher.eq("id", 1);
        assertTrue(matcher.isMatch());
        matcher.eq("id", 2);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void eq1() throws Exception {
        matcher.where("id");

        matcher.eq(1);
        assertTrue(matcher.isMatch());
        matcher.eq(2);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void neq() throws Exception {
        matcher.ne("id", 2);
        assertTrue(matcher.isMatch());
        matcher.ne("id", 1);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void neq1() throws Exception {
        matcher.where("id");

        matcher.ne(2);
        assertTrue(matcher.isMatch());
        matcher.ne(1);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void like() throws Exception {
        matcher.like("name", "%ohn");
        assertTrue(matcher.isMatch());
        matcher.like("name", "Joh%");
        assertTrue(matcher.isMatch());
        matcher.like("name", "%oh%");
        assertTrue(matcher.isMatch());
        matcher.like("name", "%John%");
        assertTrue(matcher.isMatch());
        matcher.like("job", "%\\%%");
        assertTrue(matcher.isMatch());
        matcher.like("name", "John");
        assertTrue(matcher.isMatch());
        matcher.like("name", "Fish");
        assertFalse(matcher.isMatch());
    }

    @Test
    public void like1() throws Exception {
        matcher.where("name");

        matcher.like("%ohn");
        assertTrue(matcher.isMatch());
        matcher.like("Joh%");
        assertTrue(matcher.isMatch());
        matcher.like("%oh%");
        assertTrue(matcher.isMatch());
        matcher.like("%John%");
        assertTrue(matcher.isMatch());
        matcher.like("%\\%%");
        assertTrue(matcher.isMatch());
        matcher.like("John");
        assertTrue(matcher.isMatch());
        matcher.like("Fish");
        assertFalse(matcher.isMatch());
    }

    @Test
    public void lt() throws Exception {
        matcher.lt("age", 40);
        assertTrue(matcher.isMatch());
        matcher.lt("age", 30);
        assertFalse(matcher.isMatch());
        matcher.lt("age", 20);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void lt1() throws Exception {
        matcher.where("age");

        matcher.lt(40);
        assertTrue(matcher.isMatch());
        matcher.lt(30);
        assertFalse(matcher.isMatch());
        matcher.lt(20);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void gt() throws Exception {
        matcher.gt("age", 40);
        assertFalse(matcher.isMatch());
        matcher.gt("age", 30);
        assertFalse(matcher.isMatch());
        matcher.gt("age", 20);
        assertTrue(matcher.isMatch());
    }

    @Test
    public void gt1() throws Exception {
        matcher.where("age");

        matcher.gt(40);
        assertFalse(matcher.isMatch());
        matcher.gt(30);
        assertFalse(matcher.isMatch());
        matcher.gt(20);
        assertTrue(matcher.isMatch());
    }

    @Test
    public void in() throws Exception {
        Integer[] arr = {4, 6, 2, 1, 3, 5};
        matcher.in("id", arr);
        assertTrue(matcher.isMatch());
        matcher.in("age", arr);
        assertFalse(matcher.isMatch());

        Character[] charArr = {'a', 'b', 'c', 'd'};
        matcher.in("gender", charArr);
        assertFalse(matcher.isMatch());
        matcher.in("rating", charArr);
        assertTrue(matcher.isMatch());
    }

    @Test
    public void in1() throws Exception {
        Integer[] arr = {4, 6, 2, 1, 3, 5};

        matcher.where("id");
        matcher.in(arr);
        assertTrue(matcher.isMatch());

        matcher.where("age");
        matcher.in(arr);
        assertFalse(matcher.isMatch());

        Character[] charArr = {'a', 'b', 'c', 'd'};
        matcher.where("gender");
        matcher.in(charArr);
        assertFalse(matcher.isMatch());

        matcher.where("rating");
        matcher.in(charArr);
        assertTrue(matcher.isMatch());
    }

    @Test
    public void in2() throws Exception {
        Integer[] arr = {4, 6, 2, 1, 3, 5};
        List<Integer> list = Arrays.asList(arr);

        matcher.in("id", list);
        assertTrue(matcher.isMatch());
        matcher.in("age", list);
        assertFalse(matcher.isMatch());

        Character[] charArr = {'a', 'b', 'c', 'd'};
        List<Character> charList = Arrays.asList(charArr);
        matcher.in("gender", charList);
        assertFalse(matcher.isMatch());
        matcher.in("rating", charList);
        assertTrue(matcher.isMatch());
    }

    @Test
    public void in3() throws Exception {
        Integer[] arr = {4, 6, 2, 1, 3, 5};
        List<Integer> list = Arrays.asList(arr);

        matcher.where("id");
        matcher.in(list);
        assertTrue(matcher.isMatch());

        matcher.where("age");
        matcher.in(list);
        assertFalse(matcher.isMatch());


        Character[] charArr = {'a', 'b', 'c', 'd'};
        List<Character> charList = Arrays.asList(charArr);
        matcher.where("gender");
        matcher.in(charList);
        assertFalse(matcher.isMatch());

        matcher.where("rating");
        matcher.in(charList);
        assertTrue(matcher.isMatch());
    }

    @Test
    public void between() throws Exception {
        matcher.between("age", 30, 40);
        assertTrue(matcher.isMatch());
        matcher.between("age", 20, 40);
        assertTrue(matcher.isMatch());
        matcher.between("age", 20, 25);
        assertFalse(matcher.isMatch());

        matcher.between("age", 40, 30);
        assertTrue(matcher.isMatch());
        matcher.between("age", 40, 20);
        assertTrue(matcher.isMatch());

    }

    @Test
    public void between1() throws Exception {
        matcher.where("age");

        matcher.between(30, 40);
        assertTrue(matcher.isMatch());
        matcher.between(20, 40);
        assertTrue(matcher.isMatch());
        matcher.between(20, 25);
        assertFalse(matcher.isMatch());

        matcher.between(40, 30);
        assertTrue(matcher.isMatch());
        matcher.between(40, 20);
        assertTrue(matcher.isMatch());
    }

    @Test
    public void not() throws Exception {
        matcher.where("age");
        matcher.not().eq(20);
        assertTrue(matcher.isMatch());
        matcher.not().eq(30);
        assertFalse(matcher.isMatch());

        matcher.where("name");
        matcher.not().like("%ohn");
        assertFalse(matcher.isMatch());
        matcher.not().like("B%b");
        assertTrue(matcher.isMatch());

        Integer[] arr = {12, 4, 20, 30, 99};

        matcher.where("age");
        matcher.not().in(arr);
        assertFalse(matcher.isMatch());

        matcher.where("id");
        matcher.not().in(arr);
        assertTrue(matcher.isMatch());

        matcher.where("age");
        matcher.not().between(4, 15);
        assertTrue(matcher.isMatch());
        matcher.not().between(25, 40);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void and() throws Exception {

    }

    @Test
    public void and1() throws Exception {

    }

    @Test
    public void or() throws Exception {

    }

    @Test
    public void or1() throws Exception {

    }

    @Test
    public void where() throws Exception {
        assertNull(matcher.getField());
        matcher.where("age");
        assertEquals(matcher.getField(), "age");
        matcher.eq(30);
        assertEquals(matcher.getField(), "age");

        matcher.where("id");
        assertEquals(matcher.getField(), "id");
    }

    @Test
    public void where1() throws Exception {
        assertNull(matcher.getField());
        matcher.where();
        assertNull(matcher.getField());

        matcher.where("age");
        assertEquals(matcher.getField(), "age");
        matcher.where();
        assertEquals(matcher.getField(), "age");

    }

}