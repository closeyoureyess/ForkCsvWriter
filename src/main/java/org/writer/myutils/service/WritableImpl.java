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

    @Override
    public void writeToFile(List<?> data, String fileName) {
        dataActions.filterAnnotationInputList(data, CsvClass.class);
        Map<Object, Field[]> mapMapWithObjectAndArrayFields = dataActions.createMapWithObjectAndArrayFields(data);
        try {
            iterableObjectAndHisFields(mapMapWithObjectAndArrayFields, ZERO);
        } catch (IllegalAccessException | VariableNotExistsException e) {
            log.error("Возникла ошибка: " + e);
        }
        for (String s : listData) {
            log.info(s);
        }
    }

    private Optional<String> iterableObjectAndHisFields(Map<Object, Field[]> mapMapWithObjectAndArrayFields, Integer recursionFlag) throws IllegalAccessException, VariableNotExistsException {
        Optional<String> optionalS = Optional.empty();
        for (Map.Entry<Object, Field[]> entry : mapMapWithObjectAndArrayFields.entrySet()) {
            Object key = entry.getKey(); // Класс
            Field[] value = entry.getValue(); // Массив его полей
            for (Field field : value) { // Взять одно поле из массива, т.е одно поле из класса
                field.setAccessible(true);
                Class<?> typeClassField = field.getType();
                Object classFieldObject = field.get(key);
                optionalS = determineTypeField(typeClassField, classFieldObject, recursionFlag);
            }
            listData.set(listData.size() - 1, listData.get(listData.size() - 1) + CARRIAGE);
        }
        return optionalS;
    }

    private Optional<String> determineTypeField(Class<?> typeClassField, Object classFieldObject, Integer recursionFlag) throws IllegalAccessException, VariableNotExistsException {
        Optional<String[]> arrayWithItems;
        if (classFieldObject instanceof Number || classFieldObject instanceof String || classFieldObject instanceof Boolean ||
                classFieldObject instanceof Character || classFieldObject instanceof Enum) { // Объект - простой тип?
            if (recursionFlag.equals(ZERO)) {
                listData.add(classFieldObject + SEMICOLON);
            } else if (recursionFlag.equals(ONE)) {
                return Optional.of(classFieldObject.toString());
            }
        } else if (typeClassField.isArray()) { // Объект - массив?
            arrayWithItems = iterableItemsArrayField(classFieldObject);
            arrayWithItems.ifPresentOrElse(items -> listData.add(String.join(COMMA, items)), () -> listData.add(SPACE));
        } else if (typeClassField.isInterface()) { // Объект - интерфейс-коллекция?
            arrayWithItems = iterableItemsCollectionField(classFieldObject);
            arrayWithItems.ifPresentOrElse(items -> listData.add(String.join(COMMA, items)), () -> listData.add(SPACE));
        } else { // Объект - пользовательский класс?
            List<?> listWithObject = List.of(classFieldObject);
            iterableObjectAndHisFields(dataActions.createMapWithObjectAndArrayFields(listWithObject), recursionFlag);
        }
        return Optional.empty();
    }

    private Optional<String[]> iterableItemsCollectionField(Object collectionObject) throws IllegalAccessException, VariableNotExistsException {
        String[] elementsFromArray = null;
        if (collectionObject instanceof List<?> listFromField) {
            elementsFromArray = handleItemsListField(listFromField);
        } else if (collectionObject instanceof Set<?> setFromField) {
            elementsFromArray = handleItemsSetField(setFromField);
        } else if (collectionObject instanceof Map<?, ?> mapFromField) {
            elementsFromArray = handleItemsMapField(mapFromField);
        }
        if (elementsFromArray == null)
            return Optional.empty();
        return Optional.of(elementsFromArray);
    }

    private String[] handleItemsListField(List<?> listFromField) throws IllegalAccessException, VariableNotExistsException {
        Object objectFromField = listFromField.get(ZERO);
        String[] elementsFromArray;
        boolean typeMyList = detectArrayPrimitiveOrNotPrimitive(objectFromField, objectFromField.getClass());
        if (typeMyList) {
            elementsFromArray = listFromField.stream().map(Object::toString).toArray(String[]::new);
        } else {
            elementsFromArray = new String[listFromField.size()];
            for (int a = 0; a < elementsFromArray.length; a++) {
                Object arraysObject = listFromField.get(a); // Получить 1 объект из листа
                List<?> listWithListsObject = List.of(arraysObject); // Сделать List с объектом
                callRecursionForHandleObjectArray(elementsFromArray, listWithListsObject, a);
            }
            addCurlyBraceByStringArray(elementsFromArray);
        }
        return elementsFromArray;
    }

    private String[] handleItemsSetField(Set<?> setFromField) throws IllegalAccessException, VariableNotExistsException {
        Optional<?> optionalObject = setFromField.stream().findFirst();
        Object objectFromField = null;
        if (optionalObject.isPresent()) {
            objectFromField = optionalObject.get();
        } else {
            throw new VariableNotExistsException(LOCAL_VARIABLE_IS_EMPTY.getEnumDescription());
        }
        String[] elementsFromArray;
        boolean typeMySet = detectArrayPrimitiveOrNotPrimitive(objectFromField, objectFromField.getClass());
        if (typeMySet) {
            elementsFromArray = setFromField.stream().map(Object::toString).toArray(String[]::new);
        } else {
            elementsFromArray = new String[setFromField.size()];
            List<?> listFromField = setFromField.stream().toList();
            for (int a = 0; a < elementsFromArray.length; a++) {
                Object arraysObject = listFromField.get(a); // Получить 1 объект из листа
                List<?> listWithListsObject = List.of(arraysObject); // Сделать List с объектом
                callRecursionForHandleObjectArray(elementsFromArray, listWithListsObject, a);
            }
            addCurlyBraceByStringArray(elementsFromArray);
        }
        return elementsFromArray;
    }

    private String[] handleItemsMapField(Map<?, ?> mapFromField) throws IllegalAccessException, VariableNotExistsException {
        int counter = 0;
        Object key;
        Object value;
        String[] elementsFromArray;
        String[] localStringArray;
        String[] glueKeyValueStringArray = new String[mapFromField.size()];
        for (Map.Entry<?, ?> mapEntry : mapFromField.entrySet()) {
            key = mapEntry.getKey();
            value = mapEntry.getValue();
            List<?> listWithKeys = List.of(key);
            List<?> listWithValues = List.of(value);
            elementsFromArray = handleItemsListField(listWithKeys);
            glueKeyValueStringArray[counter] = elementsFromArray[0];
            localStringArray = handleItemsListField(listWithValues);
            glueKeyValueStringArray[counter + 1] = localStringArray[1];
            counter++;
        }
        return glueKeyValueStringArray;
    }

    private Optional<String[]> iterableItemsArrayField(Object arrayObject) throws IllegalAccessException, VariableNotExistsException {
        int lengthUnknownArray = Array.getLength(arrayObject); // Получить массив из Object
        if (lengthUnknownArray != 0) { // Если массив не пустой, то вычислить, примитивный ли это массив или нет\
            String[] elementsFromArray = new String[lengthUnknownArray];
            Object elementArrayFromArray = Array.get(arrayObject, 0);
            boolean typeMyArray = detectArrayPrimitiveOrNotPrimitive(elementArrayFromArray, elementArrayFromArray.getClass()); // Массив из объектов или примитивов?
            if (typeMyArray) { // Примитивный массив
                for (int a = 0; a < elementsFromArray.length; a++) {
                    elementsFromArray[a] = Array.get(arrayObject, a).toString();
                }
            } else { //Массив объектов
                for (int a = 0; a < elementsFromArray.length; a++) {
                    Object arraysObject = Array.get(arrayObject, a); // Получить 1 объект из массива
                    List<?> listWithArraysObject = List.of(arraysObject); // Сделать List с объектом
                    callRecursionForHandleObjectArray(elementsFromArray, listWithArraysObject, a); // ???
                }
                addCurlyBraceByStringArray(elementsFromArray);
            }
            return Optional.of(elementsFromArray);
        } else {
            return Optional.empty();
        }
    }

    private void callRecursionForHandleObjectArray(String[] elementsFromArray, List<?> listWithObjectFromArrayOrList, int countExternalCycle) throws IllegalAccessException, VariableNotExistsException {
        Map<Object, Field[]> mapMapWithObjectAndArrayFields = dataActions.createMapWithObjectAndArrayFields(listWithObjectFromArrayOrList); // Сделать Map с объектом и массивом его полей
        Optional<String> optionalS = iterableObjectAndHisFields(mapMapWithObjectAndArrayFields, ONE); // Вызвать рекурсию, чтобы разобрать каждый объект
        optionalS.ifPresent(s -> elementsFromArray[countExternalCycle] = s);
    }

    private void addCurlyBraceByStringArray(String[] elementsFromArray) {
        elementsFromArray[ZERO] = CURLY_BRACE_OPEN + elementsFromArray[ZERO];
        elementsFromArray[elementsFromArray.length - 1] = elementsFromArray[elementsFromArray.length - 1] + CURLY_BRACE_CLOSE;
    }

    private boolean detectArrayPrimitiveOrNotPrimitive(Object arrayObject, Class<?> typeClassArrayObject) {
        return typeClassArrayObject.isPrimitive() || arrayObject instanceof String;
    }
}
