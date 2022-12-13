package com.waoap.addressbook;

import java.util.List;

public class Person {
    private String name;
    private List<String> telephone;
    private String email;
    private String address;
    private String note;

    public Person(String name) {
        this.name = name;
        telephone = null;
        email = null;
        address = null;
        note = null;
    }

    public Person(String name, List<String> telephone, String email, String address, String note) {
        this.name = name;
        this.telephone = telephone;
        this.email = email;
        this.address = address;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTelephone() {
        return telephone;
    }

    public void setTelephone(List<String> telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
