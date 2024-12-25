package org.writer.myutils.service;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.writer.myutils.actions.DataActions;
import org.writer.myutils.actions.DataActionsImpl;
import org.writer.myutils.annotations.CsvClass;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.writer.myutils.other.ConstantsClass.*;

@Slf4j
public class WritableImpl implements Writable {

    private DataActions dataActions = new DataActionsImpl();

    @Override
    public void writeToFile(List<?> data, String fileName) {
        dataActions.filterAnnotationInputList(data, CsvClass.class);
        getFieldsObject(data, fileName, ONE);

    }

    private List<String> getFieldsObject(List<?> objectsForCsv, String fileName, Integer flag) {
        Map<Object, Field[]> mapWithFieldsObjects = dataActions.createMapWithObjectAndArrayFields(objectsForCsv);
        // 2
        parseMap(mapWithFieldsObjects, fileName, flag);
        return null;
    }

    private List<String> parseMap(Map<Object, Field[]> mapWithFieldsObjects, String fileName, Integer flag) {
        List<String> rowForSaveToCsv = new ArrayList<>();
        for (Map.Entry<Object, Field[]> entry : mapWithFieldsObjects.entrySet()) {
            Object key = entry.getKey();
            Field[] value = entry.getValue();
            rowForSaveToCsv = workWithFieldsArray(key, value, fileName);
            if (flag != null) {
                writeAppendedInfoToFile(rowForSaveToCsv, fileName);
            }
        }
        return rowForSaveToCsv;
    }

    private List<String> workWithFieldsArray(Object key, Field[] value, String fileName) {
        List<String> rowForSaveToCsv = new ArrayList<>();
        try {
            for (int i = 0; i < value.length; i++) {
                Field field = value[i];
                field.setAccessible(true);
                Object fieldInObject = field.get(key);
                Class<?> fieldClassType = field.getType();
                thisFieldIsArrayOrNot(fieldClassType, fieldInObject, fileName, rowForSaveToCsv);
                thisFieldIsPrimitiveOrNot(fieldClassType, fieldInObject, rowForSaveToCsv);
                thisFieldIsEnumOrNot(fieldClassType, fieldInObject, rowForSaveToCsv);
                thisFieldIsInterfaceOrNot(fieldClassType, fieldInObject, fileName, rowForSaveToCsv);
            }
            // Чекнуть последний элемент, убрать точку с запятой
        } catch (IllegalAccessException e) {
            log.error("Возникла ошибка: " + e);
        }
    }

    private void thisFieldIsInterfaceOrNot(Class<?> fieldClassType, Object fieldInObject, String fileName, List<String> rowForSaveToCsv) {
        if (fieldClassType.isInterface()) {
            switch (fieldInObject) {
                case List<?> list -> { // Это List?
                    List<?> resultList = (List<?>) fieldInObject;
                    fieldInObject = resultList.getFirst();
                    fieldClassType = fieldInObject.getClass();
                    boolean arrayOrNot = thisFieldIsArrayOrNot(fieldClassType, fieldInObject, fileName, null);
                    boolean primitiveOrNot = thisFieldIsPrimitiveOrNot(fieldClassType, fieldInObject, null);
                    boolean enumOrNot = thisFieldIsEnumOrNot(fieldClassType, fieldInObject, null);
                    if (!arrayOrNot && !primitiveOrNot && !enumOrNot) {
                        /*resultList.forEach((e) ->);*/
                    }
                }
                case Map<?, ?> map -> { // Это Map?
                    Map<?, ?> resultMap = (Map<?, ?>) fieldInObject;
                }
                case Set<?> set -> { // Это Map?
                    Set<?> resultSet = (Set<?>) fieldInObject;
                }
                default -> rowForSaveToCsv.add(fieldInObject.toString());
            }
        }
    }

    private boolean thisFieldIsEnumOrNot(Class<?> fieldClassType, Object fieldInObject, List<String> rowForSaveToCsv) {
        if (fieldClassType != null && fieldClassType.isEnum()) {
            if (rowForSaveToCsv != null) {
                Enum<?> enumValue = (Enum<?>) fieldInObject;
                rowForSaveToCsv.add(enumValue.name());
            }
            return true;
        }
        return false;
    }

    private boolean thisFieldIsPrimitiveOrNot(Class<?> fieldClassType, Object fieldInObject, List<String> rowForSaveToCsv) {
        if ( (fieldClassType != null && fieldClassType.isPrimitive()) || fieldInObject instanceof String) {
            if (rowForSaveToCsv != null) {
                rowForSaveToCsv.add(fieldInObject.toString());
            }
            return true;
        }
        return false;
    }

    private boolean thisFieldIsArrayOrNot(Class<?> fieldClassType, Object fieldInObject, String fileName, List<String> rowForSaveToCsv) {
        if (fieldClassType != null && fieldClassType.isArray()) {
            int lengthUnknownArray = Array.getLength(fieldInObject); // Получить массив из Object
            if (lengthUnknownArray != 0) { // Если массив не пустой, то вычислить, примитивный ли это массив или нет
                String[] elementsFromArray = getValuesFromPrimitiveOrNotPrimitiveArray(fileName, fieldInObject, lengthUnknownArray);
                if (elementsFromArray != null) {
                    if (rowForSaveToCsv != null) {
                        rowForSaveToCsv.add(String.join(DOT, elementsFromArray) + SEMICOLON);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private String[] getValuesFromPrimitiveOrNotPrimitiveArray(String fileName, Object fieldInObject, int lengthUnknownArray) {
        String[] elementsFromArray = null;
        Object elementArrayFromArray = Array.get(fieldInObject, 0); // Достать первый элемент из массива
        Class<?> elementArrayClassType = elementArrayFromArray.getClass();
        if (elementArrayClassType.isPrimitive()) { // Элемент - примитивный тип?
            elementsFromArray = new String[lengthUnknownArray];
            for (int a = 0; a < lengthUnknownArray; a++) {
                elementsFromArray[a] = Array.get(fieldInObject, a).toString();
            }
        } else {
            for (int b = 0; b < lengthUnknownArray; b++) {
                Object objectFromFieldWithArrayOfObjects = Array.get(fieldInObject, b);
                List<String> localListWithRow = getFieldsObject(List.of(objectFromFieldWithArrayOfObjects), fileName, null);
                if (localListWithRow != null && !localListWithRow.isEmpty()) {
                    elementsFromArray = new String[localListWithRow.size()];
                    for (int c = 0; c < lengthUnknownArray; c++) {
                        elementsFromArray[c] = localListWithRow.get(c);
                    }
                }
            }
        }
        return elementsFromArray;
    }

    private void writeAppendedInfoToFile(List<?> data, String fileName) {
        String localFileName;
        if (fileName != null) {
            localFileName = fileName;
        } else {
            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(Y_M_D_H_MM_SS_FORMAT));
            localFileName = new Faker().file().fileName() + currentDateTime + CSV_TYPE;
        }
        try (FileWriter fileWriter = new FileWriter(localFileName, true)) {

        } catch (IOException e) {
            log.error("Возникла ошибка: " + e);
        }
    }
}