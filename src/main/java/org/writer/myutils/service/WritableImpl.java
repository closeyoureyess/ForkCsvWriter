package org.writer.myutils.service;

import lombok.extern.slf4j.Slf4j;
import org.writer.myutils.actions.DataActions;
import org.writer.myutils.actions.DataActionsImpl;
import org.writer.myutils.annotations.CsvClass;
import org.writer.myutils.other.exceptions.EntitiesForParseNotFoundExceptions;
import org.writer.myutils.other.exceptions.InvalidFormatFileException;
import org.writer.myutils.other.exceptions.VariableNotExistsException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static org.writer.myutils.other.ConstantsClass.*;
import static org.writer.myutils.other.exceptions.DescriptionUserExeption.LOCAL_VARIABLE_IS_EMPTY;

/**
 * Класс содержит методы, позволяющие записать информацию в файл с расширением .csv
 * <p>
 * Алгоритм работы методов такой:
 * <p>
 * 1.Вызывается метод writeToFile()
 * 2.Класс, которым наполнен список, который передается для записи в отчет, проверяется на наличие аннотации @CsvClass, которая помечает класс
 * как сущность, с которой будет работать библиотека
 * 3.Проверяется, корректный ли передан формат файла
 * 4.Вызывается метод createMapWithObjectAndArrayFields(), который вычленяет для каждого класса с помощью рефлексии поля, помещает информацию в
 * Map, в виде объект - массив полей
 * 5.Вызывается метод iterableObjectAndHisFields(), для перебора: 1 объект - все его поля, перебор осуществляется с целью получить значения этих
 * полей, корректно записать их в csv
 * 6.Для каждого поля, которое перебирается во вложенном цикле, вызывается метод determineTypeField(), чтобы определить, какого типа переданное поле
 * и как с ним далее работать
 * <p>
 * А)Для простых типов:
 * 7.Если выясняется, что объект-поле  - это простой тип(Integer, Enum и т.д) - преобразовать его в String, записать в локальный List, из которого
 * далее будет осуществлять запись информации в csv-файл
 * <p>
 * Б)Для массивов:
 * 8.При идентификации поля как массива, вызывается метод iterableItemsArrayField() для того, чтобы понять, из каких элементов он состоит и для
 * перебора значений массива. После того, как это будет выполнено, результирующий массив String[], состоящий из элементов перебранного массива,
 * будет записан в локальный List
 * <p>
 * 9. В методе iterableItemsArrayField(), из объекта, который, как выяснилось, является массивом, достается первый элемент. Этот элемент с помощью
 * метода detectArrayPrimitiveOrNotPrimitive() проверяется на то, к какому типу он относится - Integer, Enum и т.д, или это объект
 * -Если это простой тип(метод detectArrayPrimitiveOrNotPrimitive() возвращает true), тогда происходит получение элементов массива в цикле,
 * перевод их в String, запись уже строк в массив String[] для того, чтобы в поле iterableItemsArrayField() произвести запись в List-результат
 * -Если это объект - в цикле полученные объекты по одому передаются в метод callRecursionForHandleObjectArray(), который получает массив полей
 * для этого объекта, записывает их в Map в виде: переданный объект, массив его полей. Далее, рекурсивно вызывается метод из пункта 5 iterableObjectAndHisFields()
 * для того, чтобы проанализировать поля этого объекта, записать их в List-результат. После того, как заканчиваются рекурсии и цикл, метод iterableItemsArrayField()
 * возвращает Optional с null внутри. В методе determineTypeField() этот null корректно обрабатывается, некорректная запись null в локальный
 * List с данными не происходит
 * <p>
 * В)Для коллекций:
 * 10.При идентификации поля как коллекции, вызывается метод iterableItemsCollectionField() для того, чтобы понять, что именно это за реализация коллекции
 * 11.Если выясняется, что это, например, List, вызывается handleItemsListField(), который выясняет, состоит ли коллекция из простых типов или
 * объектов, если из объекто - вызывается рекурсия, в которую передается вся коллекция и несколько значений флагов, которые нужны для того, чтобы
 * не были вызваны некоторые действия(например, проставление каретки) при вызове рекурсии, а не основного потока выполнения
 * 11.С отличием от остальных реализован метод handleItemsMapField(), он использует handleItemsListField() для идентификации, является ли ключ
 * и значение объектами, либо же простыми типами. Если ключ, либо значение - простые типы, они возвращаются в виде массива String[], если что-то
 * из этого объекты, в методе handleItemsListField() они перебираются по аналогии с тем, как это описано выше, информация записывается в List
 * localListDataForMap
 * После того, как перебор окончен, вызывается метод glueMapStringArrayKeyValue(), который производит запись элементов в массив-результат, т.е
 * например - индекс 0 - ключ, индекс 1 - значение
 * <p>
 * Г)Для объектов:
 * 12.Если поле не принадлежит ни к одному из типов и понятно, что это объект,, в последней ветке else в методе determineTypeField(),
 * рекурсирвно вызывается метод iterableObjectAndHisFields() из п.5 для перебора всех полей объекта, записи их в List-результат
 * <p>
 * 13.Пункт 5 - после того, как перебор очередных полей очередного объекта закончен, если есть нужные флаги(основной смысл, что метод должен
 * срабатывать только тогда, когда это основной поток, а не внутреннии рекурсии), вызывается метод addSemicolonOrCarriageForResultRow(), который
 * проставляет символ ';' или перенос строки '\n' в последний элемент из локального List
 * <p>
 * 14.Когда все объекты проитерированы, в методе writeToFile() вызывается метод writeDataToCsv, который записывает информацию в csv-файл
 */
