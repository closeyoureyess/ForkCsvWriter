package org.writer.myutils.actions;

import org.writer.myutils.other.exceptions.EntitiesForParseNotFoundExceptions;
import org.writer.myutils.other.exceptions.InvalidFormatFileException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс, предоставляющий методы для взаимодействия с полями, объектами полей
 */
public interface DataActions {

    /**
     * Метод, проверяющий формат в передаваемом значении файлаа
     *
     * @param filename Имя файла с расширением
     */
    void checkFormatFile(String filename) throws InvalidFormatFileException;

    /**
     * Метод, позволяющий создать Map с объектом в качестве ключа и массивом полей, которые есть в объекте, в качестве значения
     *
     * @param objectsForCsv Список объектов для формирования отчета
     * @return Map с объектами в качестве ключа и массивом полей этого объекта в значении
     */
    Map<Object, Field[]> createMapWithObjectAndArrayFields(List<?> objectsForCsv);

    /**
     * Метод, позволяющий у одного объекта-примера проверить наличие аннотации, которой объект помечается как пригодный для формирования отчета
     *
     * @param data Список объектов для формирования отчета
     * @param typeAnnotation  Аннотация, наличие которой пытаемся проверить
     * @param <T> Тип аннотации
     */
    <T> void filterAnnotationInputList(List<?> data, T typeAnnotation) throws EntitiesForParseNotFoundExceptions;

}
