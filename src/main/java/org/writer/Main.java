package org.writer;

import org.writer.model.Months;
import org.writer.model.Student;
import org.writer.model.TestClass;
import org.writer.myutils.service.Writable;
import org.writer.myutils.service.WritableImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<TestClass> list = new LinkedList<>();

        list.add(new TestClass("hmmm1", "Test", 2, Months.APRIL,
                Map.of(new Student("Test1", List.of("Test1", "Test1", "Test1")), 1,
                        new Student("Test2", List.of("Test1", "Test1", "Test1")), 232323)));
        list.add(new TestClass("hmmm2", "Test", 2, Months.APRIL,
                Map.of(new Student("Test1", List.of("Test1", "Test1", "Test1")), 1,
                        new Student("Test2", List.of("Test1", "Test1", "Test1")), 232323)));
        list.add(new TestClass("hmmm3", "Test", 2, Months.APRIL,
                Map.of(new Student("Test1", List.of("Test1", "Test1", "Test1")), 1,
                        new Student("Test2", List.of("Test1", "Test1", "Test1")), 232323)));

        /*list.add(new TestClass("hmmm1", "Test", 2, Months.APRIL,
                new Student[] {new Student("Test1", List.of("Test1", "Test1", "Test1"))}));
        list.add(new TestClass("hmmm2", "Test", 2, Months.APRIL,
                new Student[] {new Student("Test1", List.of("Test1", "Test1", "Test1"))}));
        list.add(new TestClass("hmmm3", "Test", 2, Months.APRIL,
                new Student[] {new Student("Test1", List.of("Test1", "Test1", "Test1"))}));*/
        /*list.add(new TestClass("hmmm1", "Test", 2, Months.APRIL, 3, new int[] {1, 2, 3, 4, 5, 6},
                List.of(new Student("emm1", List.of("Test1", "Test1", "Test1")))));
        list.add(new TestClass("hmmm2", "Test", 2, Months.APRIL, 3, new int[] {1, 2, 3, 4, 5, 6},
                List.of(new Student("emm2", List.of("Test2", "Test1", "Test1")))));
        list.add(new TestClass("hmmm3", "Test", 2, Months.APRIL, 3, new int[] {1, 2, 3, 4, 5, 6},
                List.of(new Student("emm3", List.of("Test3", "Test1", "Test1")))));*/
        /*list.add(new TestClass("hmmm1", List.of(new Student("emm1", List.of("Test1", "Test1", "Test1")))));
        list.add(new TestClass("hmmm2", List.of(new Student("emm2", List.of("Test2", "Test1", "Test1")))));
        list.add(new TestClass("hmmm3", List.of(new Student("emm3", List.of("Test3", "Test1", "Test1")))));*/
        /*list.add(new TestClass("Test1", "Test", 2, Months.APRIL, 3, List.of(new Student("Test1", List.of("Test1", "Test1", "Test1")))));
        list.add(new TestClass("Test2", "Test", 2, Months.APRIL, 3, List.of(new Student("Test2", List.of("Test1", "Test1", "Test1")))));
        list.add(new TestClass("Test3", "Test", 2, Months.APRIL, 3, List.of(new Student("Test3", List.of("Test1", "Test1", "Test1")))));*/
        /*list.add(new TestClass("Test1", "Test", 2, Months.APRIL, 3, new Student("Test1", List.of("Test1", "Test1", "Test1"))));
        list.add(new TestClass("Test2", "Test", 2, Months.APRIL, 3, new Student("Test1", List.of("Test1", "Test1", "Test1"))));
        list.add(new TestClass("Test3", "Test", 2, Months.APRIL, 3, new Student("Test1", List.of("Test1", "Test1", "Test1"))));*/

        /*list.add(new Student("Test1", List.of("Test1", "Test1", "Test1")));
        list.add(new Student("Test2", List.of("Test1", "Test1", "Test1")));
        list.add(new Student("Test3", List.of("Test1", "Test1", "Test1")));*/

        /*list.add(new Person("Test1", "Test", 2, Months.APRIL, 3));
        list.add(new Person("Test2", "Test", 2, Months.APRIL, 3));
        list.add(new Person("Test3", "Test", 2, Months.APRIL, 3));*/
        Writable writable = new WritableImpl();
        writable.writeToFile(list, "test");
    }
}