package org.casper.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class CasperDatabaseTest {

    private CasperDatabase casperDatabase;

    @Before
    public void setUp() throws Exception {
        casperDatabase = new CasperDatabase();
        casperDatabase.createCollection("cats");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void createCollection() throws Exception {
        casperDatabase.createCollection("dogs");
        CasperCollection<?> collection = casperDatabase.getCollection("dogs");
        assertNotNull(collection);
    }

    @Test
    public void getCollection() throws Exception {
        CasperCollection<?> collection = casperDatabase.getCollection("cats");
        assertNotNull(collection);
    }

    @Test
    public void dropCollection() throws Exception {
        casperDatabase.dropCollection("cats");
        assertNull(casperDatabase.getCollection("cats"));
    }

    @Test
    public void save() throws Exception {
        ModelA a = new ModelA("frank");
        casperDatabase.save("cats", a);
        assertEquals(casperDatabase.count("cats"), 1);
        casperDatabase.save("cats", a);
        assertEquals(casperDatabase.count("cats"), 1);

        ModelA b = new ModelA("herbert");
        casperDatabase.save("cats", b);
        assertEquals(casperDatabase.count("cats"), 2);
    }

    @Test
    public void remove() throws Exception {
        ModelA a = new ModelA("frank");
        ModelA b = new ModelA("herbert");
        casperDatabase.save("cats", a);
        casperDatabase.save("cats", b);
        casperDatabase.remove("cats", a);

        assertEquals(casperDatabase.count("cats"), 1);
        // TODO verify the correct record?
    }

    @Test
    public void delete() throws Exception {
        ModelA a = new ModelA("frank");
        ModelA b = new ModelA("herbert");
        casperDatabase.save("cats", a);
        casperDatabase.save("cats", b);
        casperDatabase.delete("cats", a);

        assertEquals(casperDatabase.count("cats"), 1);
        // TODO verify the correct record?
    }

    @Test
    public void findAll() throws Exception {

    }

    @Test
    public void findOne() throws Exception {

    }

    @Test
    public void find() throws Exception {

    }

    @Test
    public void findOne1() throws Exception {

    }

    @Test
    public void count() throws Exception {

    }

    private class ModelA {
        private String name;

        public ModelA(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}