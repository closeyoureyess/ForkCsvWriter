package org.writer;

import net.datafaker.Faker;
import net.datafaker.providers.base.Lorem;
import net.datafaker.providers.base.Name;
import net.datafaker.providers.base.Number;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.writer.model.fortest.SimpleObjectPrimitArrayCollect;
import org.writer.model.fortest.SimpleObjectWithArrayObject;
import org.writer.model.fortest.SimpleObjectWithString;
import org.writer.model.Student;
import org.writer.model.fortest.StudentNestedObject;
import org.writer.model.fortest.array.StudentArrayObjectWithNestedArrayObject;
import org.writer.model.fortest.array.StudentArrayObjects;
import org.writer.model.fortest.array.StudentArrayPrimitive;
import org.writer.model.fortest.collection.*;
import org.writer.myutils.service.WritableImpl;

import java.util.*;

class WritableImplTest {

    private WritableImpl writableImpl;
    private Faker faker;
    private Name name;
    private Number number;
    private Lorem lorem;

    @BeforeEach
    void setUp() {
        writableImpl = new WritableImpl();
        faker = new Faker();
        number = faker.number();
        name = faker.name();
        lorem = faker.lorem();
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с List<String> внутри")
    void writeToFileTest_ListString() {
        List<Student> list = new LinkedList<>();
        Student student = Student.builder().name(faker.name().firstName()).score(List.of(faker.name().firstName(), faker.name().firstName())).build();
        Student student2 = Student.builder().name(faker.name().firstName()).score(List.of(faker.name().firstName(), faker.name().firstName())).build();
        list.add(student);
        list.add(student2);
        String fileName = faker.lorem().word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с List<SimpleObjectWithString> внутри(кастомный класс с одним полем String внутри)")
    void writeToFileTest_ListSimpleObject() {
        List<StudentListObject> list = new LinkedList<>();
        SimpleObjectWithString simpleObjectWithString = new SimpleObjectWithString(name.firstName());
        StudentListObject studentListObject = StudentListObject.builder().name(name.firstName())
                .score(List.of(simpleObjectWithString)).build();
        list.add(studentListObject);
        StudentListObject studentListObject2 = StudentListObject.builder().name(name.firstName())
                .score(List.of(simpleObjectWithString)).build();
        list.add(studentListObject2);
        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с примитивным массивом int[] внутри")
    void writeToFileTest_arrayPrimitive() {
        List<StudentArrayPrimitive> list = new LinkedList<>();
        StudentArrayPrimitive studentArrayPrimitive =
                StudentArrayPrimitive.builder()
                        .arrayPrimitive(new int[]{number.randomDigit(), number.randomDigit(), number.randomDigit(), number.randomDigit()})
                        .name(name.firstName())
                        .build();
        list.add(studentArrayPrimitive);
        StudentArrayPrimitive studentArrayPrimitive2 =
                StudentArrayPrimitive.builder()
                        .arrayPrimitive(new int[]{number.randomDigit(), number.randomDigit(), number.randomDigit(), number.randomDigit()})
                        .name(name.firstName())
                        .build();
        list.add(studentArrayPrimitive2);
        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с массивом объектов SimpleObjectWithString[] внутри")
    void writeToFileTest_arrayObjects() {
        List<StudentArrayObjects> list = new LinkedList<>();
        SimpleObjectWithString simpleObjectWithString = SimpleObjectWithString.builder().name(name.firstName()).build();
        StudentArrayObjects studentArrayObjects = StudentArrayObjects.builder()
                .name(name.firstName())
                .arraySimpleObjectWithStrings(new SimpleObjectWithString[]{simpleObjectWithString, simpleObjectWithString, simpleObjectWithString, simpleObjectWithString, simpleObjectWithString})
                .build();
        list.add(studentArrayObjects);
        StudentArrayObjects studentArrayObjects2 = StudentArrayObjects.builder()
                .name(name.firstName())
                .arraySimpleObjectWithStrings(new SimpleObjectWithString[]{simpleObjectWithString, simpleObjectWithString, simpleObjectWithString, simpleObjectWithString, simpleObjectWithString})
                .build();
        list.add(studentArrayObjects2);
        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с массивом объектов SimpleObjectWithArrayObject[] внутри и " +
            "внутри у класса, который обернут в массив, есть примитивный массив")
    void writeToFileTest_arrayObjectsWithArrayPrimitiveObject() {
        List<StudentArrayObjectWithNestedArrayObject> list = new LinkedList<>();
        SimpleObjectWithArrayObject simpleObjectWithArrayObject = SimpleObjectWithArrayObject.builder()
                .name(name.firstName())
                .arraySimpleObjectWithStrings(new int[] {number.randomDigit(), number.randomDigit(), number.randomDigit()})
                .build();
        StudentArrayObjectWithNestedArrayObject studentArrayObjectWithNestedArrayObject = StudentArrayObjectWithNestedArrayObject.builder()
                .name(name.firstName())
                .arraySimpleObjectWithStrings(new SimpleObjectWithArrayObject[] {simpleObjectWithArrayObject, simpleObjectWithArrayObject})
                .build();
        list.add(studentArrayObjectWithNestedArrayObject);
        StudentArrayObjectWithNestedArrayObject studentArrayObjectWithNestedArrayObject2 = StudentArrayObjectWithNestedArrayObject.builder()
                .name(name.firstName())
                .arraySimpleObjectWithStrings(new SimpleObjectWithArrayObject[] {simpleObjectWithArrayObject, simpleObjectWithArrayObject})
                .build();
        list.add(studentArrayObjectWithNestedArrayObject2);
        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с Map внутри, у которого ключ и значение - это объекты")
    void writeToFileTest_mapWithObject() {
        List<StudentMapObject> list = new LinkedList<>();
        SimpleObjectPrimitArrayCollect simpleObjectPrimitArrayCollect = SimpleObjectPrimitArrayCollect.builder().name("ПЕРВЫЙ ОБЪЕКТ")
                .age(new int[] {number.randomDigit(), number.randomDigit()})
                .list(List.of("ЛИСТ 1", name.firstName(), name.firstName()))
                .build();
        SimpleObjectPrimitArrayCollect simpleObjectPrimitArrayCollect2 = SimpleObjectPrimitArrayCollect.builder().name("ВТОРОЙ ОБЪЕКТ")
                .age(new int[] {number.randomDigit(), number.randomDigit()})
                .list(List.of("ЛИСТ 2", name.firstName(), name.firstName()))
                .build();
        list.add(new StudentMapObject("ПЕРВЫЙ В ЛИСТЕ", Map.of(simpleObjectPrimitArrayCollect,simpleObjectPrimitArrayCollect2)));
        list.add(new StudentMapObject("ВТОРОЙ В ЛИСТЕ", Map.of(simpleObjectPrimitArrayCollect,simpleObjectPrimitArrayCollect2)));

        SimpleObjectPrimitArrayCollect simpleObjectPrimitArrayCollect3 = SimpleObjectPrimitArrayCollect.builder().name("ТРЕТИЙ ОБЪЕКТ")
                .age(new int[] {number.randomDigit(), number.randomDigit()})
                .list(List.of("ЛИСТ 3", name.firstName(), name.firstName()))
                .build();
        SimpleObjectPrimitArrayCollect simpleObjectPrimitArrayCollect4 = SimpleObjectPrimitArrayCollect.builder().name("ЧЕТВЕРТЫЙ ОБЪЕКТ")
                .age(new int[] {number.randomDigit(), number.randomDigit()})
                .list(List.of("ЛИСТ 4", name.firstName(), name.firstName()))
                .build();
        list.add(new StudentMapObject("ТРЕТИЙ В ЛИСТЕ", Map.of(simpleObjectPrimitArrayCollect3,simpleObjectPrimitArrayCollect4)));
        list.add(new StudentMapObject("ЧЕТВЕРТЫЙ В ЛИСТЕ", Map.of(simpleObjectPrimitArrayCollect3,simpleObjectPrimitArrayCollect4)));

        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с Map внутри, у которого ключ - объект, значение - это примитив")
    void writeToFileTest_mapWithObjectKeyAndValuePrimitive() {
        List<StudentMapKeyObjectAndValuePrim> list = new LinkedList<>();
        StudentMapKeyObjectAndValuePrim studentMapKeyObjectAndValuePrim = StudentMapKeyObjectAndValuePrim.builder()
                .name("Первый объект")
                .map(Map.of(new SimpleObjectWithString(name.firstName()), number.randomDigit()))
                .build();
        StudentMapKeyObjectAndValuePrim studentMapKeyObjectAndValuePrim2 = StudentMapKeyObjectAndValuePrim.builder()
                .name("Второй объект")
                .map(Map.of(new SimpleObjectWithString(name.firstName()), number.randomDigit()))
                .build();
        list.add(studentMapKeyObjectAndValuePrim);
        list.add(studentMapKeyObjectAndValuePrim2);

        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с Map внутри, у которого ключ - примитив, значение - это объект")
    void writeToFileTest_mapWithObjectValueAndKeyPrimitive() {
        List<StudentMapKeyPrimAndValueObject> list = new LinkedList<>();
        StudentMapKeyPrimAndValueObject studentMapKeyPrimAndValueObject = StudentMapKeyPrimAndValueObject.builder()
                .name("Первый объект")
                .map(Map.of(number.randomDigit(), new SimpleObjectWithString(name.firstName())))
                .build();
        StudentMapKeyPrimAndValueObject studentMapKeyPrimAndValueObject2 = StudentMapKeyPrimAndValueObject.builder()
                .name("Второй объект")
                .map(Map.of(number.randomDigit(), new SimpleObjectWithString(name.firstName())))
                .build();
        list.add(studentMapKeyPrimAndValueObject);
        list.add(studentMapKeyPrimAndValueObject2);

        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с Map внутри, у которой ключ и значение - примитивы")
    void writeToFileTest_mapWithPrimValueAndKeyPrimitive() {
        List<StudentMapKeyAndValuePrim> list = new LinkedList<>();
        StudentMapKeyAndValuePrim studentMapKeyAndValuePrim = StudentMapKeyAndValuePrim.builder()
                .name("Первый объект")
                .map(Map.of(number.randomDigit(), number.randomDigit()))
                .build();
        StudentMapKeyAndValuePrim studentMapKeyAndValuePrim2 = StudentMapKeyAndValuePrim.builder()
                .name("Второй объект")
                .map(Map.of(number.randomDigit(), number.randomDigit()))
                .build();
        list.add(studentMapKeyAndValuePrim);
        list.add(studentMapKeyAndValuePrim2);

        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с Set внутри, который заполнен примитивами")
    void writeToFileTest_setPrimitive() {
        List<StudentSetObjectPrim> list = new LinkedList<>();
        StudentSetObjectPrim studentSetObjectPrim = StudentSetObjectPrim.builder()
                .name("Первый объект")
                .score(Set.of(number.randomDigit(), number.randomDigit()))
                .build();
        StudentSetObjectPrim studentSetObjectPrim2 = StudentSetObjectPrim.builder()
                .name("Второй объект")
                .score(Set.of(number.randomDigit(), number.randomDigit()))
                .build();
        list.add(studentSetObjectPrim);
        list.add(studentSetObjectPrim2);

        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс с Set внутри, который заполнен объектами")
    void writeToFileTest_setObject() {
        List<StudentSetObject> list = new LinkedList<>();
        StudentSetObject studentSetObject = StudentSetObject.builder()
                .name("Первый объект")
                .score(Set.of(new SimpleObjectWithString(name.firstName()), new SimpleObjectWithString(name.firstName())))
                .build();
        StudentSetObject studentSetObject2 = StudentSetObject.builder()
                .name("Второй объект")
                .score(Set.of(new SimpleObjectWithString(name.firstName()), new SimpleObjectWithString(name.firstName())))
                .build();
        list.add(studentSetObject);
        list.add(studentSetObject2);

        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }

    @Test
    @DisplayName("Проверка writeToFile(), если передается класс со вложенным объектом внутри")
    void writeToFileTest_nestedObject() {
        List<StudentNestedObject> list = new LinkedList<>();
        StudentNestedObject studentNestedObject = StudentNestedObject.builder()
                .name("Первый объект")
                .simpleObjectWithString(new SimpleObjectWithString(name.firstName()))
                .build();
        StudentNestedObject studentNestedObject2 = StudentNestedObject.builder()
                .name("Первый объект")
                .simpleObjectWithString(new SimpleObjectWithString(name.firstName()))
                .build();
        list.add(studentNestedObject);
        list.add(studentNestedObject2);

        String fileName = lorem.word() + System.currentTimeMillis() + ".csv";

        Assertions.assertDoesNotThrow(() -> writableImpl.writeToFile(list, fileName));
    }
}
