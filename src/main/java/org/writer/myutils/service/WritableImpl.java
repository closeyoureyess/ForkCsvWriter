package org.writer.myutils.service;

import lombok.extern.slf4j.Slf4j;
import org.writer.myutils.actions.DataActions;
import org.writer.myutils.actions.DataActionsImpl;
import org.writer.myutils.annotations.CsvClass;
import org.writer.myutils.other.exceptions.VariableNotExistsException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static org.writer.myutils.other.ConstantsClass.*;
import static org.writer.myutils.other.exceptions.DescriptionUserExeption.LOCAL_VARIABLE_IS_EMPTY;

@Slf4j
public class WritableImpl implements Writable {

    private DataActions dataActions = new DataActionsImpl();

    private List<String> listData = new ArrayList<>();
    private List<String> localListDataForMap = new ArrayList<>();

    @Override
    public void writeToFile(List<?> data, String fileName) {
        log.info("start");
        dataActions.filterAnnotationInputList(data, CsvClass.class);
        Map<Object, Field[]> mapMapWithObjectAndArrayFields = dataActions.createMapWithObjectAndArrayFields(data);
        try {
            iterableObjectAndHisFields(mapMapWithObjectAndArrayFields, ZERO);
        } catch (IllegalAccessException | VariableNotExistsException e) {
            log.error("Возникла ошибка: " + e);
        }
        for (String s : listData) {
            System.out.println(s);
        }
    }

    /**
     * Метод, позволяющий перебрать объект и его поля, в т.ч содержимое этих полей
     *
     * @param mapMapWithObjectAndArrayFields Map с объектами и полями этого объекта
     * @param recursionFlag                  Локальный флаг для рекурсии
     * @throws IllegalAccessException
     * @throws VariableNotExistsException
     */
    private void iterableObjectAndHisFields(Map<Object, Field[]> mapMapWithObjectAndArrayFields, Integer recursionFlag) throws IllegalAccessException, VariableNotExistsException {
        Optional<String> optionalS = Optional.empty();
        for (Map.Entry<Object, Field[]> entry : mapMapWithObjectAndArrayFields.entrySet()) {
            Object key = entry.getKey(); // Класс
            Field[] value = entry.getValue(); // Массив его полей
            for (Field field : value) { // Взять одно поле из массива, т.е одно поле из класса
                field.setAccessible(true);
                Class<?> typeClassField = field.getType();
                Object classFieldObject = field.get(key);
                determineTypeField(typeClassField, classFieldObject, recursionFlag); // Какого типа это поле?
            }
            if (recursionFlag.equals(ZERO)) {
                addSemicolonOrCarriageForResultRow();
            }
        }
    }

    private void addSemicolonOrCarriageForResultRow() {
        String lastElement = listData.get(listData.size() - 1);
        if (lastElement != null && lastElement.contains(SEMICOLON)) {
            listData.set(listData.size() - 1, lastElement + CARRIAGE);
        } else if (lastElement != null && !lastElement.contains(SEMICOLON)) {
            listData.set(listData.size() - 1, lastElement + SEMICOLON + CARRIAGE);
        }
    }

