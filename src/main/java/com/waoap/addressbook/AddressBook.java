package com.waoap.addressbook;

import com.waoap.addressbook.utils.PinYinComparator;

import java.util.HashMap;
import java.util.PriorityQueue;

public class AddressBook {
    /**
     * 用优先队列来实现联系人按拼音首字母顺序排序
     */
    private final PriorityQueue<String> names = new PriorityQueue<>(new PinYinComparator());

    /**
     * 联系人姓名与联系人实例的哈希表
     */
    private final HashMap<String, Person> names2contacts = new HashMap<>();

    public PriorityQueue<String> getNames() {
        return names;
    }

    public HashMap<String, Person> getNames2contacts() {
        return names2contacts;
    }

    /**
     * 添加联系人
     *
     * @param contact 要添加的联系人
     */
    public void add(Person contact) {
        names.offer(contact.getName());
        names2contacts.put(contact.getName(), contact);
    }

    public void delete(Person contact) {
        names.remove(contact.getName());
        names2contacts.remove(contact.getName());
    }

    public void modify(Person oldContact, Person newContact) {
        delete(oldContact);
        add(newContact);
    }
}
