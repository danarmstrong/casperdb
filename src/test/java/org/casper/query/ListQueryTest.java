package org.casper.query;

import org.casper.model.TestObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class ListQueryTest {
    List<TestObject> list;
    ListQuery<List<TestObject>> query;

    @Before
    public void setUp() throws Exception {
        list = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            TestObject t = new TestObject(i, "Name" + Integer.toString(i), i + 20, "Digging", 'm', 'a');
            list.add(t);
        }

        query = ListQuery.from(list);
    }

    @Test
    public void from() throws Exception {

    }

    @Test
    public void eq() throws Exception {

    }

    @Test
    public void eq1() throws Exception {

    }

    @Test
    public void ne() throws Exception {

    }

    @Test
    public void ne1() throws Exception {

    }

    @Test
    public void lg() throws Exception {

    }

    @Test
    public void lg1() throws Exception {

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
    public void le() throws Exception {

    }

    @Test
    public void le1() throws Exception {

    }

    @Test
    public void ge() throws Exception {

    }

    @Test
    public void ge1() throws Exception {

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
    public void like() throws Exception {

    }

    @Test
    public void like1() throws Exception {

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
    public void not() throws Exception {

    }

    @Test
    public void where() throws Exception {

    }

    @Test
    public void where1() throws Exception {

    }

    @Test
    public void limit() throws Exception {

    }

    @Test
    public void execute() throws Exception {

    }

    @Test
    public void toString1() throws Exception {
        String q = query.where("name").like("Name%").and("age").gt(0).or("job").eq("Digging").toString();
        System.out.println(q);
    }

}