    /**
     * Метод, позволяющий определить тип переданного поля
     *
     * @param typeClassField   объект Class для удобной работы с типами
     * @param classFieldObject Сам объект класса из поля
     * @param recursionFlag    Флаг для рекурсии
     * @throws IllegalAccessException
     * @throws VariableNotExistsException
     */
    private void determineTypeField(Class<?> typeClassField, Object classFieldObject, Integer recursionFlag) throws IllegalAccessException, VariableNotExistsException {
        Optional<String[]> arrayWithItems;
        if (classFieldObject instanceof Number || classFieldObject instanceof String || classFieldObject instanceof Boolean ||
                classFieldObject instanceof Character || classFieldObject instanceof Enum) { // Объект - простой тип?
            if (recursionFlag.equals(ZERO)) {
                listData.add(classFieldObject + SEMICOLON);
            } else if (recursionFlag.equals(ONE)) { // Если перебирается элемент из Map
                localListDataForMap.add(classFieldObject.toString());
            }
        } else if (typeClassField.isArray()) { // Объект - массив?
            arrayWithItems = iterableItemsArrayField(classFieldObject, recursionFlag); // Определить тип объектов в массиве, перебрать массив
            arrayWithItems.ifPresent(items -> {
                String resultRow = String.join(COMMA, items) + SEMICOLON;
                if (recursionFlag.equals(ZERO)) {
                    listData.add(resultRow);
                } else if (recursionFlag.equals(ONE)) { // Если коллекция - Map
                    localListDataForMap.add(resultRow);
                }
            });
        } else if (typeClassField.isInterface()) { // Объект - интерфейс-коллекция?
            arrayWithItems = iterableItemsCollectionField(classFieldObject, recursionFlag); // Определить тип объектов в коллекции, перебрать коллекцию
            arrayWithItems.ifPresent(strings -> {
                String resultRow = String.join(COMMA, strings);
                if (recursionFlag.equals(ZERO)) {
                    listData.add(resultRow);
                } else if (recursionFlag.equals(ONE)) { // Если коллекция - Map
                    localListDataForMap.add(resultRow);
                }
            });
        } else { // Объект - пользовательский класс?
            List<?> listWithObject = List.of(classFieldObject); // Поместить объект в List
            iterableObjectAndHisFields(dataActions.createMapWithObjectAndArrayFields(listWithObject), recursionFlag); // Начать перебор полей объекта
        }
    }

    private Optional<String[]> iterableItemsCollectionField(Object collectionObject, Integer recursionFlag) throws
            IllegalAccessException, VariableNotExistsException {
        String[] elementsFromArray = null;
        if (collectionObject instanceof List<?> listFromField) { // Объект - коллекция List?
            elementsFromArray = handleItemsListField(listFromField, recursionFlag);
        } else if (collectionObject instanceof Set<?> setFromField) { // Объект - коллекция Set?
            elementsFromArray = handleItemsSetField(setFromField, recursionFlag);
        } else if (collectionObject instanceof Map<?, ?> mapFromField) { // Объект - коллекция Map?
            elementsFromArray = handleItemsMapField(mapFromField);
        }
        if (elementsFromArray == null)
            return Optional.empty();
        return Optional.of(elementsFromArray);
    }

    private String[] handleItemsListField(List<?> listFromField, Integer recursionFlag) throws
            IllegalAccessException, VariableNotExistsException {
        Object objectFromField = listFromField.get(ZERO);
        String[] elementsFromArray = null;
        boolean typeMyList = detectArrayPrimitiveOrNotPrimitive(objectFromField);
        if (typeMyList) {
            elementsFromArray = listFromField.stream().map(Object::toString).toArray(String[]::new);
        } else {
            callRecursionForHandleObjectArray(listFromField, recursionFlag);
        }
        return elementsFromArray;
    }

    private String[] handleItemsSetField(Set<?> setFromField, Integer recursionFlag) throws
            IllegalAccessException, VariableNotExistsException {
        Optional<?> optionalObject = setFromField.stream().findFirst();
        Object objectFromField = null;
        if (optionalObject.isPresent()) {
            objectFromField = optionalObject.get();
        } else {
            throw new VariableNotExistsException(LOCAL_VARIABLE_IS_EMPTY.getEnumDescription());
        }
        String[] elementsFromArray;
        boolean typeMySet = detectArrayPrimitiveOrNotPrimitive(objectFromField);
        if (typeMySet) {
            elementsFromArray = setFromField.stream().map(Object::toString).toArray(String[]::new);
        } else {
            elementsFromArray = new String[setFromField.size()];
            List<?> listFromField = setFromField.stream().toList();
            for (int a = 0; a < elementsFromArray.length; a++) {
                Object arraysObject = listFromField.get(a); // Получить 1 объект из листа
                List<?> listWithListsObject = List.of(arraysObject); // Сделать List с объектом
                callRecursionForHandleObjectArray(listWithListsObject, recursionFlag);
            }
        }
        return elementsFromArray;
    }

