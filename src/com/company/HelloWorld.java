package com.company;

/**
 * Scratchpad for testing
 */
class Person {
    private String name;

    public Person(String n) {
        super();
        this.name = n;
    }

    public void setName(String n) {
        this.name = n;
    }
}

class Student extends Person {
    public Student() {
        super("Student");
    }
}


public class HelloWorld {
    public static void main(String[] args) {
        Student s = new Student();
    }
}
