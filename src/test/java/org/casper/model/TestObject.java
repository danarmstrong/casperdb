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
    private Character gender;
    private Character rating;

    public TestObject(int id, String name, int age, String job, Character gender, Character rating) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.job = job;
        this.gender = gender;
        this.rating = rating;
    }

    public TestObject(String name, int age, String job, Character gender, Character rating) {
        this.name = name;
        this.age = age;
        this.job = job;
        this.gender = gender;
        this.rating = rating;
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

    public Character getGender() {
        return gender;
    }

    public Character getRating() {
        return rating;
    }
}
