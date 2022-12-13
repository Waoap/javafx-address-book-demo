package com.waoap.addressbook;

import com.waoap.addressbook.utils.PinYinComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class AddressBook {
    private final PriorityQueue<String> names = new PriorityQueue<>(new PinYinComparator());

    private final HashMap<String, Person> names2contacts = new HashMap<>();

    private final ArrayList<Person> contacts = new ArrayList<>();

    public PriorityQueue<String> getNames() {
        return names;
    }

    public HashMap<String, Person> getNames2contacts() {
        return names2contacts;
    }

    public ArrayList<Person> getContacts() {
        return contacts;
    }

    /**
     * 添加联系人
     *
     * @param contact 要添加的联系人
     */
    public void add(Person contact) {
        names.offer(contact.getName());
        names2contacts.put(contact.getName(), contact);
        contacts.add(contact);
    }
}
