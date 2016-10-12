package org.casper.query;

import org.casper.model.TestObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class ObjectMatcherTest {
    private TestObject testObject;
    private ObjectMatcher<TestObject> matcher;

    @Before
    public void setUp() throws Exception {
        testObject = new TestObject(1, "John", 30, "Fighting clowns 100% of the time");
        matcher = ObjectMatcher.from(testObject);
    }

    @Test
    public void from() throws Exception {
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
        matcher.eq("id", 1);
        assertTrue(matcher.isMatch());
        matcher.eq("id", 2);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void eq1() throws Exception {
        matcher.where("id").eq(1);
        assertTrue(matcher.isMatch());
        matcher.where("id").eq(2);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void neq() throws Exception {
        matcher.neq("id", 2);
        assertTrue(matcher.isMatch());
        matcher.neq("id", 1);
        assertFalse(matcher.isMatch());
    }

    @Test
    public void neq1() throws Exception {
        matcher.where("id").neq(2);
        assertTrue(matcher.isMatch());
        matcher.where("id").neq(1);
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
        matcher.where("name").like("%ohn");
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
    public void lt() throws Exception {

    }

    @Test
    public void lt1() throws Exception {

    }

    @Test
    public void gt() throws Exception {

    }

    @Test
    public void gt1() throws Exception {

    }

    @Test
    public void in() throws Exception {

    }

    @Test
    public void in1() throws Exception {

    }

    @Test
    public void in2() throws Exception {

    }

    @Test
    public void in3() throws Exception {

    }

    @Test
    public void between() throws Exception {

    }

    @Test
    public void between1() throws Exception {

    }

    @Test
    public void not() throws Exception {

    }

    @Test
    public void and() throws Exception {

    }

    @Test
    public void or() throws Exception {

    }

    @Test
    public void where() throws Exception {

    }

    @Test
    public void where1() throws Exception {

    }

    @Test
    public void and1() throws Exception {

    }

    @Test
    public void or1() throws Exception {

    }

}