    /**
     * Метод, позволяющий достать содержимое из Map, если простые типы -
     *
     * @param mapFromField Map, являющийся полем в классе
     * @return Массив строк с перебранными элементами массива
     * @throws IllegalAccessException
     * @throws VariableNotExistsException
     */
    private String[] handleItemsMapField(Map<?, ?> mapFromField) throws
            IllegalAccessException, VariableNotExistsException {
        Object key;
        Object value;
        for (Map.Entry<?, ?> mapEntry : mapFromField.entrySet()) {
            key = mapEntry.getKey();
            value = mapEntry.getValue();
            List<?> listWithKeys = List.of(key);
            List<?> listWithValues = List.of(value);
            handleItemsListField(listWithKeys, ONE); // Разобрать ключ(примитив или объект, если второе - пройтись по полям
            handleItemsListField(listWithValues, ONE); // Разобрать значение
        }
        String[] arrayWithGlueKeyValue = localListDataForMap.toArray(String[]::new); // Собранную в локальный List информацию вернуть в виде массива String[]
        localListDataForMap.clear();
        return arrayWithGlueKeyValue;
    }

    /**
     * Метод, позволяющий достать содержимое из массива(который является полем в классе), через рекурсию перебрать его, рез-ты внутри
     * рекурсии(если массив состоит из объектов, а не простых типов, будут добавены в List внутри рекурсии), если из простых типов
     *
     * @param arrayObject Массив, являющийся полем в классе
     * @return Массив строк с перебранными элементами массива
     * @throws IllegalAccessException
     * @throws VariableNotExistsException
     */
    private Optional<String[]> iterableItemsArrayField(Object arrayObject, Integer recursionFlag) throws
            IllegalAccessException, VariableNotExistsException {
        int lengthUnknownArray = Array.getLength(arrayObject); // Получить массив из Object
        if (lengthUnknownArray != 0) { // Если массив не пустой, то вычислить, примитивный ли это массив или нет
            String[] elementsFromArray = null;
            Object elementArrayFromArray = Array.get(arrayObject, 0);
            boolean typeMyArray = detectArrayPrimitiveOrNotPrimitive(elementArrayFromArray); // Массив из объектов или примитивов?
            if (typeMyArray) { // Примитивный массив
                elementsFromArray = new String[lengthUnknownArray];
                for (int a = 0; a < elementsFromArray.length; a++) {
                    elementsFromArray[a] = Array.get(arrayObject, a).toString();
                }
            } else { //Массив объектов
                for (int a = 0; a < lengthUnknownArray; a++) {
                    Object arraysObject = Array.get(arrayObject, a); // Получить 1 объект из массива
                    List<?> listWithArraysObject = List.of(arraysObject); // Сделать List с объектом
                    callRecursionForHandleObjectArray(listWithArraysObject, recursionFlag); // Разобрать объект, т.е его поля
                }
            }
            return Optional.ofNullable(elementsFromArray);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Метод, преобразующий переданную коллекци/массив в Map с объектами, полями этих объектов, и позволяющий далее
     * рекурсивно перебрать все элементы этой коллекции/массива из Map
     *
     * @param listWithObjectFromArrayOrList List с коллекцией/элементами из массива(эл-ты массива перебираются по одному, т.е в List всегда будет
     *                                      передан только 1 элемент)
     * @throws IllegalAccessException
     * @throws VariableNotExistsException
     */
    private void callRecursionForHandleObjectArray(List<?> listWithObjectFromArrayOrList, Integer recursionFlag) throws
            IllegalAccessException, VariableNotExistsException {
        Map<Object, Field[]> mapMapWithObjectAndArrayFields = dataActions.createMapWithObjectAndArrayFields(listWithObjectFromArrayOrList); // Сделать Map с объектом и массивом его полей
        iterableObjectAndHisFields(mapMapWithObjectAndArrayFields, recursionFlag); // Вызвать рекурсию, чтобы разобрать каждый объект
    }

    /**
     * Метод, позволяющий узнать, явлется ли переданный элемент коллекции простым типом, или это объект(если возвращается false)
     *
     * @param arrayObject Объект, который получили из коллекции или массива
     * @return true - объект простой тип, false - переданный Object является объектом, коллекция/массив состоят не из простых типов
     */
    private boolean detectArrayPrimitiveOrNotPrimitive(Object arrayObject) {
        return arrayObject instanceof Number || arrayObject instanceof String || arrayObject instanceof Boolean ||
                arrayObject instanceof Character || arrayObject instanceof Enum;
    }
}