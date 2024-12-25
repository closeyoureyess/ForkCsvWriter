package org.writer;

import org.writer.model.Months;
import org.writer.model.Person;
import org.writer.myutils.service.Writable;
import org.writer.myutils.service.WritableImpl;

import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Person> list = new LinkedList<>();
        list.add(new Person("Test", "Test", 2, Months.APRIL, 3));
        /*list.add(Months.APRIL);*/
        Writable writable = new WritableImpl();
        writable.writeToFile(list, "test");
        WritableImpl writable1 = new WritableImpl();
    }
}