@Slf4j
public class WritableImpl implements Writable {

    private DataActions dataActions = new DataActionsImpl();
    private List<String> listData = new ArrayList<>();
    private List<String> localListDataForMap = new ArrayList<>();

    @Override
    public void writeToFile(List<?> data, String fileName) {
        log.info("start");
        try {
            dataActions.filterAnnotationInputList(data, CsvClass.class);
            dataActions.checkFormatFile(fileName);
            Map<Object, Field[]> mapMapWithObjectAndArrayFields = dataActions.createMapWithObjectAndArrayFields(data);
            iterableObjectAndHisFields(mapMapWithObjectAndArrayFields, ZERO, TWO_INT_ARRAY_AND_COLLECTION_RECURSIONS);
            writeDataToCsv(listData, fileName);
        } catch (IllegalAccessException | IOException | EntitiesForParseNotFoundExceptions |
                 InvalidFormatFileException |
                 VariableNotExistsException e) {
            log.error("Возникла ошибка: " + e);
        }
    }

    /**
     * Метод для записи собранной информации в файл
     *
     * @param data     List со строкой, готовой для записи
     * @param fileName Имя файла, в который будет записана информация
     * @throws IOException
     */
    private void writeDataToCsv(List<String> data, String fileName) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            for (String row : data) {
                bw.append(row);
            }
        }
        if (!data.isEmpty()) {
            data.clear();
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
    private void iterableObjectAndHisFields(Map<Object, Field[]> mapMapWithObjectAndArrayFields, Integer recursionFlag,
                                            Integer additionalRecursionFlag) throws IllegalAccessException, VariableNotExistsException {
        for (Map.Entry<Object, Field[]> entry : mapMapWithObjectAndArrayFields.entrySet()) {
            Object key = entry.getKey(); // Класс
            Field[] value = entry.getValue(); // Массив его полей
            for (Field field : value) { // Взять одно поле из массива, т.е одно поле из класса
                field.setAccessible(true);
                Class<?> typeClassField = field.getType();
                Object classFieldObject = field.get(key);
                determineTypeField(typeClassField, classFieldObject, recursionFlag); // Какого типа это поле?
            }
            if (additionalRecursionFlag != null && additionalRecursionFlag.equals(TWO_INT_ARRAY_AND_COLLECTION_RECURSIONS)
                    && recursionFlag.equals(ZERO)) {
                String lastElement = removeExcessCommaFromLastElement(listData);
                addSemicolonOrCarriageForResultRow(listData, lastElement);
            }
        }
    }

    /**
     * Метод, проставляющий точку с запятой, каретку в конец строки
     *
     * @param listWithData List с информацией, полученной из проверенных объектов
     */
    private void addSemicolonOrCarriageForResultRow(List<String> listWithData, String lastElement) {
        if (lastElement != null && lastElement.contains(SEMICOLON) && !lastElement.contains(CARRIAGE)) {
            listWithData.set(listWithData.size() - 1, lastElement + CARRIAGE);
        } else if (lastElement != null && !lastElement.contains(SEMICOLON)) {
            listWithData.set(listWithData.size() - 1, lastElement + SEMICOLON + CARRIAGE);
        }
    }

    /**
     * Метод, удаляющий лишнюю запятую из последнего элемента List
     *
     * @param listWithData List с информацией, полученной из проверенных объектов
     * @return String с удаленной лищней запятой из последнего элемента
     */
    private String removeExcessCommaFromLastElement(List<String> listWithData) {
        String lastElement = listWithData.get(listWithData.size() - 1);
        if (lastElement != null && lastElement.contains(COMMA)) {
            int lengthLast = lastElement.length();
            if (lengthLast > 1) {
                int lastIndex;
                int lastIndex2;
                if (lastElement.contains(SEMICOLON)) {
                    lastIndex = lengthLast - 2;
                } else {
                    lastIndex = lengthLast - 1;
                }
                lastIndex2 = lastElement.lastIndexOf(COMMA);
                if (lastIndex == lastIndex2) {
                    lastElement = new StringBuilder(lastElement).deleteCharAt(lastIndex).toString();
                }
            }
        }
        return lastElement;
    }

    /**
     * Метод, позволяющий определить тип переданного поля и записать значените, полученное из поля, в локальный List, для последующей записи
     * в файл
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
            if (recursionFlag == null) {
                listData.add(classFieldObject + COMMA);
            } else if (recursionFlag.equals(ZERO)) {
                listData.add(classFieldObject + SEMICOLON);
            } else if (recursionFlag.equals(ONE_INT_MAP_RECURSION)) { // Если перебирается элемент из Map
                localListDataForMap.add(classFieldObject.toString()/* + COMMA*/);
            }
        } else if (typeClassField.isArray()) { // Объект - массив?
            arrayWithItems = iterableItemsArrayField(classFieldObject, recursionFlag); // Определить тип объектов в массиве, перебрать массив
            arrayWithItems.ifPresent(items -> {
                String resultRow;
                if (recursionFlag == null) {
                    resultRow = String.join(COMMA, items) + COMMA;
                    listData.add(resultRow);
                } else if (recursionFlag.equals(ZERO)) {
                    resultRow = String.join(COMMA, items) + SEMICOLON;
                    listData.add(resultRow);
                } else if (recursionFlag.equals(ONE_INT_MAP_RECURSION)) { // Если перебираются ключ/значение Map
                    resultRow = String.join(COMMA, items)/* + SEMICOLON*/;
                    localListDataForMap.add(resultRow);
                }
            });
        } else if (typeClassField.isInterface()) { // Объект - интерфейс-коллекция?
            arrayWithItems = iterableItemsCollectionField(classFieldObject, recursionFlag); // Определить тип объектов в коллекции, перебрать коллекцию
            arrayWithItems.ifPresent(strings -> {
                String resultRow = String.join(COMMA, strings);
                if (recursionFlag.equals(ZERO)) {
                    listData.add(resultRow);
                } else if (recursionFlag.equals(ONE_INT_MAP_RECURSION)) { /// Если перебираются ключ/значение Map
                    localListDataForMap.add(resultRow);
                }
            });
        } else { // Объект - пользовательский класс?
            List<?> listWithObject = List.of(classFieldObject); // Поместить объект в List
            iterableObjectAndHisFields(dataActions.createMapWithObjectAndArrayFields(listWithObject), recursionFlag, null); // Начать перебор полей объекта
        }
    }

    /**
     * Метод, позволяющий определить, к какому именно типу коллекций относится переданный объект и перебрать элементы коллекции
     *
     * @param collectionObject Объект-поле, являющийся коллекцией
     * @param recursionFlag    Флаг для рекурсии
     * @return Optional с массивом перебранных элементов коллекции
     * @throws IllegalAccessException
     * @throws VariableNotExistsException
     */
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

    /**
     * Метод, позволяющий перебрать элементы переданной коллекции List, собрать результаты в массив String[], если элементы простые, если объекты -
     * запустить рекурсию
     *
     * @param listFromField Коллекция List, которую необходимо перебрать
     * @param recursionFlag Флаг рекурсии
     * @return Массив перебранных элементов List
     * @throws IllegalAccessException
     * @throws VariableNotExistsException
     */
    private String[] handleItemsListField(List<?> listFromField, Integer recursionFlag) throws
            IllegalAccessException, VariableNotExistsException {
        Object objectFromField = listFromField.get(ZERO);
        String[] elementsFromArray = null;
        boolean typeMyList = detectArrayPrimitiveOrNotPrimitive(objectFromField);
        if (typeMyList) {
            elementsFromArray = listFromField.stream().map(Object::toString).toArray(String[]::new);
        } else {
            callRecursionForHandleObjectArray(listFromField, recursionFlag, null);
        }
        return elementsFromArray;
    }

    /**
     * Метод, позволяющий перебрать элементы переданной коллекции Set, собрать результаты в массив String[], если элементы простые, если объекты -
     * запустить рекурсию
     *
     * @param setFromField  Коллекция Set, которую необходимо перебрать
     * @param recursionFlag Флаг рекурсии
     * @return Массив перебранных элементов Set
     * @throws IllegalAccessException
     * @throws VariableNotExistsException Эксепшен, который может быть выброшен, если коллекция Set пуста
     */
    private String[] handleItemsSetField(Set<?> setFromField, Integer recursionFlag) throws
            IllegalAccessException, VariableNotExistsException {
        Optional<?> optionalObject = setFromField.stream().findFirst();
        Object objectFromField;
        String[] elementsFromArray = null;
        if (optionalObject.isPresent()) {
            objectFromField = optionalObject.get();
        } else {
            throw new VariableNotExistsException(LOCAL_VARIABLE_IS_EMPTY.getEnumDescription());
        }
        boolean typeMySet = detectArrayPrimitiveOrNotPrimitive(objectFromField);
        if (typeMySet) {
            elementsFromArray = setFromField.stream().map(Object::toString).toArray(String[]::new);
        } else {
            List<?> listFromField = setFromField.stream().toList();
            callRecursionForHandleObjectArray(listFromField, recursionFlag, null);
            /*elementsFromArray = new String[setFromField.size()];
            for (int a = 0; a < elementsFromArray.length; a++) {
                Object arraysObject = listFromField.get(a); // Получить 1 объект из листа
                List<?> listWithListsObject = List.of(arraysObject); // Сделать List с объектом
                callRecursionForHandleObjectArray(listWithListsObject, recursionFlag, null);
            }*/
        }
        return elementsFromArray;
    }

    /**
     * Метод, позволяющий достать содержимое из Map, ключи и значения записать в массив String[], в виде индекс 0 - ключ, индекс 1 - значение
     *
     * @param mapFromField Map, являющийся полем в классе
     * @return Массив строк с перебранными элементами массива
     * @throws IllegalAccessException
     * @throws VariableNotExistsException
     */
    private String[] handleItemsMapField(Map<?, ?> mapFromField) throws
            IllegalAccessException, VariableNotExistsException {
        var listWithKeys = new ArrayList<>();
        var listWithValues = new ArrayList<>();
        Object key;
        Object value;
        String[] arrayWithKey = null;
        String[] arrayWithValue = null;
        String[] arrayWithGlueKeyValue = null;
        for (Map.Entry<?, ?> mapEntry : mapFromField.entrySet()) {
            key = mapEntry.getKey();
            value = mapEntry.getValue();
            listWithKeys.add(key);
            listWithValues.add(value);
        }
        arrayWithKey = handleItemsListField(listWithKeys, ONE_INT_MAP_RECURSION); // Разобрать ключ(примитив или объект, если второе - пройтись по полям
        arrayWithValue = handleItemsListField(listWithValues, ONE_INT_MAP_RECURSION); // Разобрать значениe

        arrayWithGlueKeyValue = glueMapStringArrayKeyValue(arrayWithKey, arrayWithValue, localListDataForMap);
        return arrayWithGlueKeyValue;
    }

    /**
     * Метод, склеивающий массив строк с ключами и значениями в единный массив строк, либо же достающий из локального List записанные туда
     * значения, которые также записываются в массив-результат
     *
     * @param arrayWithKey   Массив String с ключами из первоначальной Map
     * @param arrayWithValue Массив String со значениями из первоначальной Map
     * @param listDataForMap List для записи ключа/значения(либо и тех, и других вместе), если ключ и значение - не простые типы, а объекты
     * @return
     */
    private String[] glueMapStringArrayKeyValue(String[] arrayWithKey, String[] arrayWithValue, List<String> listDataForMap) {
        String[] arrayWithGlueKeyValue = null;
        int usualCount = 0;
        int countKey = 0;
        int countValue = 1;
        if (!listDataForMap.isEmpty()) { // List  для записи ключа/значения не пустой? Если да, это означает, что в процессе рекурсии туда были записаны значения
            if (arrayWithKey != null && arrayWithValue == null) { // Массив строк arrayWithKey - это означает, что ключ в Map - простой тип. Массив строк arrayWithValue пустой - это означает, что значение состоит из объектов
                arrayWithGlueKeyValue = new String[arrayWithKey.length + listDataForMap.size()];
                while (usualCount < arrayWithKey.length) {
                    arrayWithGlueKeyValue[countKey] = arrayWithKey[usualCount];
                    arrayWithGlueKeyValue[countValue] = listDataForMap.get(usualCount); // Записать значения, собранные в List, в массив строк
                    countKey++;
                    countValue++;
                    usualCount++;
                }
            } else if (arrayWithKey == null && arrayWithValue != null) { // То же, что описано выше, но наоборот - значения arrayWithValue - простые типы, arrayWithKey - объекты
                arrayWithGlueKeyValue = new String[arrayWithValue.length + listDataForMap.size()];
                while (usualCount < arrayWithValue.length) {
                    arrayWithGlueKeyValue[countKey] = listDataForMap.get(usualCount);
                    arrayWithGlueKeyValue[countValue] = arrayWithValue[usualCount];
                    countKey++;
                    countValue++;
                    usualCount++;
                }
            } else {
                arrayWithGlueKeyValue = new String[listDataForMap.size()];
                usualCount = 1;
                while (usualCount < listDataForMap.size()) {
                    arrayWithGlueKeyValue[countKey] = listDataForMap.get(countKey);
                    arrayWithGlueKeyValue[countValue] = listDataForMap.get(countValue);
                    countKey++;
                    countValue++;
                    usualCount++;
                }
            }
        } else {
            if (arrayWithKey != null && arrayWithValue != null) { // Массивы с ключами, значениями не пустые? Это означает, что ключи из значения в этой Map - простые типы
                arrayWithGlueKeyValue = new String[arrayWithKey.length + arrayWithValue.length];
                while (usualCount < arrayWithKey.length) {
                    arrayWithGlueKeyValue[countKey] = arrayWithKey[usualCount];
                    arrayWithGlueKeyValue[countValue] = arrayWithValue[usualCount];
                    countKey++;
                    countValue++;
                    usualCount++;
                }
            }
        }
        listDataForMap.clear();
        return arrayWithGlueKeyValue; // Массив строк, состоящий из пар ключ-значение(Пример - индекс 0 ключ, индекс 1 значение)
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
                    callRecursionForHandleObjectArray(listWithArraysObject, null, null); // Разобрать объект, т.е его поля
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
    private void callRecursionForHandleObjectArray(List<?> listWithObjectFromArrayOrList, Integer recursionFlag, Integer additionalRecursionFlag) throws
            IllegalAccessException, VariableNotExistsException {
        Map<Object, Field[]> mapMapWithObjectAndArrayFields = dataActions.createMapWithObjectAndArrayFields(listWithObjectFromArrayOrList); // Сделать Map с объектом и массивом его полей
        iterableObjectAndHisFields(mapMapWithObjectAndArrayFields, recursionFlag, additionalRecursionFlag); // Вызвать рекурсию, чтобы разобрать каждый объект
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