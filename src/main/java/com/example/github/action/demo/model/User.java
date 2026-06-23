package com.example.github.action.demo.model;

public class User {

    private Long id;
    private String name;
    private String phoneNo;

    public User(Long id, String name, String phoneNo) {
        this.id = id;
        this.name = name;
        this.phoneNo = phoneNo;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

}