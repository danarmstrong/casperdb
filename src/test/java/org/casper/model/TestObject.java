package org.casper.model;


import org.casper.stereotype.CasperId;
import org.casper.stereotype.CasperIndexed;

public class TestObject {
    @CasperId
    private int id;
    @CasperIndexed
    private String name;
    private int age;
    private String job;

    public TestObject(int id, String name, int age, String job) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.job = job;
    }

    public TestObject(String name, int age, String job) {
        this.name = name;
        this.age = age;
        this.job = job;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getJob() {
        return job;
    }
}
